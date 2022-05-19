package com.example.demo.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

/**
 * 決策器。
 */
public class MyDecider implements JobExecutionDecider {
	private int count;

	/**
	 * decide 決策器，先執行 odd 奇數。
	 */
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		count++;
		if (count % 2 == 0) {
			return new FlowExecutionStatus("even");
		} else {
			return new FlowExecutionStatus("odd");
		}
	}
}
