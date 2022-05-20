package com.example.demo.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

// 使用 implements 的方式
public class MyJobListener implements JobExecutionListener {
	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("beforeJob: " + jobExecution.getJobInstance().getJobName());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("afterJob: " + jobExecution.getJobInstance().getJobName());
	}
}