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
public class RetryDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job retryDemoJob() {
		return jobBuilderFactory.get("retryDemoJob")
				.start(retryDemoStep())
				.build();
	}

	@Bean
	public Step retryDemoStep() {
		return stepBuilderFactory.get("retryDemoStep")
				.<String, String>chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				// retry 的使用方式
				.faultTolerant() // 先開啟容錯
				.retry(CustomRetryException.class) // 指明發生什麼樣的異常時進行重試
				.retryLimit(5) // 指定重試次數，超過就不會再重試 (次數代表 reader + processor + writer 出錯的總次數)
				.build();
	}

	@Bean
	@StepScope
	public ListItemReader<String> reader() {
		List<String> items = new ArrayList<>();

		for (int i = 0; i < 60; i++) {
			items.add(String.valueOf(i));
		}

		return new ListItemReader<>(items);
	}

	@Bean
	@StepScope
	public ItemWriter<String> writer() {
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
	public ItemProcessor<String, String> processor() {
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
