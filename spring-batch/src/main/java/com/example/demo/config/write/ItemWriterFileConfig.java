package com.example.demo.config.write;

import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.demo.config.read.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ItemWriterFileConfig {
	// 向文件輸出數據，覆蓋原先數據
	@Bean
	public FlatFileItemWriter<User> fileItemWriter() throws Exception {
		// 把 User 對象轉成字符串輸出到文件
		FlatFileItemWriter<User> writer = new FlatFileItemWriter<User>();
		String path = "D:\\user.txt";
		writer.setResource(new FileSystemResource(path));

		// 把 User 對象轉成字符串
		writer.setLineAggregator(new LineAggregator<User>() {
			ObjectMapper mapper = new ObjectMapper();

			@Override
			public String aggregate(User user) {
				String str = null;

				try {
					str = mapper.writeValueAsString(user);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}

				return str;
			}
		});

		writer.afterPropertiesSet();

		return writer;
	}
}
