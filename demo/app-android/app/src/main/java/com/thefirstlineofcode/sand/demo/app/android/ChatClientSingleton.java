package com.thefirstlineofcode.sand.demo.app.android;

import android.content.Context;

import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.chalk.android.StandardChatClient;
import com.thefirstlineofcode.sand.client.operator.OperatorPlugin;
import com.thefirstlineofcode.sand.demo.client.DemoPlugin;

class ChatClientSingleton {
	private static IChatClient chatClient;

	static IChatClient get(Context context) {
		if (chatClient == null) {
			chatClient = new StandardChatClient(Toolkits.getStreamConfig(context));
			chatClient.register(OperatorPlugin.class);
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