package com.example.demo.listener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

// 使用註解的方式
public class MyChunkListener {
	@BeforeChunk
	public void beforeChunk(ChunkContext chunkContext) {
		System.out.println("@BeforeChunk: " + chunkContext.getStepContext().getStepName());
	}

	@AfterChunk
	public void afterChunk(ChunkContext chunkContext) {
		System.out.println("@AfterChunk: " + chunkContext.getStepContext().getStepName());
	}

}