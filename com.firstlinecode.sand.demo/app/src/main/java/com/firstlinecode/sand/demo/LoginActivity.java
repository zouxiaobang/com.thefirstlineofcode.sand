package com.firstlinecode.sand.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firstlinecode.basalt.protocol.core.IError;
import com.firstlinecode.chalk.AuthFailureException;
import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.chalk.core.stream.NegotiationException;
import com.firstlinecode.chalk.core.stream.UsernamePasswordToken;
import com.firstlinecode.chalk.network.ConnectionException;

public class LoginActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		UsernamePasswordToken token = Toolkits.getUsernamePasswordToken(this);
		if (token != null) {
			EditText etUserName = findViewById(R.id.userName);
			etUserName.setText(token.getUsername());
			EditText etPassword = findViewById(R.id.password);
			etPassword.setText(new String(token.getPassword()));
		}
	}

	public void startRegisterActivity(View view) {
		startActivity(new Intent(this, RegisterActivity.class));
	}

	public void startConfigureStreamActivity(View view) {
		startActivity(new Intent(this, ConfigureStreamActivity.class));
	}

	public void login(View view) {
		EditText etUserName = findViewById(R.id.userName);
		String userName = etUserName.getText().toString();
		if (TextUtils.isEmpty(userName)) {
			Toast.makeText(this, getString(R.string.user_name_cant_be_null), Toast.LENGTH_LONG).show();
			etUserName.requestFocus();

			return;
		}

		EditText etPassword = findViewById(R.id.password);
		String password = etPassword.getText().toString();
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(this, getString(R.string.password_cant_be_null), Toast.LENGTH_LONG).show();
			etPassword.requestFocus();

			return;
		}

		IChatClient chatClient = ChatClientSingleton.get(this);
		try {
			chatClient.connect(new UsernamePasswordToken(userName, password));
		} catch (ConnectionException e) {
			Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
			return;
		} catch (AuthFailureException e) {
			Toast.makeText(this, getString(R.string.incorrect_user_name_or_password), Toast.LENGTH_LONG).show();
			etUserName.selectAll();
			etUserName.requestFocus();

			return;
		} catch (RuntimeException e) {
			NegotiationException ne = Toolkits.findNegotiationException(e);
			if (ne != null && ne.getAdditionalErrorInfo() instanceof IError) {
				IError error = (IError)ne.getAdditionalErrorInfo();
				Toast.makeText(this, getString(R.string.unknown_error,
						Toolkits.getErrorInfo(error)), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, getString(R.string.unknown_error, e.getClass().getName()), Toast.LENGTH_LONG).show();
			}

			return;
		}

		Toolkits.rememberUser(this, userName, password.toCharArray());

		startActivity(new Intent(this, MainActivity.class));
	}
}
