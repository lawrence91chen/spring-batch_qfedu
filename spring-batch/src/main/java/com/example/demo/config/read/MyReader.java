package com.example.demo.config.read;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * 自定義ItemReader
 */
public class MyReader implements ItemReader<String> {
	private Iterator<String> iterator;

	public MyReader(List<String> list) {
		this.iterator = list.iterator();
	}

	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		// 一個數據一個數據讀
		if (iterator.hasNext()) {
			return this.iterator.next();
		} else {
			return null;
		}
	}
}