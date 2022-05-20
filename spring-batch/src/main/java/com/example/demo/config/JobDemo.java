package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job jobDemoJob() {
		return jobBuilderFactory.get("jobDemoJob")
//				.start(jobDemoJobStep1())
//				.next(jobDemoJobStep2())
//				.next(jobDemoJobStep3())
//				.build();

				// on、from、to、end
				// fail、stopAndRestart
				.start(jobDemoJobStep1())
				.on("COMPLETED").to(jobDemoJobStep2())
				.from(jobDemoJobStep2()).on("COMPLETED").to(jobDemoJobStep3())
				.from(jobDemoJobStep3()).end()
				.build();
	}

	@Bean
	public Step jobDemoJobStep1() {
		return stepBuilderFactory.get("jobDemoJobStep1").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("jobDemoJobStep1");

				return RepeatStatus.FINISHED;
			}
		}).build();
	}

	@Bean
	public Step jobDemoJobStep2() {
		return stepBuilderFactory.get("jobDemoJobStep2").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("jobDemoJobStep2");

				return RepeatStatus.FINISHED;
			}
		}).build();
	}

	@Bean
	public Step jobDemoJobStep3() {
		return stepBuilderFactory.get("jobDemoJobStep3").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("jobDemoJobStep3");

				return RepeatStatus.FINISHED;
			}
		}).build();
	}
}
