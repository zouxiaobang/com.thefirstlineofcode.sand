package com.thefirstlineofcode.sand.demo.app.android;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thefirstlineofcode.basalt.xmpp.core.IError;
import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;
import com.thefirstlineofcode.basalt.xmpp.core.stream.error.StreamError;
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
import com.thefirstlineofcode.sand.protocols.things.simple.light.TurnOff;
import com.thefirstlineofcode.sand.protocols.things.simple.light.TurnOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements IOperator.Listener,
		IAuthorizedDevicesService.Listener, IErrorListener {
	private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
	
	private String host;
	private DevicesAdapter devicesAdapter;
	private File downloadDir;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toolbar tbToolbar = findViewById(R.id.tb_tool_bar);
		tbToolbar.setTitle(R.string.app_name);
		setSupportActionBar(tbToolbar);
		
		downloadDir = new File(getCacheDir(), "download-dir");
		if (!downloadDir.exists()) {
			try {
				Files.createDirectory(downloadDir.toPath());
			} catch (IOException e) {
				throw new RuntimeException("Can't create download directory.", e);
			}
		}
		
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
			
			ProgressBar pbRetrievingDevices = findViewById(R.id.pb_retrieving_devices);
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
		ProgressBar pbRetrievingDevices = findViewById(R.id.pb_retrieving_devices);
		pbRetrievingDevices.setVisibility(View.INVISIBLE);
		
		runOnUiThread(() -> Toast.makeText(MainActivity.this,
				getString(R.string.retrieve_authorized_devices_timeout),
				Toast.LENGTH_LONG).show());
	}
	
	public void takeAPhoto(JabberId target) {
		logger.info("Take a photo from camera {}.", target);
		
		IChatClient chatClient = ChatClientSingleton.get(MainActivity.this);
		IRemoting remoting = chatClient.createApi(IRemoting.class);
		
		remoting.execute(target, new TakePhoto(), 30 * 1000, new IRemoting.Callback() {
			@Override
			public void executed(Object xep) {
				String photoUrl = ((TakePhoto)xep).getPhotoUrl();
				Spanned spanned = Html.fromHtml("Your photo was taken. Download address is <a href=\"" +
						photoUrl + "\">" + photoUrl + "</a>", FROM_HTML_MODE_COMPACT);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Your photo").setMessage(spanned).
					setPositiveButton(string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
							AlertDialog.Builder photoDialogBuilder = new AlertDialog.Builder(MainActivity.this);
							View photoView = LayoutInflater.from(MainActivity.this).inflate(R.layout.photo_view, null, false);
							ImageView ivPhoto = photoView.findViewById(R.id.iv_photo);
							ProgressBar pbDownloadingPhoto = photoView.findViewById(R.id.pb_downloading_photo);
							photoDialogBuilder.setView(photoView);
							photoDialogBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							
							runOnUiThread(() -> {
								AlertDialog photoDialog = photoDialogBuilder.create();
								photoDialog.show();
							});
							
							DownloadingPhotoTask downloadingPhotoTask = new DownloadingPhotoTask(
									MainActivity.this, pbDownloadingPhoto, ivPhoto, downloadDir);
							downloadingPhotoTask.execute((TakePhoto)xep);
						}
					}).
					setNegativeButton(string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}
				);
				
				runOnUiThread(() -> {
					AlertDialog dialog = builder.create();
					dialog.show();
				});
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
	
	private static class DownloadingPhotoTask extends AsyncTask<TakePhoto, Void, File> {
		private final Context context;
		private ProgressBar pbDownloadingPhoto;
		private ImageView ivPhoto;
		private File downloadDir;
		
		public DownloadingPhotoTask(Context context, ProgressBar pbDownloadingPhoto,
					ImageView ivPhoto, File downloadDir) {
			this.context = context;
			this.pbDownloadingPhoto = pbDownloadingPhoto;
			this.ivPhoto = ivPhoto;
			this.downloadDir = downloadDir;
		}
		
		@Override
		protected void onPreExecute() {
			pbDownloadingPhoto.setVisibility(View.VISIBLE);
			ivPhoto.setVisibility(View.INVISIBLE);
		}
		
		@Override
		protected File doInBackground(TakePhoto... takePhotos) {
			TakePhoto takePhoto = takePhotos[0];
			OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).build();
			Call call = client.newCall(getPhotoDownloadingRequest(takePhoto.getPhotoUrl()));
			Response response;
			try {
				response = call.execute();
			} catch (IOException e) {
				return null;
			}
			
			if (response.code() != 200) {
				return null;
			}
			
			return downloadPhoto(response, takePhoto.getPhotoFileName());
		}
		
		@Override
		protected void onPostExecute(File downloadPhoto) {
			if (downloadPhoto == null) {
				Toast.makeText(context,
						context.getString(R.string.failed_to_download_photo),
						Toast.LENGTH_LONG).show();
			} else {
				pbDownloadingPhoto.setVisibility(View.GONE);
				ivPhoto.setImageURI(Uri.fromFile(downloadPhoto));
				ivPhoto.setVisibility(View.VISIBLE);
			}
		}
		
		private Request getPhotoDownloadingRequest(String photoUrl) {
			return new Request.Builder().url(photoUrl).build();
		}
		
		private File downloadPhoto(Response response, String photoFileName) {
			File downloadedPhoto = new File(downloadDir, photoFileName);
			InputStream input = null;
			BufferedOutputStream output = null;
			byte[] buf = new byte[2048];
			int len;
			try {
				input = response.body().byteStream();
				output = new BufferedOutputStream(new FileOutputStream(downloadedPhoto));
				while ((len = input.read(buf, 0, 2048)) != -1) {
					output.write(buf, 0, len);
				}
				
				return downloadedPhoto;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return null;
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void takeAVideo(JabberId target) {
		logger.info("Take a video from camera {}.", target);
	}
	
	public void openLiveSteaming(JabberId target) {
		logger.info("Open live streaming of camera {}.", target);
		
		Intent intent = new Intent(this, LiveStreamingActivity.class);
		intent.putExtra("camera-jid", target.toString());
		startActivity(intent);
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
		builder.setTitle("Restart").setMultiChoiceItems(new String[] {"Restart after shutdown"}, new boolean[] {false},
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						shutdownSystem.setRestart(isChecked);
					}
				}
			).setPositiveButton(string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						
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
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			}
		);
		
		runOnUiThread(() -> {
			AlertDialog dialog = builder.create();
			dialog.show();
		});
	}
	
	public void flash(JabberId target) {
		logger.info("Flash light {}.", target);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose repeat times").setItems(new String[] {"1", "2", "5"},
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
						Flash flash = new Flash();
						if (which == 0) {
							flash.setRepeat(1);
						} else if (which == 1) {
							flash.setRepeat(2);
						} else {
							flash.setRepeat(5);
						}

						executeAction(target, flash, "Flash");
					}
				});
		
		runOnUiThread(() -> {
			AlertDialog dialog = builder.create();
			dialog.show();
		});
	}
	
	public void turnOn(JabberId target) {
		logger.info("Turn on light {}.", target);
		executeAction(target, new TurnOn(), "Turn On");
	}
	
	public void turnOff(JabberId target) {
		logger.info("Turn off light {}.", target);
		executeAction(target, new TurnOff(), "Turn Off");
	}

	private void executeAction(JabberId target, Object action, String actionDescription) {
		IChatClient chatClient = ChatClientSingleton.get(this);
		IRemoting remoting = chatClient.createApi(IRemoting.class);
		remoting.execute(target, action, new RemotingCallback(this, actionDescription));
	}

	private static class RemotingCallback implements IRemoting.Callback {
		private final Activity activity;
		private final String actionDescription;

		public RemotingCallback(Activity activity, String actionDescription) {
			this.activity = activity;
			this.actionDescription = actionDescription;
		}

		@Override
		public void executed(Object xep) {
			activity.runOnUiThread(() -> Toast.makeText(activity,
					actionDescription + " executed.",
					Toast.LENGTH_LONG).show());
		}

		@Override
		public void occurred(StanzaError error) {
			activity.runOnUiThread(() -> Toast.makeText(activity,
					actionDescription + " execution error: " + error.toString(),
					Toast.LENGTH_LONG).show());
		}

		@Override
		public void timeout() {
			activity.runOnUiThread(() -> Toast.makeText(activity,
					actionDescription + " execution timeout.",
					Toast.LENGTH_LONG).show());
		}
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
