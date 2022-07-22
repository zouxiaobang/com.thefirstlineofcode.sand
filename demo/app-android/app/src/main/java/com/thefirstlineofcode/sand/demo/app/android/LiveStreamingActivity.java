package com.thefirstlineofcode.sand.demo.app.android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveStreamingActivity extends AppCompatActivity {
	private static final Logger logger = LoggerFactory.getLogger(LiveStreamingActivity.class);
	public static final int MEDIAS_PERMISSIONS_REQUEST_CODE = 2;
	
	private WebView webView;
	
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
		}
		
		setContentView(R.layout.activity_live_streaming);
		
		webView = findViewById(R.id.webview);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		webView.setWebViewClient(new LiveStreamingWebViewClient());
		webView.setWebChromeClient(new LiveStreamingWebChromeClient());
		webView.addJavascriptInterface(this, "androidApp");
		
		String host = ChatClientSingleton.get(this).getStreamConfig().getHost();
		webView.loadUrl(String.format("https://%s/index.html", host));
	}
	
	private boolean checkPermission(String permission) {
		return ContextCompat.checkSelfPermission(LiveStreamingActivity.this,
				permission) == PackageManager.PERMISSION_GRANTED;
	}
	
	class LiveStreamingWebViewClient extends WebViewClient {
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}
	}
	
	class LiveStreamingWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			return super.onJsAlert(view, url, message, result);
		}
		
		@Override
		public void onPermissionRequest(PermissionRequest request) {
			request.grant(request.getResources());
		}
	}
	@JavascriptInterface
	public void offerSdp(String message) {
		runOnUiThread(() -> Toast.makeText(LiveStreamingActivity.this,
				String.format("Received offer SDP message: %s.", message),
				Toast.LENGTH_LONG).show());
	}
}
