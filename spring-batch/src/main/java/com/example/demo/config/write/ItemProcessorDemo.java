package com.example.demo.config.write;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.read.User;

@Configuration
public class ItemProcessorDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("dbJdbcReader2")
	private ItemReader<? extends User> dbJdbcReader;
	@Autowired
	private ItemWriter<? super User> fileItemWriter;

	@Autowired
	private ItemProcessor<User, User> usernameUpperProcessor;
	@Autowired
	private ItemProcessor<User, User> idFilterProcessor;

	@Bean
	public Job itemProcessorDemoJob() {
		return jobBuilderFactory.get("itemProcessorDemoJob")
				.start(itemProcessorDemoStep())
				.build();
	}

	@Bean
	public Step itemProcessorDemoStep() {
		return stepBuilderFactory.get("itemProcessorDemoStep")
				.<User, User>chunk(5)
				.reader(dbJdbcReader)
//				.processor(usernameUpperProcessor) // 當只有一種處理方式時
				.processor(process())
				.writer(fileItemWriter)
				.build();
	}

	// 同時有多種處理數據的方式
	@Bean
	public CompositeItemProcessor<User, User> process() {
		CompositeItemProcessor<User, User> processor = new CompositeItemProcessor<>();

		List<ItemProcessor<User, User>> delegates = new ArrayList<>();
		delegates.add(usernameUpperProcessor);
		delegates.add(idFilterProcessor);
		processor.setDelegates(delegates);

		return processor;
	}
}