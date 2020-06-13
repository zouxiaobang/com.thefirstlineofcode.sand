package com.firstlinecode.sand.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firstlinecode.chalk.IChatClient;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.app_name);
		setSupportActionBar(toolbar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.toolbar, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.logout) {
			logout();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	private void logout() {
		IChatClient chatClient = ChatClientSingleton.get(this);
		if (chatClient != null && chatClient.isConnected())
			chatClient.close();

		finish();

		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra(getString(R.string.auto_login), false);
		startActivity(intent);
	}
}
