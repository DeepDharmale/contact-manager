package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entites.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	// pagination

    @Query("from Contact c where c.user.id = :userId")
    //current page-page
    //contact per page-size -5
    public  Page<Contact> findByUser(@Param("userId") int userId,Pageable pageable);
    
}
