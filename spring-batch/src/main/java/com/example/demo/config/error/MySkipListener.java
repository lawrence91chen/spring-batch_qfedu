package com.example.demo.config.error;

import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class MySkipListener implements SkipListener<String, String> {
	// 分別可在 讀/寫/處理 出錯時記錄相關訊息

	@Override
	public void onSkipInRead(Throwable t) {

	}

	@Override
	public void onSkipInWrite(String item, Throwable t) {

	}

	@Override
	public void onSkipInProcess(String item, Throwable t) {
		System.out.println(item + " occured exception: " + t);
	}
}
