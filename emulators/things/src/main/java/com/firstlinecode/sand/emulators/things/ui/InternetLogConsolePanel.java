package com.firstlinecode.sand.emulators.things.ui;

import java.awt.event.WindowEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstlinecode.chalk.core.IChatClient;
import com.firstlinecode.chalk.network.ConnectionException;
import com.firstlinecode.chalk.network.IConnectionListener;

public class InternetLogConsolePanel extends AbstractLogConsolePanel implements IConnectionListener {
	private static final long serialVersionUID = -7218394171950030532L;
	
	private static final Logger logger = LoggerFactory.getLogger(InternetLogConsolePanel.class);
	
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
		String logMessage = "G<--S: " + message;
		
		if (logger.isDebugEnabled())
			logger.debug(logMessage);
		
		log(logMessage);
	}

	@Override
	public void messageSent(String message) {
		String logMessage = "G-->S: " + message;
		
		if (logger.isDebugEnabled())
			logger.debug(logMessage);
		
		log(logMessage);
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
