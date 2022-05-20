package com.example.demo.config;

import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParametersDemo implements StepExecutionListener {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	private Map<String, JobParameter> parameters;

	@Bean
	public Job parameterJob() {
		return jobBuilderFactory.get("parameterJob")
				.start(parameterStep())
				.build();
	}

	/**
	 * Job 執行的是 Step，Job 使用的數據肯定是在 step 中使用，所以只需給Step傳遞數據。
	 * Q: 如何給 step 傳遞參數? A: 使用監聽，使用 Step 級別的監聽來傳遞數據。 (可以在 before 裡傳數據)
	 * 使用當前的類來做監聽，讓 ParametersDemo 實現 StepExecutionListener (當然也可以另外建一個類來監聽，這邊只是演示方便)
	 */
	@Bean
	public Step parameterStep() {
		return stepBuilderFactory.get("parameterStep")
				// 因為有實現 StepExecutionListener，所以傳 this
				.listener(this)
				.tasklet(new Tasklet() {
					@Override
					public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
						// 輸出接收到的參數的值
						System.out.println(parameters.get("info"));
						return RepeatStatus.FINISHED;
					}
				}).build();
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		// 傳參數
		parameters = stepExecution.getJobParameters().getParameters();
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}
}
