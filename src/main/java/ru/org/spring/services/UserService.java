package ru.org.spring.services;

import ru.org.spring.model.User;
import ru.org.spring.model.Role;
import ru.org.spring.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> index() {

        return entityManager.createQuery("from User", User.class).getResultList();
    }

    @Transactional(readOnly = true)
    public User show(Long id) {
        return entityManager.find(User.class, id);
    }

    @Transactional
    public void save(User person) {
        entityManager.persist(person);
        entityManager.flush();
    }

    @Transactional
    public void update(Long id, User updatedPerson) {
        entityManager.find(User.class, id);
        entityManager.merge(updatedPerson);
        entityManager.flush();
    }

    public User readPerson(Long id) {
        return entityManager.find(User.class, id);
    }

    @Transactional
    public void delete(Long id) {
        User user = readPerson(id);
        entityManager.remove(user);
        entityManager.flush();

    }

    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = findByName(name);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", name));
        }
        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }
}
