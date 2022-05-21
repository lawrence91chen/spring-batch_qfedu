package com.example.demo.config.data.restart;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.demo.config.data.Customer;

@Component("restartWriter")
public class RestartWriter implements ItemWriter<Customer> {
	@Override
	public void write(List<? extends Customer> items) throws Exception {
		for (Customer customer : items) {
			System.out.println(customer);
		}
	}
}
