package com.example.demo.config.write;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.demo.config.read.User;

@Component
public class UsernameUpperProcessor implements ItemProcessor<User, User> {
	@Override
	public User process(User item) throws Exception {
		System.out.println("UsernameUpperProcessor: " + item);

		User user = new User();

		user.setId(item.getId());
		user.setUsername(item.getUsername().toUpperCase());
		user.setPassword(item.getPassword());
		user.setAge(item.getAge());

		return user;
	}
}
