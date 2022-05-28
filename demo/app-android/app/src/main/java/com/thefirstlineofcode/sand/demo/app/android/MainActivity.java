package com.thefirstlineofcode.sand.demo.app.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.thefirstlineofcode.sand.demo.app.android.R;
import com.thefirstlineofcode.basalt.protocol.core.IError;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stream.error.StreamError;
import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.core.IErrorListener;
import com.thefirstlineofcode.sand.client.operator.IOperator;
import com.thefirstlineofcode.sand.demo.client.AclError;
import com.thefirstlineofcode.sand.demo.client.IAclService;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements IOperator.Listener, IAclService.Listener, IErrorListener {
	private ExpandableListViewAdapter devicesListViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.app_name);
		setSupportActionBar(toolbar);

		IChatClient chatClient = ChatClientSingleton.get(this);
		IAclService aclService = chatClient.createApi(IAclService.class);
		if (!aclService.getListeners().contains(this)) {
			aclService.addListener(this);
			chatClient.getStream().addErrorListener(this);
		}

		ExpandableListView devicesListView = findViewById(R.id.devices);
		devicesListViewAdapter = new ExpandableListViewAdapter(this, aclService.getLocal());
		devicesListView.setAdapter(devicesListViewAdapter);

	}

	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = new Intent(this, AppExitMonitor.class);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.toolbar, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.refresh_devices) {
			refreshDevices();
			return true;
		} else if (item.getItemId() == R.id.authorize_device) {
			authorizeDevice();
			return true;
		} else if (item.getItemId() == R.id.logout) {
			logout();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	private void refreshDevices() {
		// TODO
	}

	private void authorizeDevice() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
		integrator.setOrientationLocked(false);

		integrator.initiateScan();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (result != null && result.getContents() != null) {
			authorizeDevice(result.getContents());
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void authorizeDevice(String deviceId) {
		IChatClient chatClient = ChatClientSingleton.get(this);
		IOperator operator = chatClient.createApi(IOperator.class);
		if (!operator.getListeners().contains(this))
			operator.addListener(this);

		operator.authorize(deviceId);
	}

	private void logout() {
		finish();

		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra(getString(R.string.auto_login), false);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		ChatClientSingleton.destroy();
		super.onDestroy();
	}

	@Override
	public void authorized(String deviceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, getString(R.string.device_has_authorized, deviceId), Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void confirmed(String concentratorId, String nodeId, String lanId) {
		// NOOP
	}

	@Override
	public void canceled(String deviceId) {
		// NOOP
	}

	@Override
	public void canceled(String concentratorId, String nodeId) {
		// NOOP
	}

	@Override
	public void occurred(IOperator.AuthorizationError error, String deviceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, getString(R.string.failed_to_authorize_device, error.getReason()), Toast.LENGTH_LONG).show();
			}
		});

	}

	@Override
	public void occurred(IOperator.ConfirmationError error, String concentratorId, String nodeId) {
		// NOOP
	}

	@Override
	public void retrived(AccessControlList acl) {

	}

	@Override
	public void updated(AccessControlList acl) {
		devicesListViewAdapter.updateAcl(acl);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				devicesListViewAdapter.notifyDataSetChanged();
				Toast.makeText(MainActivity.this, getString(R.string.devices_has_updated), Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void timeout(Iq iq) {

	}

	@Override
	public void occurred(AclError error) {

	}

	@Override
	public void occurred(IError error) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (error instanceof StreamError) {
					Toast.makeText(MainActivity.this, getString(R.string.stream_error_occurred, error.getDefinedCondition()), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(MainActivity.this, getString(R.string.stanza_error_occurred, error.getDefinedCondition()), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
