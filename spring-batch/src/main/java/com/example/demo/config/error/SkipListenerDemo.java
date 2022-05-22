package com.example.demo.config.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkipListenerDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private MySkipListener mySkipListener;

	@Bean
	public Job skipListenerDemoJob() {
		return jobBuilderFactory.get("skipListenerDemoJob")
				.start(skipListenerDemoStep())
				.build();
	}

	@Bean
	public Step skipListenerDemoStep() {
		return stepBuilderFactory.get("skipListenerDemoStep")
				.<String, String>chunk(10)
				.reader(skipListenerReader())
				.processor(skipListenerProcessor())
				.writer(skipListenerWriter())
				.faultTolerant()
				.skip(CustomRetryException.class)
				.skipLimit(10)
				.listener(mySkipListener)
				.build();
	}

	@Bean
	@StepScope
	public ListItemReader<String> skipListenerReader() {
		List<String> items = new ArrayList<>();

		for (int i = 0; i < 60; i++) {
			items.add(String.valueOf(i));
		}

		return new ListItemReader<>(items);
	}

	@Bean
	@StepScope
	public ItemWriter<String> skipListenerWriter() {
		return new ItemWriter<String>() {
			@Override
			public void write(List<? extends String> items) throws Exception {
				for (String item : items) {
					System.out.println("write: " + item);
				}
			}
		};
	}

	@Bean
	@StepScope
	public ItemProcessor<String, String> skipListenerProcessor() {
		return new ItemProcessor<String, String>() {
			@Override
			public String process(String item) throws Exception {
				System.out.println("processing item: " + item);

				if (item.equalsIgnoreCase("26")) {
					throw new CustomRetryException("Process failed of item 26");
				} else {
					return String.valueOf(Integer.valueOf(item) * -1);
				}
			}
		};
	}
}
