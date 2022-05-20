# Spring Batch

- Ch1: 入門 (1~5)
- Ch2: 作業流 (6~



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

  ```
  Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
  2021-05-04 21:39:53.562 ERROR 13604 --- [           main] o.s.b.d.LoggingFailureAnalysisReporter   : 
  
  ***************************
  APPLICATION FAILED TO START
  ***************************
  
  Description:
  
  Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
  
  Reason: Failed to determine a suitable driver class
  
  
  Action:
  
  Consider the following:
  	If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.
  	If you have database settings to be loaded from a particular profile you may need to activate it (no profiles are currently active).
  ```

  

- pom.xml

  ```xml
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-batch</artifactId>
  		</dependency>
  ```
  
  
  
  ```xml
  		<dependency>
  			<groupId>com.h2database</groupId>
  			<artifactId>h2</artifactId>
  			<scope>runtime</scope>
  		</dependency>
  ```
  
  或
  
  ```xml
          <dependency>
              <groupId>org.hsqldb</groupId>
              <artifactId>hsqldb</artifactId>
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

  

## 4、替換為 MySQL 數據庫

- pom.xml

  ```xml
  		<dependency>
  			<groupId>mysql</groupId>
  			<artifactId>mysql-connector-java</artifactId>
  			<scope>runtime</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-jdbc</artifactId>
  			<scope>runtime</scope>
  		</dependency>
  ```

- application.properties

  ```properties
  # Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
  spring.datasource.driverClassName=com.mysql.jdbc.Driver
  spring.datasource.url=jdbc:mysql://localhost:3306/SPRING_BATCH
  spring.datasource.username=test
  spring.datasource.password=test
  # C:\Users\user\.m2\repository\org\springframework\batch\spring-batch-core\4.3.2\spring-batch-core-4.3.2.jar
  spring.datasource.schema=classpath:/org/springframework/batch/core/schema-mysql.sql
  # spring batch 初始化時執行 schema-mysql.sql
  spring.batch.initialize-schema=always
  ```

- spring batch ER-diagram

  ![spring batch ER-diagram](../docs/spring-batch.png)

- 其他

> https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-versions.html
>
> ![image-20210504224231239](spring-batch.assets/image-20210504224231239.png)



## 5、核心 API

![job-stereotypes-parameters](../assets/job-stereotypes-parameters.png)

- JobInstance
  - JobInstance 和 Job 的關係如同 Java 中實例與類的關係。
  - Job 定義了一個工作流程，JobInstance 就是該工作流程的一個具體實例。
  - 一個 Job 可以有多個 JobInstance 

- JobParameters:
  - 不同的 JobParameters 配置將產生不同的JobInstance。
  - 使用相同的 JobParameters 運行同一 Job，會重複使用上一個 JobInstance
  - 可以初步理解為 **JobInstance = Job + JobParameters** 

- JobExecution:
  - 表示 JobInstance 的一次運行
  - 每次 JobInstance 的運行都會產生 1 個 JobExecution (包含 運行時間、狀態成功與否、...)
- StepExecution:
  - 類似於 JobExecution，表示 Step 的一次運行
- ExecutionContext
  - 表示每一個 StepExecution 的執行內容。包含開發人員需要在批處理執行中保留的任何資料，例如重新啟動所需的統計資訊或狀態資訊。



## 6、Job 的創建和使用

- Job: 作業。批處理中的核心概念，是Batch操作的基礎單元。
- 每個作業(Job)有 1 個或者多個作業步驟(Step)



## 7、Flow 的創建和使用

1. Flow 是多個 Step 的集合
2. 可以被多個 Job 複用
3. 使用 FlowBuilder 來創建

> 可以組裝若干個 Step 成為一個 flow 給多個不同的 Job 複用。



## 8、split 實現併發執行

實現任務中的多個step或多個flow併發執行

1. 創建若干個step
2. 創建兩個flow
3. 創建一個任務包含以上兩個flow，並讓這兩個 flow 併發執行

```
2022-05-19 18:43:12.904  INFO 20508 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=splitDemoJob]] launched with the following parameters: [{}]
2022-05-19 18:43:12.965  INFO 20508 --- [cTaskExecutor-2] o.s.batch.core.job.SimpleStepHandler     : Executing step: [splitDemoStep1]
2022-05-19 18:43:12.987  INFO 20508 --- [cTaskExecutor-1] o.s.batch.core.job.SimpleStepHandler     : Executing step: [splitDemoStep2]
splitDemoStep1
splitDemoStep2
2022-05-19 18:43:13.007  INFO 20508 --- [cTaskExecutor-2] o.s.batch.core.step.AbstractStep         : Step: [splitDemoStep1] executed in 42ms
2022-05-19 18:43:13.023  INFO 20508 --- [cTaskExecutor-1] o.s.batch.core.step.AbstractStep         : Step: [splitDemoStep2] executed in 36ms
2022-05-19 18:43:13.048  INFO 20508 --- [cTaskExecutor-1] o.s.batch.core.job.SimpleStepHandler     : Executing step: [splitDemoStep3]
splitDemoStep3
2022-05-19 18:43:13.073  INFO 20508 --- [cTaskExecutor-1] o.s.batch.core.step.AbstractStep         : Step: [splitDemoStep3] executed in 25ms
2022-05-19 18:43:13.097  INFO 20508 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [FlowJob: [name=splitDemoJob]] completed with the following parameters: [{}] and the following status: [COMPLETED] in 170ms
```

![image-20220519184404502](spring-batch.assets/image-20220519184404502.png)



## 9、決策器的使用

接口: JobExecutionDecider



> on() 方法沒辦法滿足比較複雜的條件



## 10、Job 的嵌套

一個 Job 可以嵌套在另一個 Job 中，被嵌套的 Job 稱為子Job，外部Job稱為父Job。子 Job 不能單獨執行，需要由父Job來啟動。

- 案例: 創建兩個 Job，一個作為子 Job，再創建一個 Job 作為父 Job。



## 11、監聽器的使用

用來監聽批處理作業的執行情況
創建監聽可以通過實現接口或使用注解
(不僅限於監聽 Job)

- 不同的監聽，以及觸發時機

  ```
  JobExecutionListener(before,after)
  StepExecutionListener(before,after)
  ChunkListener(before,after,error)
  ItemReadListener,ItemProcessListener,ItemWriteListener(before,after,error)
  ```


![image-20220520171237971](spring-batch.assets/image-20220520171237971.png)



## 12、Job 參數

在 Job 運行時可以用 `key=value` 形式傳遞參數



程式執行時傳外部 program arguments

```
--info=MyInformation
```

