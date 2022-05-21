package com.example.demo.config.write;

import javax.sql.DataSource;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.read.Customer;

@Configuration
public class ItemWriterDbConfig {
	@Autowired
	private DataSource dataSource;

	@Bean
	public JdbcBatchItemWriter<Customer> itemWriterDb() {
		JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();

		writer.setDataSource(dataSource);
		writer.setSql(
				"insert into customer(firstName, lastName, birthday) values (:firstName, :lastName, :birthday)");
		// 將 Customer 中對應屬性的值與 Sql 語句中佔位符的值進行替換
		writer.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<>());

		return writer;
	}
}
