package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entites.Contact;
import com.smart.entites.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    // adds common data (logged-in user) to every response
    @ModelAttribute
    public void addCommonData(Model m, Principal principal) {
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);
        m.addAttribute("user", user);
    }

    // dashboard
    @RequestMapping("/index")
    public String dashboard(Model model) {
        model.addAttribute("title", "User dashboard");
        return "normal/user_dashboard";
    }

    // open add contact form
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    // process add contact form
    @PostMapping("/process-contact")
    public String processContact(
            @ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile file,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            if (file.isEmpty()) {
                contact.setImage("contact.png");
            } else {
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath()
                        + File.separator
                        + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            contact.setUser(user);
            user.getContact().add(contact);
            this.userRepository.save(user);

            redirectAttributes.addFlashAttribute("message",
                    new Message("Your contact is added!! Add more...", "success"));

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message",
                    new Message("Something went wrong !! Try again", "danger"));
        }

        return "redirect:/user/add-contact";
    }

    // show contacts with pagination
    @GetMapping("/show-contacts/{page}")
    public String showContact(@PathVariable("page") Integer page, Model m, Principal principal) {
        m.addAttribute("title", "Show User Contacts");

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        PageRequest pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = this.contactRepository.findByUser(user.getId(), pageable);

        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    // show particular contact detail
    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId,
                                    Model model, Principal principal) {

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        Contact contact = this.contactRepository.findById(cId).orElse(null);

        if (contact == null) {
            return "redirect:/user/show-contacts/0";
        }

        // use .equals() to safely compare Integer objects
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
        }

        return "normal/contact_detail";
    }
    
    //delete contact handler
    
    @GetMapping("/delete/{cId}")
    public String deltecontact(@PathVariable("cId") Integer cId, Model model,HttpSession session)
    {
    	Optional<Contact> contactOptional = this.contactRepository.findById(cId);
    	Contact contact = contactOptional.get();
    	
    	
    	//checking if the contact is present or not
    	if(contact == null)
		{
			return "redirect:/user/show-contacts/0";
		}
    	this.contactRepository.delete(contact);
    	
    	session.setAttribute("message", new Message("Contact deleted successfully!!", "success"));
    	
    	
    
    	
    	
    	
    	return "redirect:/user/show-contacts/0";
    }
}