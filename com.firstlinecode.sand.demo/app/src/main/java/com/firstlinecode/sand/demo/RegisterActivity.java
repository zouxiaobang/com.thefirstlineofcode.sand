package com.firstlinecode.sand.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}

	public void register(View view) {
		EditText etLoginName = findViewById(R.id.loginName);
		if (TextUtils.isEmpty(etLoginName.getText().toString())) {
			Toast.makeText(view.getContext(), getString(R.string.name_cant_be_null), Toast.LENGTH_LONG).show();
			etLoginName.requestFocus();

			return;
		}

		EditText etPassword = findViewById(R.id.password);
		if (TextUtils.isEmpty(etPassword.getText().toString())) {
			Toast.makeText(view.getContext(), getString(R.string.password_cant_be_null), Toast.LENGTH_LONG).show();
			etPassword.requestFocus();

			return;
		}

		EditText etConfirmPassword = findViewById(R.id.confirmPassword);
		if (TextUtils.isEmpty(etConfirmPassword.getText().toString())) {
			Toast.makeText(view.getContext(), getString(R.string.confirm_password_cant_be_null), Toast.LENGTH_LONG).show();
			etConfirmPassword.requestFocus();

			return;
		}

		if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
			Toast.makeText(view.getContext(), getString(R.string.password_not_equal_to_confirm_password), Toast.LENGTH_LONG).show();
			etPassword.requestFocus();

			return;
		}

		register(etLoginName.getText().toString(), etPassword.getText().toString());
	}

	private void register(String loginName, String password) {
		// TODO
	}
}
