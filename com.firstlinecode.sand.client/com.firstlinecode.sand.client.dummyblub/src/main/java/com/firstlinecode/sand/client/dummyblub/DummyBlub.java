package com.firstlinecode.sand.client.dummyblub;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.firstlinecode.sand.client.dummything.IDummyThing;

public class DummyBlub extends JPanel implements ActionListener, IDummyThing {
	private static final long serialVersionUID = 7977420920860074017L;

	public enum SwitchState {
		ON,
		OFF,
		CONTROL
	}
	
	public enum BlubState {
		ON,
		OFF
	}
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final BlubState DEFAULT_BLUB_STATE = BlubState.OFF;
	
	private static String FILE_NAME_BLUB_OFF = "blub_off.png";
	private static String FILE_NAME_BLUB_ON = "blub_on.png";

	
	private JPanel radioPanel = new JPanel(new GridLayout(0, 1));
	private JLabel blubImage;
	private JButton flash = new JButton("Flash");
	
	private SwitchState switchState = DEFAULT_SWITCH_STATE;
	private BlubState blubState = DEFAULT_BLUB_STATE;
	
	public DummyBlub() {
		this(DEFAULT_SWITCH_STATE, DEFAULT_BLUB_STATE);
	}
	
	public DummyBlub(SwitchState switchState) {
		this(switchState, switchState == SwitchState.ON ? BlubState.ON : BlubState.OFF);
	}
	
	public DummyBlub(SwitchState switchState, BlubState blubState) {
		super(new BorderLayout());
		
		if (switchState == null)
			throw new IllegalArgumentException("Null switch state.");
		
		if (blubState == null)
			throw new IllegalArgumentException("Null blub state.");
		
		if (switchState == SwitchState.ON && blubState == BlubState.OFF ||
				switchState == SwitchState.OFF && blubState == BlubState.ON) {
			throw new IllegalStateException(String.format("Invalid dummy blub states. Switch state: %s. Blub state: %s.", switchState, blubState));
		}
		
		this.switchState = switchState;
		this.blubState = blubState;
		
		
		JRadioButton off = new JRadioButton("Turn Off");
		off.setMnemonic(KeyEvent.VK_F);
		off.setActionCommand("off");
		if (switchState == SwitchState.OFF)
			off.setSelected(true);
		
		JRadioButton on = new JRadioButton("Turn On");
		on.setMnemonic(KeyEvent.VK_N);
		on.setActionCommand("on");
		if (switchState == SwitchState.ON)
			on.setSelected(true);
		
		JRadioButton control = new JRadioButton("Remote Control");
		control.setMnemonic(KeyEvent.VK_R);
		control.setActionCommand("Remote Control");
		control.setSelected(true);
		if (switchState == SwitchState.CONTROL)
			control.setSelected(true);
		
		ButtonGroup group = new ButtonGroup();
		group.add(off);
		group.add(on);
		group.add(control);
		
		off.addActionListener(this);
		on.addActionListener(this);
		control.addActionListener(this);
		
		blubImage = new JLabel(getBlubImageIcon(blubState));
		
		radioPanel.add(off);
		radioPanel.add(on);
		radioPanel.add(control);
		
		add(radioPanel, BorderLayout.LINE_START);
		add(blubImage, BorderLayout.CENTER);
		
		add(flash, BorderLayout.SOUTH);
		flash.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				flashBlub();
			}

			private void flashBlub() {
				radioPanel.setEnabled(false);
				flash.setEnabled(false);
				
				blubImage.setIcon(getBlubImageIcon(BlubState.ON));
				
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						blubImage.setIcon(getBlubImageIcon(BlubState.OFF));
						
						flash.setEnabled(true);
						radioPanel.setEnabled(true);
					}
					
				}, 50);
			}
		});
		
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setPreferredSize(new Dimension(560, 280));
	}
	
	protected ImageIcon getBlubImageIcon(BlubState blubState) {
		if (blubState == null) {
			throw new IllegalArgumentException("Null blub state.");
		}
		
		String path = blubState == BlubState.ON ? "/images/" + FILE_NAME_BLUB_ON : "/images/" + FILE_NAME_BLUB_OFF;
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			throw new RuntimeException("Couldn't find file: " + path);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("off")) {
			switchState = SwitchState.OFF;
			blubState = BlubState.OFF;
			blubImage.setIcon(getBlubImageIcon(blubState));
			flash.setEnabled(true);
		} else if (actionCommand.equals("on")) {
			switchState = SwitchState.ON;
			blubState = BlubState.ON;
			blubImage.setIcon(getBlubImageIcon(blubState));			
			flash.setEnabled(false);
		} else {
			if (switchState == SwitchState.CONTROL)
				switchState = SwitchState.CONTROL;
			flash.setEnabled(false);			
		}
	}
	
	public SwitchState getSwitchState() {
		return switchState;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

	@Override
	public String getThingName() {
		return "Dummy Blub";
	}

	@Override
	public String getThingsName() {
		return "Dummy Blubs";
	}

	@Override
	public String getThingInstanceName(int index) {
		return String.format("%s #%d", getThingName(), index);
	}

}
