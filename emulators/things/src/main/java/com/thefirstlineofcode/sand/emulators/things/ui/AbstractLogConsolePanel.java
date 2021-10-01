package com.thefirstlineofcode.sand.emulators.things.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.thefirstlineofcode.basalt.oxm.binary.BinaryUtils;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.gem.protocols.bxmpp.BinaryMessageProtocolReader;
import com.thefirstlineofcode.sand.client.things.obm.IObmFactory;
import com.thefirstlineofcode.sand.client.things.obm.ObmFactory;
import com.thefirstlineofcode.sand.emulators.things.ILogger;
import com.thefirstlineofcode.sand.protocols.lora.dac.Allocated;
import com.thefirstlineofcode.sand.protocols.lora.dac.Allocation;
import com.thefirstlineofcode.sand.protocols.lora.dac.Introduction;

public abstract class AbstractLogConsolePanel extends JPanel implements ILogger, WindowListener {
	private static final long serialVersionUID = 2661118467157999059L;
	
	protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

	protected final Map<Protocol, Class<?>> protocolToTypes = new HashMap<>();
	
	private JTextArea logConsole;
	private JButton clear;
	private final IObmFactory obmFactory = ObmFactory.createInstance();
	private final BinaryMessageProtocolReader bMessageProtocolReader;
	
	public AbstractLogConsolePanel() {
		bMessageProtocolReader = new BinaryMessageProtocolReader(((ObmFactory)ObmFactory.createInstance()).getBinaryXmppProtocolConverter());
		registerAddressConfigurationProtocolTypes();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		logConsole = new JTextArea();
		logConsole.setAutoscrolls(true);
		Font font = logConsole.getFont();
		if (font.getSize() > 8)
			logConsole.setFont(new Font("LogFont", font.getStyle(), font.getSize() - 8));
		
		add(new JScrollPane(logConsole));
		
		clear = new JButton("Clear Console");
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logConsole.setText(null);
			}
		});
		add(clear);
	}
	
	public void log(Exception e) {
		StringWriter out = new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		logConsole.append(out.getBuffer().toString());
		logConsole.append(LINE_SEPARATOR);
		
		logConsole.setCaretPosition(logConsole.getDocument().getLength() - 1);
	}
	
	public void log(String message) {
		logConsole.append(message);
		logConsole.append(LINE_SEPARATOR);
		
		logConsole.setCaretPosition(logConsole.getDocument().getLength());
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		doWindowClosing(e);
	}
	
	protected abstract void doWindowClosing(WindowEvent e);

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	private void registerAddressConfigurationProtocolTypes() {
		protocolToTypes.put(Allocated.PROTOCOL, Allocated.class);
		protocolToTypes.put(Allocation.PROTOCOL, Allocation.class);
		protocolToTypes.put(Introduction.PROTOCOL, Introduction.class);
	}

	protected Object parseProtocol(byte[] data) {
		Protocol protocol = bMessageProtocolReader.readProtocol(data);
		if (protocol == null) {
			throw new RuntimeException(String.format("Unknown protocol. Data is %s.", BinaryUtils.getHexStringFromBytes(data)));
		}

		Class<?> actionType = protocolToTypes.get(protocol);
		if (actionType == null) {
			throw new RuntimeException(String.format("Action not supported. Protocol is %s.", protocol));
		}

		return obmFactory.toObject(actionType, data);
	}
}
