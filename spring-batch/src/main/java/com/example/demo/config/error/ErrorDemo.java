package com.example.demo.config.error;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job errorDemoJob() {
		return jobBuilderFactory.get("errorDemoJob")
				.start(errorDemoStep1())
				.next(errorDemoStep2())
				.build();
	}

	@Bean
	public Step errorDemoStep1() {
		return stepBuilderFactory.get("errorDemoStep1")
				.tasklet(errorHandling())
				.build();
	}

	@Bean
	public Step errorDemoStep2() {
		return stepBuilderFactory.get("errorDemoStep2")
				.tasklet(errorHandling())
				.build();
	}

	@Bean
	@StepScope
	public Tasklet errorHandling() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				// 每個不同 Step 有自己的執行上下文
				Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

				if (stepExecutionContext.containsKey("NoError")) {
					System.out.println("The second run will succeed.");
					return RepeatStatus.FINISHED;
				} else {
					System.out.println("The first run will fail.");
					chunkContext.getStepContext().getStepExecution().getExecutionContext().put("NoError", true);
					throw new RuntimeException("error ...");
				}
			}
		};
	}
}
