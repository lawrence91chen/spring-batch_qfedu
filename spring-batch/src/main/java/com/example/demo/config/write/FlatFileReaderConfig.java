package com.example.demo.config.write;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;

import com.example.demo.config.read.Customer;

@Configuration
public class FlatFileReaderConfig {
	@Bean("flatFileReader2")
	public FlatFileItemReader<Customer> flatFileReader() {
		FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
		// 指定 classpath 下的文件檔進行讀取
		reader.setResource(new ClassPathResource("customer.txt"));
		// 跳過第一行表頭
		reader.setLinesToSkip(1);
		// 數據解析
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		// 指名表頭字段
		tokenizer.setNames("id", "firstName", "lastName", "birthday");

		// 把解析出的一行數據映射為 Customer 對象
		DefaultLineMapper<Customer> mapper = new DefaultLineMapper<>();
		mapper.setLineTokenizer(tokenizer);
		mapper.setFieldSetMapper(new FieldSetMapper<Customer>() {
			@Override
			public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
				Customer customer = new Customer();
				customer.setId(fieldSet.readLong("id"));
				customer.setFirstName(fieldSet.readString("firstName"));
				customer.setLastName(fieldSet.readString("lastName"));
				customer.setBirthday(fieldSet.readString("birthday"));

				return customer;
			}
		});

		mapper.afterPropertiesSet(); // 檢查
		reader.setLineMapper(mapper);

		return reader;
	}
}
