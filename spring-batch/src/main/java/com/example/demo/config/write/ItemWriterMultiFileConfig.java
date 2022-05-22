package com.example.demo.config.write;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.read.User;

@Configuration
public class ItemWriterMultiFileConfig {
	@Autowired
	@Qualifier("fileItemWriter")
	private ItemWriter<? super User> fileItemWriter;
	@Autowired
	@Qualifier("xmlItemWriter")
	private ItemWriter<? super User> xmlItemWriter;

	// 調用輸出到單個文件操作來實現輸出數據到多個文件
//	@Bean
//	public CompositeItemWriter<User> multiFileItemWriter() throws Exception {
//		CompositeItemWriter<User> writer = new CompositeItemWriter<>();
//		// 輸出到兩個不同的文件中 (實際上是讓多個輸出對象去寫)
//		writer.setDelegates(Arrays.asList(fileItemWriter, xmlItemWriter));
//
//		writer.afterPropertiesSet();
//
//		return writer;
//	}

	// 實現分類: 按照某種條件對數據進行分類存儲不同文件
	@Bean
	public ClassifierCompositeItemWriter<User> multiFileItemWriter() {
		ClassifierCompositeItemWriter<User> writer = new ClassifierCompositeItemWriter<>();

		writer.setClassifier(new Classifier<User, ItemWriter<? super User>>() {
			private static final long serialVersionUID = 1L;
			// 分類，比如按照年齡分成兩個文件
			@Override
			public ItemWriter<? super User> classify(User user) {
				// 按照 user 的 id 進行分類
				return user.getId() % 2 == 0 ? fileItemWriter : xmlItemWriter;
			}
		});

		return writer;
	}
}
