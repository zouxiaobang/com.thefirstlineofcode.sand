package com.thefirstlineofcode.sand.demo.app.android;

import android.content.Context;

import com.thefirstlineofcode.chalk.android.StandardChatClient;
import com.thefirstlineofcode.sand.client.operator.OperatorPlugin;
import com.thefirstlineofcode.sand.client.remoting.RemotingPlugin;
import com.thefirstlineofcode.sand.client.things.simple.camera.CameraPlugin;
import com.thefirstlineofcode.sand.client.webcam.WebcamPlugin;
import com.thefirstlineofcode.sand.demo.client.DemoPlugin;

class ChatClientSingleton {
	private static StandardChatClient chatClient;

	static StandardChatClient get(Context context) {
		if (chatClient == null) {
			chatClient = new StandardChatClient(Toolkits.getStreamConfig(context));
			chatClient.register(OperatorPlugin.class);
			chatClient.register(RemotingPlugin.class);
			chatClient.register(CameraPlugin.class);
			chatClient.register(WebcamPlugin.class);
			chatClient.register(DemoPlugin.class);
		}

		return chatClient;
	}

	static void destroy() {
		if (chatClient == null)
			return;

		if (chatClient.isConnected())
			chatClient.close();

		chatClient = null;
	}
}
