package com.firstlinecode.sand.client.dummygateway;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = -1872492975053174389L;

	public AboutDialog(JFrame parent, String version) {
		super(parent, "About Dialog", true);
		
		Box box = Box.createVerticalBox();
		box.add(Box.createVerticalStrut(20));
		box.add(new JLabel("Dummy Gateway " + version));
		
		JPanel creatorPanel = new JPanel();
		creatorPanel.setLayout(new BorderLayout());
		creatorPanel.add(new JLabel("Created by FirstLineCode."), "East");
		box.add(creatorPanel);
		
		getContentPane().add(box, "Center");
		
		JPanel okPanel = new JPanel();
		JButton ok = new JButton("Ok");
		okPanel.add(ok);
		getContentPane().add(okPanel, "South");
		
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setVisible(false);
			}
		});
		
		Rectangle bounds = parent.getBounds();
		int x = (int)(bounds.x + (bounds.getWidth() - 360) / 2);
		int y = (int)(bounds.y + (bounds.getHeight() - 200) / 2);
		setBounds(x, y, 480, 200);
	}
}
