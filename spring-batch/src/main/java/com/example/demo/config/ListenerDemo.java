package com.example.demo.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.listener.MyChunkListener;
import com.example.demo.listener.MyJobListener;

@Configuration
public class ListenerDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job listenerJob() {
		return jobBuilderFactory.get("listenerJob")
				.start(listenerJobStep1())
				// 使用監聽的方式，傳入自定義的 Listener
				// 作業執行前 執行 beforeJob、作業執行後 執行 afterJob
				.listener(new MyJobListener())
				.build();
	}

	/**
	 * Chunk 方式設置 Step
	 */
	@Bean
	public Step listenerJobStep1() {
		return stepBuilderFactory.get("listenerJobStep1")
				// Chunk: 實現數據的讀取。 特點: 可以 read, process, write
				// <String, String> 規定讀取和輸出的數據類型
				// 參數傳入 2 表示每讀完 2 個數據進行一次輸出處理
				.<String, String>chunk(2)
				// 容錯
				.faultTolerant()
				.listener(new MyChunkListener())
				// 數據的讀取
				.reader(read())
				// 數據的寫入/輸出
				.writer(write())
				.build();
	}

	// 定義 怎麼讀
	@Bean
	public ItemReader<String> read() {
		return new ListItemReader<>(Arrays.asList("java", "spring", "mybatis"));
	}

	// 定義 怎麼寫
	@Bean
	public ItemWriter<String> write() {
		return new ItemWriter<String>() {
			@Override
			public void write(List<? extends String> items) throws Exception {
				for (String item : items) {
					System.out.println("write:" + item);
				}
			}
		};
	}
}