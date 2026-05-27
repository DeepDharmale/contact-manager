package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entites.Contact;
import com.smart.entites.User;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model m, Principal principal) {

        String userName = principal.getName();

        User user = userRepository.getUserByUserName(userName);

        m.addAttribute("user", user);
    }

    // dashboard Home
    @RequestMapping("/index")
    public String dashboard(Model model) {

        model.addAttribute("title", "User dashboard");

        return "normal/user_dashboard";
    }

    // open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {

        model.addAttribute("title", "Add Contact");

        model.addAttribute("contact", new Contact());

        return "normal/add_contact_form";
    }

    // processing add contact form

    @PostMapping("/process-contact")
    public String processContact(
            @ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile file,
            Principal principal) {

        try {

            String name = principal.getName();

            User user = this.userRepository.getUserByUserName(name);

            contact.setUser(user);

            user.getContact().add(contact);

            this.userRepository.save(user);

            System.out.println("Added to database");

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "redirect:/user/add-contact";
    }

}