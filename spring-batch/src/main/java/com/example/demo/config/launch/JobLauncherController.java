package com.example.demo.config.launch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLauncherController {
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private Job jobLauncherDemoJob;

	@GetMapping("job/{msg}")
	public String run(@PathVariable String msg) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		// 把接收到的參數值傳給任務
		JobParameters parameters = new JobParametersBuilder()
				.addString("msg", msg)
				.toJobParameters();
		// 啟動任務，並把參數傳給任務
		jobLauncher.run(jobLauncherDemoJob, parameters);

		return "Job already be done with msg: " + msg;
	}
}
