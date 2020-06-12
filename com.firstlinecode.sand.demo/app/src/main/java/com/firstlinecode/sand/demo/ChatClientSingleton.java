package com.firstlinecode.sand.demo;

import android.content.Context;

import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.chalk.android.StandardChatClient;

public class ChatClientSingleton {
	private static IChatClient chatClient;

	public static IChatClient get(Context context) {
		if (chatClient == null)
			chatClient = new StandardChatClient(Toolkits.getStreamConfig(context));

		return chatClient;
	}
}
