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
public class ItemWriterMultiFileDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("dbJdbcReader2")
	private ItemReader<? extends User> dbJdbcReader;
	@Autowired
	@Qualifier("multiFileItemWriter")
	private ItemWriter<? super User> multiFileItemWriter;

	@Bean
	public Job itemWriterMultiFileDemoJob() {
		return jobBuilderFactory.get("itemWriterMultiFileDemoJob")
				.start(itemWriterMultiFileDemoStep())
				.build();
	}

	@Bean
	public Step itemWriterMultiFileDemoStep() {
		return stepBuilderFactory.get("itemWriterMultiFileDemoStep")
				.<User, User>chunk(5)
				.reader(dbJdbcReader)
				.writer(multiFileItemWriter)
				.build();
	}
}
