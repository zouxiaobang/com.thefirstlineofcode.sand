package com.firstlinecode.sand.demo.app.android;

import android.content.Context;

import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.chalk.android.StandardChatClient;

class ChatClientSingleton {
	private static IChatClient chatClient;

	static IChatClient get(Context context) {
		if (chatClient == null)
			chatClient = new StandardChatClient(Toolkits.getStreamConfig(context));

		return chatClient;
	}
}
