package com.firstlinecode.sand.emulators.things.ui;

import java.awt.event.WindowEvent;

import com.firstlinecode.chalk.core.IChatClient;
import com.firstlinecode.chalk.network.ConnectionException;
import com.firstlinecode.chalk.network.IConnectionListener;

public class InternetLogConsolePanel extends AbstractLogConsolePanel implements IConnectionListener {
	private static final long serialVersionUID = -7218394171950030532L;
	
	private IChatClient chatClient;
	
	public InternetLogConsolePanel() {
		super();
	}
	
	public InternetLogConsolePanel(IChatClient chatClient) {
		super();
		
		this.chatClient = chatClient;
		if (chatClient != null) {
			chatClient.getStream().addConnectionListener(this);
		}
	}
	
	public void setChatClient(IChatClient chatClient) {
		if (chatClient != null) {
			chatClient.getStream().removeConnectionListener(this);
		}
		
		this.chatClient = chatClient;
		chatClient.getStream().addConnectionListener(this);
	}

	@Override
	public void exceptionOccurred(ConnectionException exception) {
		log(exception);
	}

	@Override
	public void messageReceived(String message) {
		log("S-->G: " + message);
	}

	@Override
	public void messageSent(String message) {
		log("G-->S: " + message);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		if (chatClient != null)
			chatClient.getStream().removeConnectionListener(this);
	}

	@Override
	public void heartBeatsReceived(int length) {
		// Do nothing.	
	}
	
}
