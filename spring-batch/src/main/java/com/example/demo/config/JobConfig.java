package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobConfig {
	// 注入創建任務對象的對象。
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	// 任務的執行由 Step 決定。 (一個任務可以包含多個 step)
	// 注入創建 Step 對象的對象。
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	/**
	 * 創建任務對象。
	 */
	@Bean
	public Job helloWorldJob() {
		return jobBuilderFactory
				// 任務名稱: helloWorldJob
				.get("helloWorldJob")
				// 指定任務開始執行的 step
				.start(step1())
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory
				// Step 名稱: step1
				.get("step1")
				// 指定 step 實現的功能 (tasklet 或 chunk)
				.tasklet(new Tasklet() {

					@Override
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
							throws Exception {
						// Step 具體的執行內容
						System.out.println("Hello World!");

						// 指定 Step 執行完後的狀態。 (後面可能還有其他 step，所以前一個 step 的狀態可能會決定後面會不會繼續執行)
						return RepeatStatus.FINISHED;
					}
				}).build();
	}
}
