package com.example.demo.config.launch;

import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobOperatorDemo implements StepExecutionListener, ApplicationContextAware {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private JobExplorer jobExplorer;
	@Autowired
	private JobRegistry jobRegistry;

	private ApplicationContext context;

	Map<String, JobParameter> parameters;

	@Bean
	public JobRegistryBeanPostProcessor jobRegistrar() throws Exception {
		JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
		postProcessor.setJobRegistry(jobRegistry);
		postProcessor.setBeanFactory(context.getAutowireCapableBeanFactory());
		postProcessor.afterPropertiesSet();

		return postProcessor;
	}

	// JobOperator 需要自行創建
	@Bean
	public JobOperator jobOperator() {
		SimpleJobOperator operator = new SimpleJobOperator();

		operator.setJobLauncher(jobLauncher);
		// 可以把 key=value 的字串轉換成 JobParameters
		operator.setJobParametersConverter(new DefaultJobParametersConverter());
		operator.setJobRepository(jobRepository);
		operator.setJobExplorer(jobExplorer);
		// 將 jobOperator.start 的參數 jobName 和具體的 Job 對象關聯起來
		// 但還需要自己另外創 JobRegistryBeanPostProcessor
		operator.setJobRegistry(jobRegistry);

		return operator;
	}

	@Bean
	public Job jobOperatorDemoJob() {
		return jobBuilderFactory.get("jobOperatorDemoJob")
				.start(jobOperatorDemoStep())
				.build();
	}

	@Bean
	public Step jobOperatorDemoStep() {
		return stepBuilderFactory.get("jobOperatorDemoStep")
				.listener(this)
				.tasklet(new Tasklet() {
					@Override
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
						System.out.println("Got msg: " + parameters.get("msg").getValue());
						return RepeatStatus.FINISHED;
					}
				})
				.build();
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		parameters = stepExecution.getJobParameters().getParameters();
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
