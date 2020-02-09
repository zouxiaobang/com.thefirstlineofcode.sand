package com.firstlinecode.sand.emulators.gateway.log;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public abstract class AbstractLogConsolePanel extends JPanel implements ILogger, WindowListener {
	private static final long serialVersionUID = 2661118467157999059L;
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private JTextArea logConsole;
	private JButton clear;
	
	public AbstractLogConsolePanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		logConsole = new JTextArea();
		logConsole.setAutoscrolls(true);
		JScrollPane logConsoleScrollPane = new JScrollPane(logConsole);
		add(logConsoleScrollPane);
		
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
		
		logConsole.setCaretPosition(logConsole.getDocument().getLength() - 1);
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
}
