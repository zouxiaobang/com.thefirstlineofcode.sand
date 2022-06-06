package com.thefirstlineofcode.sand.demo.app.android;

import android.app.Application;
import android.os.Environment;

import com.thefirstlineofcode.chalk.android.logger.LogConfigurator;

import java.io.File;

public class MainApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		File dataDir = getApplicationContext().getExternalFilesDir(null);
		new LogConfigurator().configure(dataDir.getAbsolutePath(), "sand-demo", LogConfigurator.LogLevel.INFO);
	}
}
