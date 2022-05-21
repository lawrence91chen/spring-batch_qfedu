package com.example.demo.config.write;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemWriterDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("myWriter")
	private ItemWriter<String> myWriter;

	@Bean
	public Job itemWriterDemoJob() {
		return jobBuilderFactory.get("itemWriterDemoJob")
				.start(itemWriterDemoStep())
				.build();
	}

	@Bean
	public Step itemWriterDemoStep() {
		return stepBuilderFactory.get("itemWriterDemoStep")
				.<String, String>chunk(5)
				.reader(myReader())
				.writer(myWriter)
				.build();
	}

	@Bean
	public ItemReader<String> myReader() {
		List<String> items = new ArrayList<>();

		for (int i = 1; i <= 50; i++) {
			items.add("java-" + i);
		}

		return new ListItemReader<>(items);
	}
}