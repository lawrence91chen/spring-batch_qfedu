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
public class SkipDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job skipDemoJob() {
		return jobBuilderFactory.get("skipDemoJob")
				.start(skipDemoStep())
				.build();
	}

	@Bean
	public Step skipDemoStep() {
		return stepBuilderFactory.get("skipDemoStep")
				.<String, String>chunk(10)
				.reader(skipReader())
				.processor(skipProcessor())
				.writer(skipWriter())
				// skip 的使用方式
				.faultTolerant() // 先開啟容錯
				.skip(CustomRetryException.class) // 發生什麼樣的異常時進行跳過
				.skipLimit(5) // 指定跳過次數，超過就不會再跳過
				.build();
	}

	@Bean
	@StepScope
	public ListItemReader<String> skipReader() {
		List<String> items = new ArrayList<>();

		for (int i = 0; i < 60; i++) {
			items.add(String.valueOf(i));
		}

		return new ListItemReader<>(items);
	}

	@Bean
	@StepScope
	public ItemWriter<String> skipWriter() {
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
	public ItemProcessor<String, String> skipProcessor() {
		return new ItemProcessor<String, String>() {
			private int attempCount = 0;

			@Override
			public String process(String item) throws Exception {
				System.out.println("processing item: " + item);

				if (item.equalsIgnoreCase("26")) {
					attempCount++;

					if (attempCount >= 3) {
						System.out.println("Retried [" + attempCount + "] times success.");
						return String.valueOf(Integer.valueOf(item) * -1);
					} else {
						System.out.println("Processed the [" + attempCount + "] times fail.");
						throw new CustomRetryException("Process failed. Attempt: " + attempCount);
					}

				} else {
					return String.valueOf(Integer.valueOf(item) * -1);
				}
			}
		};
	}
}
