package com.example.security1.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security1.model.User;


// CRUD 함수를 JpaRepository가 들고있음.
// @Repository 없어도 IoC됨. 상속받았기 때문
public interface UserRepository extends JpaRepository<User, Integer>{
	
	public User findByUsername(String username);
}
