package com.thefirstlineofcode.sand.demo.app.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.thefirstlineofcode.sand.demo.app.android.R;
import com.thefirstlineofcode.basalt.protocol.core.IError;
import com.thefirstlineofcode.chalk.core.AuthFailureException;
import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.core.stream.NegotiationException;
import com.thefirstlineofcode.chalk.core.stream.UsernamePasswordToken;
import com.thefirstlineofcode.chalk.network.ConnectionException;

public class LoginActivity extends AppCompatActivity {
	public static final int INTERNET_PERMISSION_REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		UsernamePasswordToken token = Toolkits.getUsernamePasswordToken(this);
		if (token == null)
			return;

		EditText etUserName = findViewById(R.id.et_user_name);
		etUserName.setText(token.getUsername());
		EditText etPassword = findViewById(R.id.et_password);
		etPassword.setText(new String(token.getPassword()));

		Intent intent = getIntent();
		if (intent != null && intent.getBooleanExtra(getString(R.string.auto_login), true))
			login(findViewById(R.id.bt_login));
	}

	public void startRegisterActivity(View view) {
		startActivity(new Intent(this, RegisterActivity.class));
	}

	public void startConfigureStreamActivity(View view) {
		startActivity(new Intent(this, ConfigureStreamActivity.class));
	}

	public void login(View view) {
		EditText etUserName = findViewById(R.id.et_user_name);
		String userName = etUserName.getText().toString();
		if (TextUtils.isEmpty(userName)) {
			runOnUiThread(() -> {
				Toast.makeText(this, getString(R.string.user_name_cant_be_null), Toast.LENGTH_LONG).show();
				etUserName.requestFocus();
			});

			return;
		}

		EditText etPassword = findViewById(R.id.et_password);
		String password = etPassword.getText().toString();
		if (TextUtils.isEmpty(password)) {
			runOnUiThread(() -> {
				Toast.makeText(this, getString(R.string.password_cant_be_null), Toast.LENGTH_LONG).show();
				etPassword.requestFocus();
			});

			return;
		}

		IChatClient chatClient = ChatClientSingleton.get(this);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
			if (!chatClient.isConnected() && !connect(etUserName, userName, password, chatClient))
				return;
		} else {
			requestPermissions(new String[] {Manifest.permission.INTERNET}, INTERNET_PERMISSION_REQUEST_CODE);
		}

		finish();
		startActivity(new Intent(this, MainActivity.class));
	}

	private boolean connect(EditText etUserName, String userName, String password, IChatClient chatClient) {
		try {
			chatClient.connect(new UsernamePasswordToken(userName, password));
		} catch (ConnectionException e) {
			runOnUiThread(() -> Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show());
			return false;
		} catch (AuthFailureException e) {
			runOnUiThread(() -> {
				Toast.makeText(this, getString(R.string.incorrect_user_name_or_password), Toast.LENGTH_LONG).show();
				etUserName.selectAll();
				etUserName.requestFocus();
			});
			
			return false;
		} catch (RuntimeException e) {
			NegotiationException ne = Toolkits.findNegotiationException(e);
			if (ne != null && ne.getAdditionalErrorInfo() instanceof IError) {
				IError error = (IError)ne.getAdditionalErrorInfo();
				runOnUiThread(() -> Toast.makeText(this, getString(R.string.unknown_error,
						Toolkits.getErrorInfo(error)), Toast.LENGTH_LONG).show());
			} else {
				runOnUiThread(() -> Toast.makeText(this, getString(R.string.unknown_error, e.getClass().getName()), Toast.LENGTH_LONG).show());
			}

			return false;
		}

		Toolkits.rememberUser(this, userName, password.toCharArray());
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == INTERNET_PERMISSION_REQUEST_CODE) {
			login(findViewById(R.id.bt_login));
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
