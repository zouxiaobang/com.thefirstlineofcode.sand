package com.thefirstlineofcode.sand.emulators.things.ui;

import java.awt.event.WindowEvent;

import com.thefirstlineofcode.chalk.network.ConnectionException;
import com.thefirstlineofcode.chalk.network.IConnectionListener;
import com.thefirstlineofcode.sand.client.things.obm.IObmFactory;

public class InternetLogConsolePanel extends AbstractLogConsolePanel implements IConnectionListener {
	private static final long serialVersionUID = -7218394171950030532L;
	
	public InternetLogConsolePanel(IObmFactory obmFactory) {
		super(obmFactory);
	}

	@Override
	public void exceptionOccurred(ConnectionException exception) {
		log(exception);
	}

	@Override
	public void messageReceived(String message) {
		log("G<--S: " + message);
	}

	@Override
	public void messageSent(String message) {
		log("G-->S: " + message);
	}

	@Override
	public void heartBeatsReceived(int length) {
		// Do nothing.
	}

	@Override
	protected void doWindowClosing(WindowEvent e) {}
	
}
