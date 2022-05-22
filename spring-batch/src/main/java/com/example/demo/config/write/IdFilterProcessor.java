package com.example.demo.config.write;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.demo.config.read.User;

@Component
public class IdFilterProcessor implements ItemProcessor<User, User> {
	@Override
	public User process(User user) throws Exception {
		System.out.println("IdFilterProcessor: " + user);

		if (user.getId() % 2 == 0)
			return user;
		else
			// 相當於把該對象過濾掉
			return null;
	}
}