# Spring Batch

## 1、SpringBatch 概述

![spring-batch-reference-model](../assets/spring-batch-reference-model.png)

- Spring Batch 是一個批處理應用框架，`不是調度框架`。需要和調度框架(Quartz、Control-M、...)合作來完成相應的批處理任務。
- Spring Batch 實現批處理是基於任務(Job)的，Job 的執行是由若干個 Step(步驟) 所構成(一個 Job 由一個或多個 Step 所組成)。
- Step 可以實現數據的讀取、處理、輸出。
- Job 可以由 JobLauncher 來進行啟動，在任務的執行過程中和 Job 相關的信息會被持久化到數據庫中，也就是 JobRepository。



## 2、搭建 Spring Batch 項目

- 使用 spring.io 創建 spring boot maven 項目，並 import 至 IDE 中

- 批處理任務執行的相關信息(metadata)需要持久化儲存到資料庫中

  - 未配置數據源專案會啟動失敗

- pom.xml

  ```xml
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-batch</artifactId>
  		</dependency>
  
  		<dependency>
  			<groupId>com.h2database</groupId>
  			<artifactId>h2</artifactId>
  			<scope>runtime</scope>
  		</dependency>
  ```

  

## 3、創建 Spring Batch 入門程序

- 創一個  Config 類 (@Configuration) 並加上 Batch 註解 `@EnableBatchProcessing`。

  ```java
  @Configuration
  @EnableBatchProcessing
  public class JobConfig {
  	// 注入創建任務對象的對象。
  	@Autowired
  	private JobBuilderFactory jobBuilderFactory;
  	// 任務的執行由 Step 決定。 (一個任務可以包含多個 step)
  	// 注入創建 Step 對象的對象。
  	@Autowired
  	private StepBuilderFactory stepBuilderFactory;
  
  	/**
  	 * 創建任務對象。
  	 */
  	@Bean
  	public Job helloWorldJob() {
  		return jobBuilderFactory
  				// 任務名稱: helloWorldJob
  				.get("helloWorldJob")
  				// 指定任務開始執行的 step
  				.start(step1())
  				.build();
  	}
  
  	@Bean
  	public Step step1() {
  		return stepBuilderFactory
  				// Step 名稱: step1
  				.get("step1")
  				// 指定 step 實現的功能 (tasklet 或 chunk)
  				.tasklet(new Tasklet() {
  
  					@Override
  					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
  							throws Exception {
  						// Step 具體的執行內容
  						System.out.println("Hello World!");
  
  						// 指定 Step 執行完後的狀態。 (後面可能還有其他 step，所以前一個 step 的狀態可能會決定後面會不會繼續執行)
  						return RepeatStatus.FINISHED;
  					}
  				}).build();
  	}
  }
  ```

  

- 執行啟動類即可執行任務。

  ```
  2022-05-18 16:43:30.915  INFO 19672 --- [           main] o.s.b.a.b.JobLauncherApplicationRunner   : Running default command line with: []
  2022-05-18 16:43:31.012  INFO 19672 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=helloWorldJob]] launched with the following parameters: [{}]
  2022-05-18 16:43:31.042  INFO 19672 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
  Hello World!
  2022-05-18 16:43:31.054  INFO 19672 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [step1] executed in 11ms
  2022-05-18 16:43:31.061  INFO 19672 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=helloWorldJob]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 29ms
  ```

  

