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
		EditText etName = findViewById(R.id.name);
		if (TextUtils.isEmpty(etName.getText().toString())) {
			Toast.makeText(view.getContext(), "Name can't be null.", Toast.LENGTH_LONG).show();
			etName.requestFocus();

			return;
		}

		EditText etPassword = findViewById(R.id.password);
		if (TextUtils.isEmpty(etPassword.getText().toString())) {
			Toast.makeText(view.getContext(), "Password can't be null.", Toast.LENGTH_LONG).show();
			etPassword.requestFocus();

			return;
		}

		EditText etRepeatPassword = findViewById(R.id.repeatPassword);
		if (TextUtils.isEmpty(etRepeatPassword.getText().toString())) {
			Toast.makeText(view.getContext(), "Repeat password can't be null.", Toast.LENGTH_LONG).show();
			etRepeatPassword.requestFocus();

			return;
		}

		if (!etPassword.getText().toString().equals(etRepeatPassword.getText().toString())) {
			Toast.makeText(view.getContext(), "Password doesn't equal to repeat password.", Toast.LENGTH_LONG).show();
			etPassword.requestFocus();

			return;
		}

		register(etName.getText().toString(), etPassword.getText().toString());
	}

	private void register(String name, String password) {
		// TODO
	}
}
