package com.thefirstlineofcode.sand.demo.app.android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.sand.client.webcam.IWatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveStreamingActivity extends AppCompatActivity {
	private static final Logger logger = LoggerFactory.getLogger(LiveStreamingActivity.class);
	
	public static final int MEDIAS_PERMISSIONS_REQUEST_CODE = 2;
	
	private IWatcher watcher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (!checkPermission(Manifest.permission.CAMERA) ||
				!checkPermission(Manifest.permission.RECORD_AUDIO) ||
				!checkPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
			requestPermissions(
					new String[] {
							Manifest.permission.CAMERA,
							Manifest.permission.RECORD_AUDIO,
							Manifest.permission.MODIFY_AUDIO_SETTINGS
					}, MEDIAS_PERMISSIONS_REQUEST_CODE);
		} else {
			onCreate();
		}
	}
	
	private void onCreate() {
		String sCameraJid = getIntent().getStringExtra("camera-jid");
		JabberId cameraJid = JabberId.parse(sCameraJid);
		
		setContentView(R.layout.activity_live_streaming);
		
		WebView webView = findViewById(R.id.webview);
		watcher = ChatClientSingleton.get(this).createApiImpl(WebViewWatcher.class,
				new Class<?>[] {JabberId.class, WebView.class}, new Object[] {cameraJid, webView});
		watcher.watch();
	}
	
	private boolean checkPermission(String permission) {
		return ContextCompat.checkSelfPermission(LiveStreamingActivity.this,
				permission) == PackageManager.PERMISSION_GRANTED;
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == MEDIAS_PERMISSIONS_REQUEST_CODE) {
			onCreate();
		} else {
			new AlertDialog.Builder(this).
					setTitle("Error").
					setMessage("User denied permissions request. App will exit.").
					setPositiveButton("Ok", (dialog, which) -> {finish();}).
					create().show();
		}
	}
}
