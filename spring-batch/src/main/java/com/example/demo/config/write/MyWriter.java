package com.example.demo.config.write;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component("myWriter")
public class MyWriter implements ItemWriter<String> {
	@Override
	public void write(List<? extends String> items) throws Exception {
		// 輸出一批的數量，chunk 的值
		System.out.println(items.size());

		for (String str : items) {
			System.out.println(str);
		}
	}
}