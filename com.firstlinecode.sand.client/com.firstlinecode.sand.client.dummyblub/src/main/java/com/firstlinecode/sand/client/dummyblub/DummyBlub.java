package com.firstlinecode.sand.client.dummyblub;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.firstlinecode.sand.client.dummything.AbstractDummyThing;
import com.firstlinecode.sand.client.dummything.AbstractDummyThingPanel;
import com.firstlinecode.sand.client.dummything.IDummyThing;

public class DummyBlub extends AbstractDummyThing implements IDummyThing, IBlub {
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final BlubState DEFAULT_BLUB_STATE = BlubState.OFF;
	
	private SwitchState switchState = DEFAULT_SWITCH_STATE;
	private BlubState blubState = DEFAULT_BLUB_STATE;
	
	private DummyBlubPanel panel;
	
	public DummyBlub() {
		this(DEFAULT_SWITCH_STATE, DEFAULT_BLUB_STATE);
	}
	
	public DummyBlub(SwitchState switchState) {
		this(switchState, switchState == SwitchState.ON ? BlubState.ON : BlubState.OFF);
	}
	
	public DummyBlub(SwitchState switchState, BlubState blubState) {
		super(DummyBlubFactory.THING_NAME);
		
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
	}

	private class DummyBlubPanel extends AbstractDummyThingPanel implements ActionListener {
		private static final long serialVersionUID = 7660599095831708565L;
		
		private static final String FILE_NAME_BLUB_OFF = "blub_off.png";
		private static final String FILE_NAME_BLUB_ON = "blub_on.png";

		private JPanel radioPanel;
		private JLabel blubImage;
		
		public DummyBlubPanel() {
			super(DummyBlub.this);
		}
		
		@Override
		protected JPanel createThingCustomizedUi() {			
			JPanel customizedUi = new JPanel();
			
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
			
			radioPanel = new JPanel(new GridLayout(0, 1));
			radioPanel.add(off);
			radioPanel.add(on);
			radioPanel.add(control);
			
			customizedUi.add(radioPanel, BorderLayout.LINE_START);
			customizedUi.add(blubImage, BorderLayout.CENTER);
			
			customizedUi.setPreferredSize(new Dimension(360, 180));
			
			return customizedUi;
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
				doTurnOff();
			} else if (actionCommand.equals("on")) {
				doTurnOn();
			} else {
				if (switchState == SwitchState.CONTROL)
					switchState = SwitchState.CONTROL;
			}
		}
		
		private void updateStatus() {
			panel.updateStatus(getThingStatus());
		}
	}

	@Override
	protected void doWriteExternal(ObjectOutput out) throws IOException {
		out.writeObject(blubState);
		out.writeObject(switchState);
	}

	@Override
	protected void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		blubState = (BlubState)in.readObject();
		switchState = (SwitchState)in.readObject();
	}
	
	public SwitchState getSwitchState() {
		return switchState;
	}

	@Override
	public BlubState getBlubState() {
		return blubState;
	}

	@Override
	public void turnOn() throws NotRemoteControlStateException {
		if (switchState != SwitchState.CONTROL)
			throw new NotRemoteControlStateException(switchState);
		
		doTurnOn();
	}

	private void doTurnOn() {
		switchState = SwitchState.ON;
		if (powered) {			
			lightBlub();
		}
	}

	@Override
	public void turnOff() throws NotRemoteControlStateException {
		if (switchState != SwitchState.CONTROL)
			throw new NotRemoteControlStateException(switchState);
		
		doTurnOff();
	}

	private void doTurnOff() {
		switchState = SwitchState.OFF;
		
		if (powered) {			
			unlightBlub();
		}		
	}

	@Override
	public void flash() throws NotRemoteControlStateException, NotTurnOffStateException {
		panel.radioPanel.setEnabled(false);
		
		panel.blubImage.setIcon(panel.getBlubImageIcon(BlubState.ON));
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				panel.blubImage.setIcon(panel.getBlubImageIcon(BlubState.OFF));				
				panel.radioPanel.setEnabled(true);
			}
			
		}, 50);
	}

	@Override
	public AbstractDummyThingPanel getPanel() {
		panel = new DummyBlubPanel();
		panel.updateStatus();
		
		return panel;
	}

	@Override
	protected void doReset() {
		doTurnOff();
	}

	@Override
	protected void doPowerOn() {
		if (switchState == SwitchState.ON || blubState == BlubState.ON)
			lightBlub();
	}

	private void lightBlub() {
		blubState = BlubState.ON;
		panel.blubImage.setIcon(panel.getBlubImageIcon(blubState));
	}

	private void unlightBlub() {
		blubState = BlubState.OFF;
		panel.blubImage.setIcon(panel.getBlubImageIcon(blubState));
	}

	@Override
	protected void doPowerOff() {
		unlightBlub();
	}
}
