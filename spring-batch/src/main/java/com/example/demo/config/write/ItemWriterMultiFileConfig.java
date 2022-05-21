package com.example.demo.config.write;

import java.util.Arrays;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.read.User;

@Configuration
public class ItemWriterMultiFileConfig {
	@Autowired
	@Qualifier("fileItemWriter")
	private ItemWriter<? super User> fileItemWriter;
	@Autowired
	@Qualifier("xmlItemWriter")
	private ItemWriter<? super User> xmlItemWriter;

	// 調用輸出到單個文件操作來實現輸出數據到多個文件
	@Bean
	public CompositeItemWriter<User> multiFileItemWriter() throws Exception {
		CompositeItemWriter<User> writer = new CompositeItemWriter<>();
		// 輸出到兩個不同的文件中 (實際上是讓多個輸出對象去寫)
		writer.setDelegates(Arrays.asList(fileItemWriter, xmlItemWriter));

		writer.afterPropertiesSet();

		return writer;
	}
}
