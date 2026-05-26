package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entites.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal)
	{
		String userName =principal.getName();
		System.out.println("USERNAME "+userName);
		//get the user using username(Email)
		
		User user = userRepository.getUserByUserName(userName);
		
		System.out.println("USER "+user);
		
		model.addAttribute("user",user);
		
		return "normal/user_dashboard";
	}

}
