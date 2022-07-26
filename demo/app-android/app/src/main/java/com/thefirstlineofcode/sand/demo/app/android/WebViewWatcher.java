package com.thefirstlineofcode.sand.demo.app.android;

import android.net.http.SslError;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.thefirstlineofcode.basalt.xmpp.core.JabberId;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.sand.client.webcam.AbstractWatcher;

public class WebViewWatcher extends AbstractWatcher {
	private WebView webView;
	
	public WebViewWatcher(IChatServices chatServices, JabberId peer, WebView webView) {
		super(chatServices, peer);
		
		this.webView = webView;
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		webView.setWebViewClient(new LiveStreamingWebViewClient());
		webView.setWebChromeClient(new LiveStreamingWebChromeClient());
		webView.addJavascriptInterface(this, "androidApp");
	}
	
	@Override
	public void start() {
		String host = chatServices.getStream().getStreamConfig().getHost();
		webView.loadUrl(String.format("https://%s/index.html", host));
	}
	
	@Override
	public void stop() {
	
	}
	
	@Override
	public void close() {
		stop();
	}
	
	private static class LiveStreamingWebViewClient extends WebViewClient {
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}
	}
	
	private static class LiveStreamingWebChromeClient extends WebChromeClient {
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
	public void processJavascriptSignal(String id, String data) {
		processSignal(id, data);
	}
}
