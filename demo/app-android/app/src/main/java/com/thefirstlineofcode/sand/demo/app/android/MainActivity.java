package com.thefirstlineofcode.sand.demo.app.android;

import android.R.string;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.basalt.protocol.core.stream.error.StreamError;
import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.core.IErrorListener;
import com.thefirstlineofcode.sand.client.operator.IOperator;
import com.thefirstlineofcode.sand.client.remoting.IRemoting;
import com.thefirstlineofcode.sand.demo.client.IAuthorizedDevicesService;
import com.thefirstlineofcode.sand.demo.protocols.AuthorizedDevice;
import com.thefirstlineofcode.sand.demo.protocols.AuthorizedDevices;
import com.thefirstlineofcode.sand.protocols.actuator.actions.Restart;
import com.thefirstlineofcode.sand.protocols.actuator.actions.ShutdownSystem;
import com.thefirstlineofcode.sand.protocols.actuator.actions.Stop;
import com.thefirstlineofcode.sand.protocols.things.simple.camera.TakePhoto;
import com.thefirstlineofcode.sand.protocols.things.simple.light.Flash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IOperator.Listener,
		IAuthorizedDevicesService.Listener, IErrorListener {
	private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
	
	private String host;
	private DevicesAdapter devicesAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toolbar tbToolbar = findViewById(R.id.tb_tool_bar);
		tbToolbar.setTitle(R.string.app_name);
		setSupportActionBar(tbToolbar);
		
		retrieveAuthorizedDevices();
	}
	
	private void retrieveAuthorizedDevices() {
		runOnUiThread(() ->
				Toast.makeText(this,
						getString(R.string.retrieving_your_devices),
						Toast.LENGTH_LONG).show());
		
		ProgressBar pbRetrievingDevices = findViewById(R.id.pb_retrieving_devices);
		pbRetrievingDevices.setVisibility(View.VISIBLE);
		
		IChatClient chatClient = ChatClientSingleton.get(this);
		host = chatClient.getStreamConfig().getHost();
		IAuthorizedDevicesService authorizedDevicesService =
				chatClient.createApi(IAuthorizedDevicesService.class);
		authorizedDevicesService.addListener(this);
		
		authorizedDevicesService.retrieve();
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
		retrieveAuthorizedDevices();
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
		runOnUiThread(() ->
				Toast.makeText(MainActivity.this,
						getString(R.string.device_has_authorized, deviceId),
						Toast.LENGTH_LONG).show());
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
		runOnUiThread(() ->
				Toast.makeText(MainActivity.this,
						getString(R.string.failed_to_authorize_device, error.getReason()),
						Toast.LENGTH_LONG).show());

	}

	@Override
	public void occurred(IOperator.ConfirmationError error, String concentratorId, String nodeId) {
		// NOOP
	}

	@Override
	public void retrieved(AuthorizedDevices authorizedDevices) {
		runOnUiThread(() -> {
			List<AuthorizedDevice> lDevices = authorizedDevices.getDevices();
			
			AuthorizedDevice[] devices = new AuthorizedDevice[0];
			if (lDevices != null && lDevices.size() != 0)
				devices = lDevices.toArray(new AuthorizedDevice[0]);
			
			if (devicesAdapter == null) {
				ListView lvDevices = findViewById(R.id.lv_devices);
				devicesAdapter = new DevicesAdapter(MainActivity.this, host, devices);
				lvDevices.setAdapter(devicesAdapter);
			} else {
				devicesAdapter.setDevices(devices);
				devicesAdapter.notifyDataSetChanged();
			}
			
			ProgressBar pbRetrievingDevices = (ProgressBar)findViewById(R.id.pb_retrieving_devices);
			pbRetrievingDevices.setVisibility(View.INVISIBLE);
		});
	}
	
	@Override
	public void occurred(StanzaError error) {
		ProgressBar pbRetrievingDevices = (ProgressBar)findViewById(R.id.pb_retrieving_devices);
		pbRetrievingDevices.setVisibility(View.INVISIBLE);
		
		runOnUiThread(() ->
				Toast.makeText(MainActivity.this,
						getString(R.string.stanza_error_occurred, error.getDefinedCondition()),
						Toast.LENGTH_LONG).show()
		);
	}
	
	@Override
	public void timeout() {
		ProgressBar pbRetrievingDevices = (ProgressBar)findViewById(R.id.pb_retrieving_devices);
		pbRetrievingDevices.setVisibility(View.INVISIBLE);
		
		runOnUiThread(() -> Toast.makeText(MainActivity.this,
				getString(R.string.retrieve_authorized_devices_timeout),
				Toast.LENGTH_LONG).show());
	}
	
	public void takeAPhoto(JabberId target) {
		logger.info("Take a photo from camera {}.", target);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose prepare time").setItems(new String[] {"5 Seconds", "10 Seconds", "15 Seconds"},
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						IChatClient chatClient = ChatClientSingleton.get(MainActivity.this);
						IRemoting remoting = chatClient.createApi(IRemoting.class);
						
						TakePhoto takePhoto = new TakePhoto();
						if (which == 0) {
							takePhoto.setPrepareTime(5);
						} else if (which == 1) {
							takePhoto.setPrepareTime(10);
						} else {
							takePhoto.setPrepareTime(15);
						}
						
						remoting.execute(target, takePhoto, 30 * 1000, new IRemoting.Callback() {
							@Override
							public void executed(Object xep) {
								runOnUiThread(() -> Toast.makeText(MainActivity.this,
										"Take photo executed.",
										Toast.LENGTH_LONG).show());
							}
							
							@Override
							public void occurred(StanzaError error) {
								runOnUiThread(() -> Toast.makeText(MainActivity.this,
										"Take photo execution error: " + error.toString(),
										Toast.LENGTH_LONG).show());
							}
							
							@Override
							public void timeout() {
								runOnUiThread(() -> Toast.makeText(MainActivity.this,
										"Take photo execution timeout.",
										Toast.LENGTH_LONG).show());
							}
						});
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void takeAVideo(JabberId target) {
		logger.info("Take a video from camera {}.", target);
	}
	
	public void openLiveSteaming(JabberId target) {
		logger.info("Open live streaming of camera {}.", target);
	}
	
	public void stop(JabberId target) {
		IChatClient chatClient = ChatClientSingleton.get(MainActivity.this);
		IRemoting remoting = chatClient.createApi(IRemoting.class);
		
		Stop stop = new Stop();
		remoting.execute(target, stop, new IRemoting.Callback() {
			@Override
			public void executed(Object xep) {
				runOnUiThread(() -> Toast.makeText(MainActivity.this,
						"Stop executed.",
						Toast.LENGTH_LONG).show());
			}
			
			@Override
			public void occurred(StanzaError error) {
				runOnUiThread(() -> Toast.makeText(MainActivity.this,
						"Stop execution error: " + error.toString(),
						Toast.LENGTH_LONG).show());
			}
			
			@Override
			public void timeout() {
				runOnUiThread(() -> Toast.makeText(MainActivity.this,
						"Stop execution timeout.",
						Toast.LENGTH_LONG).show());
			}
		});
	}
	
	public void restart(JabberId target) {
		IChatClient chatClient = ChatClientSingleton.get(MainActivity.this);
		IRemoting remoting = chatClient.createApi(IRemoting.class);
		
		Restart restart = new Restart();
		remoting.execute(target, restart, new IRemoting.Callback() {
			@Override
			public void executed(Object xep) {
				runOnUiThread(() -> Toast.makeText(MainActivity.this,
						"Restart executed.",
						Toast.LENGTH_LONG).show());
			}
			
			@Override
			public void occurred(StanzaError error) {
				runOnUiThread(() -> Toast.makeText(MainActivity.this,
						"Restart execution error: " + error.toString(),
						Toast.LENGTH_LONG).show());
			}
			
			@Override
			public void timeout() {
				runOnUiThread(() -> Toast.makeText(MainActivity.this,
						"Restart execution timeout.",
						Toast.LENGTH_LONG).show());
			}
		});
	}
	
	public void shutdownSystem(JabberId target) {
		ShutdownSystem shutdownSystem = new ShutdownSystem();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Restart").setMultiChoiceItems(new String[] {"Restart after shutdowning"}, new boolean[] {false},
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						if (isChecked) {
							shutdownSystem.setRestart(true);
						} else {
							shutdownSystem.setRestart(false);
						}
					}
				}
			).setPositiveButton(string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						IChatClient chatClient = ChatClientSingleton.get(MainActivity.this);
						IRemoting remoting = chatClient.createApi(IRemoting.class);
						
						remoting.execute(target, shutdownSystem, new IRemoting.Callback() {
							@Override
							public void executed(Object xep) {
								runOnUiThread(() -> Toast.makeText(MainActivity.this,
										"Shutdown system executed.",
										Toast.LENGTH_LONG).show());
							}
							
							@Override
							public void occurred(StanzaError error) {
								runOnUiThread(() -> Toast.makeText(MainActivity.this,
										"Shutdown system execution error: " + error.toString(),
										Toast.LENGTH_LONG).show());
							}
							
							@Override
							public void timeout() {
								runOnUiThread(() -> Toast.makeText(MainActivity.this,
										"Shutdown system execution timeout.",
										Toast.LENGTH_LONG).show());
							}
						});
					}
				}
			).setNegativeButton(string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {}
			}
		);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void flash(JabberId target) {
		logger.info("Flash light {}.", target);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose repeat times").setItems(new String[] {"1", "2", "5"},
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						IChatClient chatClient = ChatClientSingleton.get(MainActivity.this);
						IRemoting remoting = chatClient.createApi(IRemoting.class);
						
						Flash flash = new Flash();
						if (which == 0) {
							flash.setRepeat(1);
						} else if (which == 1) {
							flash.setRepeat(2);
						} else {
							flash.setRepeat(5);
						}
						
						remoting.execute(target, flash, new IRemoting.Callback() {
							@Override
							public void executed(Object xep) {
								runOnUiThread(() -> Toast.makeText(MainActivity.this,
										"Flash executed.",
										Toast.LENGTH_LONG).show());
							}
							
							@Override
							public void occurred(StanzaError error) {
								runOnUiThread(() -> Toast.makeText(MainActivity.this,
										"Flash execution error: " + error.toString(),
										Toast.LENGTH_LONG).show());
							}
							
							@Override
							public void timeout() {
								runOnUiThread(() -> Toast.makeText(MainActivity.this,
										"Flash execution timeout.",
										Toast.LENGTH_LONG).show());
							}
						});
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void turnOn(JabberId target) {
		logger.info("Turn on light {}.", target);
	}
	
	public void turnOff(JabberId target) {
		logger.info("Turn off light {}.", target);
	}
	
	public void changeMode(JabberId target) {
		logger.info("Change mode of gateway {}.", target);
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
}
