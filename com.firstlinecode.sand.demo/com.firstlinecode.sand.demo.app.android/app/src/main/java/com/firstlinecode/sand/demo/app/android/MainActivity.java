package com.firstlinecode.sand.demo.app.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firstlinecode.chalk.IChatClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
		if (item.getItemId() == R.id.device_registration) {
			registerDevice();
			return true;
		} else if (item.getItemId() == R.id.logout) {
			logout();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	private void registerDevice() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
		integrator.setOrientationLocked(false);

		integrator.initiateScan();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (result != null && result.getContents() != null) {
			System.out.println("Barcode contents: " + result.getContents());
		} else {
			super.onActivityResult(requestCode, resultCode, data);
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
