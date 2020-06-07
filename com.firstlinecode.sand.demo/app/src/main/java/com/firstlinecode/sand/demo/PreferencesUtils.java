package com.firstlinecode.sand.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.firstlinecode.chalk.core.stream.StandardStreamConfig;

public class PreferencesUtils {
	@SuppressLint("ApplySharedPref")
	public static void setStreamConfig(Context context, StandardStreamConfig streamConfig) {
		SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(context.getString(R.string.stream_config_host), streamConfig.getHost());
		editor.putInt(context.getString(R.string.stream_config_port), streamConfig.getPort());
		editor.putBoolean(context.getString(R.string.stream_config_enable_tls), streamConfig.isTlsPreferred());
		editor.commit();
	}

	public static StandardStreamConfig getStreamConfig(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
		String host = preferences.getString(context.getString(R.string.stream_config_host),
				context.getString(R.string.text_default_host));
		int port = preferences.getInt(context.getString(R.string.stream_config_port),
				Integer.parseInt(context.getString(R.string.text_default_host)));
		boolean enableTls = preferences.getBoolean(context.getString(R.string.stream_config_enable_tls),
				Boolean.parseBoolean(context.getString(R.string.stream_config_enable_tls)));

		StandardStreamConfig streamConfig = new StandardStreamConfig(host, port);
		streamConfig.setTlsPreferred(enableTls);

		return streamConfig;
	}
}
