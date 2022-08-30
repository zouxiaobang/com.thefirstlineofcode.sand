package com.thefirstlineofcode.sand.demo.app.android;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.CookieManager;
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
import com.thefirstlineofcode.sand.client.webcam.IWebrtcPeer;
import com.thefirstlineofcode.sand.protocols.webrtc.signaling.Signal;

public class WebViewWatcher extends AbstractWatcher implements IWebrtcPeer.Listener {
	private Activity activity;
	private WebView webView;
	
	public WebViewWatcher(IChatServices chatServices, Activity activity,
				JabberId peer, WebView webView) {
		super(chatServices, peer);
		
		this.activity = activity;
		this.webView = webView;
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDomStorageEnabled(true);
		
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			// Hide the zoom controls for HONEYCOMB+
			webSettings.setDisplayZoomControls(false);
		}
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}
		
		webView.setWebViewClient(new LiveStreamingWebViewClient());
		webView.setWebChromeClient(new LiveStreamingWebChromeClient());
		webView.addJavascriptInterface(this, "androidApp");
		
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptThirdPartyCookies(webView, true);
	}
	
	@Override
	public void watch() {
		if (!isStarted())
			start();
		
		addListener(this);
		
		processSignal(Signal.ID.OPEN);
	}
	
	@Override
	public void opened() {
		super.opened();
		
		String host = chatServices.getStream().getStreamConfig().getHost();
		activity.runOnUiThread(() -> webView.loadUrl(String.format("https://%s/index.html", host)));
	}
	
	@Override
	public void close() {
		processSignal(Signal.ID.CLOSE);
		removeListener(this);
	}
	
	@Override
	public void offered(String offerSdp) {
		throw new IllegalStateException("Watcher received a offer SDP from the peer. Why???");
	}
	
	@Override
	public void answered(String answerSdp) {
		String lineSeparatorReplaceString = "$$";
		StringBuilder lineSeparatorsHiddenAnswerSdp = new StringBuilder();
		for (int i = 0; i < answerSdp.length(); i++) {
			char currentChar = answerSdp.charAt(i);
			if (currentChar != '\n')
				lineSeparatorsHiddenAnswerSdp.append(currentChar);
			else
				lineSeparatorsHiddenAnswerSdp.append(lineSeparatorReplaceString);
		}
		activity.runOnUiThread(() -> webView.loadUrl("javascript:answered(\"" + lineSeparatorsHiddenAnswerSdp.toString() + "\")"));
	}
	
	@Override
	public void iceCandidateFound(String candidate) {
		String quoteReplaceString = "$$";
		StringBuilder quotesHiddenCandidate = new StringBuilder();
		for (int i = 0; i < candidate.length(); i++) {
			char currentChar = candidate.charAt(i);
			if (currentChar != '"') {
				quotesHiddenCandidate.append(currentChar);
			} else {
				quotesHiddenCandidate.append(quoteReplaceString);
			}
		}
		activity.runOnUiThread(() ->
			webView.loadUrl("javascript:iceCandidateFound(\"" + quotesHiddenCandidate + "\")")
		);
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
		Signal.ID signalId;
		try {
			signalId = Enum.valueOf(Signal.ID.class, id);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(String.format("Unknown signal ID: %s.", id));
		}
		
		processSignal(signalId, data);
	}
}
