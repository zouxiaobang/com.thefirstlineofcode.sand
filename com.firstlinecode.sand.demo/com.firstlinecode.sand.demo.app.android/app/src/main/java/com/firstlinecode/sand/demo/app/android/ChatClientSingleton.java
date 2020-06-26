package com.firstlinecode.sand.demo.app.android;

import android.content.Context;

import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.chalk.android.StandardChatClient;
import com.firstlinecode.sand.client.operator.OperatorPlugin;

class ChatClientSingleton {
	private static IChatClient chatClient;

	static IChatClient get(Context context) {
		if (chatClient == null) {
			chatClient = new StandardChatClient(Toolkits.getStreamConfig(context));
			chatClient.register(OperatorPlugin.class);
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
