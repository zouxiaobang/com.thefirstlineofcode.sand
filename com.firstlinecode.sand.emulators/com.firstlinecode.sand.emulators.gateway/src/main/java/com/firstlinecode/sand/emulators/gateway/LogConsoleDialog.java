package com.firstlinecode.sand.emulators.gateway;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LogConsoleDialog extends JDialog {
	private static final long serialVersionUID = 5197344780011371803L;
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private JTextArea logConsole;
	private JButton clear;
	
	public LogConsoleDialog(Gateway<?, ?, ?> parent) {
		super(parent, "Log Console");
		
		getContentPane().add(createLogConsolePanel());
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(parent);
		
		setBounds(50, 50, 800, 480);
	}

	/**
	 * @return
	 */
	private JPanel createLogConsolePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		logConsole = new JTextArea();
		logConsole.setAutoscrolls(true);
		JScrollPane logConsoleScrollPane = new JScrollPane(logConsole);
		panel.add(logConsoleScrollPane);
		
		clear = new JButton("Clear Console");
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logConsole.setText(null);
			}
		});
		panel.add(clear);
		
		return panel;
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
}
