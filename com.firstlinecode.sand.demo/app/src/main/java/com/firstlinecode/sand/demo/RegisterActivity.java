package com.firstlinecode.sand.demo;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firstlinecode.basalt.xeps.ibr.IqRegister;
import com.firstlinecode.basalt.xeps.ibr.RegistrationField;
import com.firstlinecode.basalt.xeps.ibr.RegistrationForm;
import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.chalk.android.StandardChatClient;
import com.firstlinecode.chalk.core.stream.StandardStreamConfig;
import com.firstlinecode.chalk.xeps.ibr.IRegistration;
import com.firstlinecode.chalk.xeps.ibr.IRegistrationCallback;
import com.firstlinecode.chalk.xeps.ibr.IbrError;
import com.firstlinecode.chalk.xeps.ibr.IbrPlugin;
import com.firstlinecode.chalk.xeps.ibr.RegistrationException;

public class RegisterActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}

	public void register(View view) {
		EditText etUserName = findViewById(R.id.userName);
		if (TextUtils.isEmpty(etUserName.getText().toString())) {
			Toast.makeText(this, getString(R.string.user_name_cant_be_null), Toast.LENGTH_LONG).show();
			etUserName.requestFocus();

			return;
		}

		EditText etPassword = findViewById(R.id.password);
		if (TextUtils.isEmpty(etPassword.getText().toString())) {
			Toast.makeText(this, getString(R.string.password_cant_be_null), Toast.LENGTH_LONG).show();
			etPassword.requestFocus();

			return;
		}

		EditText etConfirmPassword = findViewById(R.id.confirmPassword);
		if (TextUtils.isEmpty(etConfirmPassword.getText().toString())) {
			Toast.makeText(this, getString(R.string.confirm_password_cant_be_null), Toast.LENGTH_LONG).show();
			etConfirmPassword.requestFocus();

			return;
		}

		if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
			Toast.makeText(this, getString(R.string.password_not_equal_to_confirm_password), Toast.LENGTH_LONG).show();
			etPassword.requestFocus();

			return;
		}

		register(this, etUserName, etPassword);
	}

	private void register(Context context, final EditText userName, final EditText password) {
		IChatClient chatClient = createChatClient();
		IRegistration registration = chatClient.createApi(IRegistration.class);
		try {
			registration.register(new IRegistrationCallback() {
				@Override
				public Object fillOut(IqRegister iqRegister) {
					if (iqRegister.getRegister() instanceof RegistrationForm) {
						RegistrationForm form = new RegistrationForm();
						form.getFields().add(new RegistrationField("username", userName.getText().toString()));
						form.getFields().add(new RegistrationField("password", password.getText().toString()));

						return form;
					} else {
						throw new RuntimeException("Can't get registration form.");
					}
				}
			});

			Toast.makeText(context, context.getString(R.string.text_user_has_registered), Toast.LENGTH_LONG).show();
			finish();
		} catch (RegistrationException e) {
			IbrError error = e.getError();
			if (error == IbrError.CONFLICT) {
				Toast.makeText(context, context.getString(R.string.text_user_name_has_existed), Toast.LENGTH_LONG).show();
				userName.selectAll();
				userName.requestFocus();
			} else if (error == IbrError.NOT_ACCEPTABLE) {
				Toast.makeText(context, context.getString(R.string.text_invalid_user_name), Toast.LENGTH_LONG).show();
				userName.selectAll();
				userName.requestFocus();
			} else if (error == IbrError.CONNECTION_ERROR || error == IbrError.TIMEOUT) {
				Toast.makeText(context, context.getString(R.string.text_network_error), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, context.getString(R.string.text_unknown_error), Toast.LENGTH_LONG).show();
			}
		}
	}

	private IChatClient createChatClient() {
		StandardStreamConfig streamConfig = PreferencesUtils.getStreamConfig(this);
		IChatClient chatClient = new StandardChatClient(streamConfig);
		chatClient.register(IbrPlugin.class);

		return chatClient;
	}
}
