package com.example.demo.config.write;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.read.User;

@Configuration
public class ItemWriterXmlDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("dbJdbcReader2")
	private ItemReader<? extends User> dbJdbcReader;
	@Autowired
	@Qualifier("xmlItemWriter")
	private ItemWriter<? super User> xmlItemWriter;

	@Bean
	public Job itemWriterXmlDemoJob() {
		return jobBuilderFactory.get("itemWriterXmlDemoJob")
				.start(itemWriterXmlDemoStep())
				.build();
	}

	@Bean
	public Step itemWriterXmlDemoStep() {
		return stepBuilderFactory.get("itemWriterXmlDemoStep")
				.<User, User>chunk(5)
				.reader(dbJdbcReader)
				.writer(xmlItemWriter)
				.build();
	}
}
