package com.example.demo.config.read.restart;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import com.example.demo.config.read.Customer;

@Component("restartReader")
public class RestartReader implements ItemStreamReader<Customer> {
	private FlatFileItemReader<Customer> customerFlatFileItemReader = new FlatFileItemReader<>();
	private Long curLine = 0L; // 當前讀的是第幾行
	private boolean restart = false;
	private ExecutionContext executionContext; // 執行上下文:通過它向數據庫持久化一些訊息。

	public RestartReader() {
		customerFlatFileItemReader.setResource(new ClassPathResource("restart.txt"));

		// 數據解析
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		// 指名表頭字段
		tokenizer.setNames("id", "firstName", "lastName", "birthday");

		// 把解析出的一行數據映射為 Customer 對象
		DefaultLineMapper<Customer> mapper = new DefaultLineMapper<>();
		mapper.setLineTokenizer(tokenizer);
		mapper.setFieldSetMapper(new FieldSetMapper<Customer>() {
			@Override
			public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
				Customer customer = new Customer();
				customer.setId(fieldSet.readLong("id"));
				customer.setFirstName(fieldSet.readString("firstName"));
				customer.setLastName(fieldSet.readString("lastName"));
				customer.setBirthday(fieldSet.readString("birthday"));

				return customer;
			}
		});

		mapper.afterPropertiesSet(); // 檢查
		customerFlatFileItemReader.setLineMapper(mapper);
	}

	// 讀數據
	@Override
	public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Customer customer = null;
		this.curLine++;

		if (restart) {
			customerFlatFileItemReader.setLinesToSkip(this.curLine.intValue() - 1);
			restart = false;
			System.out.println("Start reading from line: " + this.curLine);
		}

		customerFlatFileItemReader.open(this.executionContext);
		customer = customerFlatFileItemReader.read();

		// 自行製造異常，讓讀取過程有失敗
		// 一旦發生異常就不會執行 update 了 (推測這種流程應該是做到第幾個失敗後面的都不會執行)
		// 但重啟時前面 update 過的就不會再讀了
		if (customer != null && customer.getFirstName().contentEquals("WrongName")) {
			throw new RuntimeException("Something wrong. customer.id=[" + customer.getId() + "]");
		}

		return customer;
	}

	// Step 執行前執行 open
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		this.executionContext = executionContext;

		if (executionContext.containsKey("curLine")) {
			this.curLine = executionContext.getLong("curLine");
			this.restart = true;
		} else {
			this.curLine = 0L;
			executionContext.put("curLine", this.curLine);
			System.out.println("Start reading from line: " + this.curLine + 1);
		}
	}

	// 每讀完 N 個數據執行一次 (每一批 chunk 指定的數量執行成功後執行)
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.put("curLine", this.curLine);
		System.out.println("curLine: " + this.curLine);
	}

	// 整個 Step 執行完成會執行 close
	@Override
	public void close() throws ItemStreamException {

	}
}
