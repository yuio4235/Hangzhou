package com.as.order.application;

import android.app.Application;
import android.content.Intent;

import com.as.order.service.IndentSyncService;

public class OrderApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
//		Intent intent = new Intent(this, IndentSyncService.class);
//		startService(intent);
	}
	
}
