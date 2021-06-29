# Spring Batch

## 1、SpringBatch 概述

![spring-batch-reference-model](../assets/spring-batch-reference-model.png)

- Spring Batch 是一個批處理應用框架，`不是調度框架`。需要和調度框架(Quartz、Control-M、...)合作來完成相應的批處理任務。
- Spring Batch 實現批處理是基於任務(Job)的，Job 的執行是由若干個 Step(步驟) 所構成(一個 Job 由一個或多個 Step 所組成)。
- Step 可以實現數據的讀取、處理、輸出。
- Job 可以由 JobLauncher 來進行啟動，在任務的執行過程中和 Job 相關的信息會被持久化到數據庫中，也就是 JobRepository。
