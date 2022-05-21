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
public class ItemWriterFileDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("dbJdbcReader2")
	private ItemReader<? extends User> dbJdbcReader;
	@Autowired
	@Qualifier("fileItemWriter")
	private ItemWriter<? super User> fileItemWriter;

	@Bean
	public Job itemWriterFileDemoJob() {
		return jobBuilderFactory.get("itemWriterFileDemoJob")
				.start(itemWriterFileDemoStep())
				.build();
	}

	@Bean
	public Step itemWriterFileDemoStep() {
		return stepBuilderFactory.get("itemWriterFileDemoStep")
				.<User, User>chunk(5)
				.reader(dbJdbcReader)
				.writer(fileItemWriter)
				.build();
	}
}
