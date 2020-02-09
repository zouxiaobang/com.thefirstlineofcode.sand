package com.firstlinecode.sand.emulators.gateway.log;

import java.awt.event.WindowEvent;

import com.firstlinecode.chalk.IChatClient;
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
			chatClient.addConnectionListener(this);
		}
	}
	
	public void setChatClient(IChatClient chatClient) {
		if (chatClient != null) {
			chatClient.removeConnectionListener(this);
		}
		
		this.chatClient = chatClient;
		chatClient.addConnectionListener(this);
	}

	@Override
	public void occurred(ConnectionException exception) {
		log(exception);
	}

	@Override
	public void received(String message) {
		log("S-->G: " + message);
	}

	@Override
	public void sent(String message) {
		log("G-->S: " + message);
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {
		if (chatClient != null)
			chatClient.removeConnectionListener(this);
	}
	
}
