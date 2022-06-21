package ru.org.spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.org.spring.model.User;
import ru.org.spring.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping()
    public String index(Model model) {
        model.addAttribute("users", userService.index());
        return "user-list.html";
    }


    @GetMapping("/user-create")
    public String newPerson(@ModelAttribute("user") User person) {
        return "user-create.html";
    }

    @PostMapping("/user-create")
    public String create(@ModelAttribute("user") User person,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "user-create.html";

        userService.save(person);
        return "redirect:/admin";
    }

    @GetMapping("/user-update/{id}")
    public String edit(Model model, @PathVariable("id") long id) {
        model.addAttribute("user", userService.show(id));
        return "user-update.html";
    }

    @RequestMapping(value = "/user-update/up/{id}", method = RequestMethod.POST)
    public String update(@ModelAttribute("user") User person,
                         @PathVariable("id") long id) {

        userService.update(id, person);
        return "redirect:/admin";
    }

    @RequestMapping(value = "/user-delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}
