package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class NestedDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private JobLauncher launcher;
	@Autowired
	private Job childJobOne;
	@Autowired
	private Job childJobTwo;

	@Bean
	public Job parentJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return jobBuilderFactory.get("parentJob")
				.start(childJob1(jobRepository, transactionManager))
				.next(childJob2(jobRepository, transactionManager))
				.build();
	}

	/**
	 * 返回 Job 類型的 Step (特殊 Step)
	 */
	private Step childJob1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new JobStepBuilder(new StepBuilder("childJob1"))
				.job(childJobOne)
				.launcher(launcher) // 使用啟動父 Job 的啟動對象
				.repository(jobRepository)
				.transactionManager(transactionManager)
				.build();
	}

	private Step childJob2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new JobStepBuilder(new StepBuilder("childJob2"))
				.job(childJobTwo)
				.launcher(launcher)
				.repository(jobRepository)
				.transactionManager(transactionManager)
				.build();
	}
}