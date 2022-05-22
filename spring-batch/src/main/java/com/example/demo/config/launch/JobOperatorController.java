package com.example.demo.config.launch;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobOperatorController {
	@Autowired
	private JobOperator jobOperator;

	@GetMapping("job/op/{msg}")
	public String run(@PathVariable String msg) throws NoSuchJobException, JobInstanceAlreadyExistsException, JobParametersInvalidException {
		// 啟動任務，同時傳參數 (用 key=value 的方式傳參)
		jobOperator.start("jobOperatorDemoJob", "msg=" + msg);

		return "Job already be done with msg: " + msg;
	}
}
