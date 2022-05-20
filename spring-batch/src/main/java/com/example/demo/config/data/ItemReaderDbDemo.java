package com.example.demo.config.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

@Configuration
public class ItemReaderDbDemo {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	@Autowired
	@Qualifier("dbJdbcWriter")
	private ItemWriter<? super User> dbJdbcWriter;

	@Bean
	public Job itemReaderDbDemoJob() {
		return jobBuilderFactory.get("itemReaderDbDemoJob")
				.start(itemReaderDbStep())
				.build();
	}

	@Bean
	public Step itemReaderDbStep() {
		return stepBuilderFactory.get("itemReaderDemoStep")
				.<User, User>chunk(2)
				.reader(dbJdbcReader())
				.writer(dbJdbcWriter)
				.build();
	}

	@Bean
	@StepScope // 指定範圍
	public JdbcPagingItemReader<User> dbJdbcReader() {
		JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();
		reader.setDataSource(dataSource);
		reader.setFetchSize(2);
		// 把讀取到的記錄轉換成 User 對象
		reader.setRowMapper(new RowMapper<User>() {
			// 結果集的映射。 rowNum: ResultSet 共有多少行數
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setId(rs.getInt(1));
				user.setUsername(rs.getString(2));
				user.setPassword(rs.getString(3));
				user.setAge(rs.getInt(4));

				return user;
			}
		});

		// 指定 sql 語句
		MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
		provider.setSelectClause("id, username, password, age");
		provider.setFromClause("from user");
		// 指定根據哪個字段進行排序
		Map<String, Order> sort = new HashMap<>(1); // 指定初始大小為 1
		sort.put("id", Order.ASCENDING);
		provider.setSortKeys(sort);

		reader.setQueryProvider(provider);

		return reader;
	}
}
