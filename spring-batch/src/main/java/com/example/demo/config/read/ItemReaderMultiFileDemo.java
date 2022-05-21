package com.example.demo.config.read;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

@Configuration
public class ItemReaderMultiFileDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("multiFileWriter")
	private ItemWriter<? super Customer> multiFileWriter;

	@Value("classpath:/file*.txt")
	private Resource[] fileResources;

	@Bean
	public Job itemReaderMultiFileDemoJob() {
		return jobBuilderFactory.get("itemReaderMultiFileDemoJob")
				.start(itemReaderMultiFileDemoStep())
				.build();
	}

	@Bean
	public Step itemReaderMultiFileDemoStep() {
		return stepBuilderFactory.get("itemReaderMultiFileDemoStep")
				.<Customer, Customer>chunk(5)
				.reader(multiFileReader())
				.writer(multiFileWriter)
				.build();
	}

	// 雖說是多文件讀取，但其實是逐個讀取單個文件
	@Bean
	@StepScope
	public MultiResourceItemReader<Customer> multiFileReader() {

		MultiResourceItemReader<Customer> reader = new MultiResourceItemReader<>();
		// 從單個文件讀取的功能
		reader.setDelegate(flatFileDelegateReader());
		reader.setResources(fileResources);

		return reader;
	}

	@Bean
	public FlatFileItemReader<Customer> flatFileDelegateReader() {
		FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
		// 數據解析
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames("id", "firstName", "lastName", "birthday");
		// 把解析出的一個數據映射為Customer對象
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
		mapper.afterPropertiesSet();

		reader.setLineMapper(mapper);

		return reader;
	}
}
