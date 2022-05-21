package com.example.demo.config.data;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.TypePermission;

@Configuration
public class ItemReaderXmlDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("xmlFileWriter")
	private ItemWriter<? super Customer> xmlFileWriter;

	@Bean
	public Job itemReaderXmlDemoJob() {
		return jobBuilderFactory.get("itemReaderXmlDemoJob")
				.start(itemReaderXmlDemoStep())
				.build();
	}

	@Bean
	public Step itemReaderXmlDemoStep() {
		return stepBuilderFactory.get("itemReaderXmlDemoStep")
				.<Customer, Customer>chunk(5)
				.reader(xmlFileReader())
				.writer(xmlFileWriter)
				.build();
	}

	@Bean
	@StepScope
	public StaxEventItemReader<Customer> xmlFileReader() {
		StaxEventItemReader<Customer> reader = new StaxEventItemReader<>();
		// 指定文件位置
		reader.setResource(new ClassPathResource("customer.xml"));
		// 指定需要處理的根標籤
		reader.setFragmentRootElementName("customer");
		// 把 xml 轉成對象 (使用 Spring 提供的 XStreamMarshaller)
		// Object -> XML: marshaller
		// XML -> Object: unmarshaller
		XStreamMarshaller unmarshaller = new XStreamMarshaller();
		// 告訴 unmarshaller 把 xml node 轉成什麼類型
		Map<String, Class<?>> map = new HashMap<>();
		map.put("customer", Customer.class);
		unmarshaller.setAliases(map);

		// As of XStream 1.4.18, the default type permissions are restricted to well-known core JDK types.
		// For any custom types, explicit type permissions need to be registered.
		// https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/oxm/xstream/XStreamMarshaller.html
		// https://x-stream.github.io/javadoc/com/thoughtworks/xstream/security/TypePermission.html
		TypePermission typePermission = new AnyTypePermission();
		typePermission.allows(Customer.class);
		unmarshaller.setTypePermissions(typePermission);

		reader.setUnmarshaller(unmarshaller);

		return reader;
	}
}
