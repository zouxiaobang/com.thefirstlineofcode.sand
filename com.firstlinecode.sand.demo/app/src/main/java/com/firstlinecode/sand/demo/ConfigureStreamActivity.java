package com.firstlinecode.sand.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firstlinecode.chalk.core.stream.StandardStreamConfig;

import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConfigureStreamActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configure_stream);
	}

	public void configureStream(View view) {
		EditText etHost = findViewById(R.id.host);
		if (TextUtils.isEmpty(etHost.getText().toString())) {
			Toast.makeText(this, getString(R.string.text_host_cant_be_null), Toast.LENGTH_LONG).show();
			etHost.requestFocus();

			return;
		}

		try {
			InetAddress inetAddress = Inet4Address.getByName(etHost.getText().toString());
			if (!(inetAddress instanceof Inet4Address)) {
				throw new IllegalArgumentException("Not an IPv4 address.");
			}
		} catch (Exception e) {
			Toast.makeText(this, getString(R.string.text_host_must_be_an_ipv4_address), Toast.LENGTH_LONG).show();
			etHost.selectAll();
			etHost.requestFocus();

			return;
		}

		EditText etPort = findViewById(R.id.port);
		if (TextUtils.isEmpty(etHost.getText().toString())) {
			Toast.makeText(this, getString(R.string.text_port_cant_be_null), Toast.LENGTH_LONG).show();
			etPort.requestFocus();

			return;
		}

		boolean portIsInvalid = false;
		int port = -1;
		try {
			port = Integer.parseInt(etPort.getText().toString());

			if (port <= 0) {
				portIsInvalid = true;
			}
		} catch (NumberFormatException e) {
			portIsInvalid = true;
		}

		if (portIsInvalid) {
			Toast.makeText(this, getString(R.string.text_port_must_be_an_positive_integer), Toast.LENGTH_LONG).show();
			etPort.selectAll();
			etPort.requestFocus();

			return;
		}

		StandardStreamConfig streamConfig = new StandardStreamConfig(etHost.getText().toString(), port);
		PreferencesUtils.setStreamConfig(this, streamConfig);

		finish();
	}
}
