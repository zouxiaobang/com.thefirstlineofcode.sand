package com.thefirstlineofcode.sand.demo.app.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thefirstlineofcode.basalt.protocol.core.IError;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stream.error.StreamError;
import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.core.IErrorListener;
import com.thefirstlineofcode.sand.client.operator.IOperator;
import com.thefirstlineofcode.sand.demo.client.AclError;
import com.thefirstlineofcode.sand.demo.client.IAclService;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;

public class MainActivity extends AppCompatActivity implements IOperator.Listener, IAclService.Listener, IErrorListener {
	private DevicesAdapter devicesAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toolbar tbToolbar = findViewById(R.id.tb_tool_bar);
		tbToolbar.setTitle(R.string.app_name);
		setSupportActionBar(tbToolbar);

		IChatClient chatClient = ChatClientSingleton.get(this);
		IAclService aclService = chatClient.createApi(IAclService.class);
		if (!aclService.getListeners().contains(this)) {
			aclService.addListener(this);
			chatClient.getStream().addErrorListener(this);
		}
		
		AccessControlList acl = aclService.getLocal();
		ListView lvDevices = findViewById(R.id.lv_devices);
		devicesAdapter = new DevicesAdapter(this, acl);
		lvDevices.setAdapter(devicesAdapter);
		
		if (acl == null) {
			retrieveDevices(aclService);
		}
	}
	
	private void retrieveDevices(IAclService aclService) {
		runOnUiThread(() -> Toast.makeText(this, getString(R.string.retriving_your_devices), Toast.LENGTH_LONG).show());
		
		aclService.retrieve();
		
		ProgressBar pbRetrievingDevices = findViewById(R.id.pb_retrieving_devices);
		pbRetrievingDevices.setVisibility(View.VISIBLE);
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
		if (item.getItemId() == R.id.item_refresh_devices) {
			refreshDevices();
			return true;
		} else if (item.getItemId() == R.id.item_authorize_device) {
			authorizeDevice();
			return true;
		} else if (item.getItemId() == R.id.item_logout) {
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
		runOnUiThread(() ->Toast.makeText(MainActivity.this, getString(R.string.device_has_authorized, deviceId), Toast.LENGTH_LONG).show());
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
		runOnUiThread(() -> Toast.makeText(MainActivity.this, getString(R.string.failed_to_authorize_device, error.getReason()), Toast.LENGTH_LONG).show());

	}

	@Override
	public void occurred(IOperator.ConfirmationError error, String concentratorId, String nodeId) {
		// NOOP
	}

	@Override
	public void retrived(AccessControlList acl) {
		ProgressBar pbRetrievingDevices = (ProgressBar)findViewById(R.id.pb_retrieving_devices);
		pbRetrievingDevices.setVisibility(View.INVISIBLE);
		
		devicesAdapter.setAcl(acl);
		runOnUiThread(() -> devicesAdapter.notifyDataSetChanged());
	}

	@Override
	public void updated(AccessControlList acl) {
		devicesAdapter.updateAcl(acl);
		runOnUiThread(() -> {
			devicesAdapter.notifyDataSetChanged();
			Toast.makeText(MainActivity.this, getString(R.string.devices_has_updated), Toast.LENGTH_LONG).show();
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
		runOnUiThread(() -> {
			if (error instanceof StreamError) {
				Toast.makeText(MainActivity.this, getString(R.string.stream_error_occurred, error.getDefinedCondition()), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(MainActivity.this, getString(R.string.stanza_error_occurred, error.getDefinedCondition()), Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public void takeAPhoto(String deviceId) {
		System.out.println(String.format("Take a photo from device %s.", deviceId));
	}
	
	public void takeAVideo(String deviceId) {
		System.out.println(String.format("Take a video from device %s.", deviceId));
	}
	
	public void openLiveSteaming(String deviceId) {
		System.out.println(String.format("Open the live streaming of device %s.", deviceId));
	}
}
