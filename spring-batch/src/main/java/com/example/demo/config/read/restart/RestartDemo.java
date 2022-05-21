package com.example.demo.config.read.restart;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.read.Customer;

@Configuration
public class RestartDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("restartReader")
	private ItemStreamReader<? extends Customer> restartReader;
	@Autowired
	@Qualifier("restartWriter")
	private ItemWriter<? super Customer> restartWriter;

	@Bean
	public Job restartDemoJob() {
		return jobBuilderFactory.get("restartDemoJob")
				.start(restartDemoStep())
				.build();
	}

	@Bean
	public Step restartDemoStep() {
		return stepBuilderFactory.get("restartDemoStep")
				.<Customer, Customer>chunk(10)
				.reader(restartReader)
				.writer(restartWriter)
				.build();
	}
}
