package com.example.demo.config.write;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.example.demo.config.read.User;

@Configuration
public class ItemWriterXmlConfig {
	@Bean
	public StaxEventItemWriter<User> xmlItemWriter() throws Exception {
		StaxEventItemWriter<User> writer = new StaxEventItemWriter<>();
		XStreamMarshaller marshaller = new XStreamMarshaller();

		// 告訴 marshaller 把數據轉成什麼類型
		Map<String, Class<?>> map = new HashMap<>();
		map.put("user", User.class);
		marshaller.setAliases(map);

		writer.setRootTagName("users");
		writer.setMarshaller(marshaller);

		String path = "D:\\user.xml";
		writer.setResource(new FileSystemResource(path));
		writer.afterPropertiesSet();

		return writer;
	}
}
