package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entites.Contact;
import com.smart.entites.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	private ContactRepository contactRepository;
	
	//search handler
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query ,Principal pricipal)
	{
		System.out.println(query);
		User user = this.userRepository.getUserByUserName(pricipal.getName());
		List<Contact> contact =  this.contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contact);

}
}
