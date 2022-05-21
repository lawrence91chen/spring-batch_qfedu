package com.example.demo.config.write;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.read.Customer;

@Configuration
@EnableBatchProcessing
public class ItemWriterDbDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("flatFileReader2")
	private ItemReader<Customer> flatFileReader;

	@Autowired
	@Qualifier("itemWriterDb")
	private ItemWriter<? super Customer> itemWriterDb;

	@Bean
	public Job itemWriterDbDemoJob() {
		return jobBuilderFactory.get("itemWriterDbDemoJob")
				.start(itemWriterDbDemoStep())
				.build();
	}

	@Bean
	public Step itemWriterDbDemoStep() {
		return stepBuilderFactory.get("itemWriterDbDemoStep")
				.<Customer, Customer>chunk(5)
				.reader(flatFileReader)
				.writer(itemWriterDb)
				.build();
	}
}