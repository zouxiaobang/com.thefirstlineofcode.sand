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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.firstlinecode.sand.client.dummything.AbstractDummyThing;
import com.firstlinecode.sand.client.dummything.AbstractDummyThingPanel;
import com.firstlinecode.sand.client.dummything.BatteryEvent;
import com.firstlinecode.sand.client.dummything.IDeviceListener;
import com.firstlinecode.sand.client.dummything.IDummyThing;
import com.firstlinecode.sand.client.dummything.PowerEvent;

public class DummyBlub extends AbstractDummyThing implements IDummyThing, IDeviceListener, IBlub {
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final BlubState DEFAULT_BLUB_STATE = BlubState.OFF;
	
	private SwitchState switchState = DEFAULT_SWITCH_STATE;
	private BlubState blubState = DEFAULT_BLUB_STATE;
	
	private JPanel switchsPanel;
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

		private JLabel blubImage;
		private JButton flash;
		
		public DummyBlubPanel() {
			super(DummyBlub.this);
			addDeviceListener(DummyBlub.this);
		}
		
		@Override
		protected JPanel createThingCustomizedUi() {			
			JPanel customizedUi = new JPanel(new BorderLayout());
			
			blubImage = new JLabel(getBlubImageIcon(blubState));
			switchsPanel = createSwitchsPanel();
			
			customizedUi.add(switchsPanel, BorderLayout.NORTH);
			customizedUi.add(blubImage, BorderLayout.CENTER);			
			customizedUi.add(createFlashPanel(), BorderLayout.SOUTH);
			
			customizedUi.setPreferredSize(new Dimension(360, 320));
			
			return customizedUi;
		}

		private JPanel createSwitchsPanel() {
			JRadioButton off = createOffButton();
			JRadioButton on = createOnButton();
			JRadioButton control = createControlButton();
			
			ButtonGroup group = new ButtonGroup();
			group.add(off);
			group.add(on);
			group.add(control);
			
			off.addActionListener(this);
			on.addActionListener(this);
			control.addActionListener(this);
			
			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(off);
			panel.add(on);
			panel.add(control);
			
			return panel;
		}

		private JRadioButton createControlButton() {
			JRadioButton control = new JRadioButton("Remote Control");
			control.setMnemonic(KeyEvent.VK_R);
			control.setActionCommand("Remote Control");
			control.setSelected(true);
			if (switchState == SwitchState.CONTROL)
				control.setSelected(true);
			return control;
		}

		private JRadioButton createOnButton() {
			JRadioButton on = new JRadioButton("Turn On");
			on.setMnemonic(KeyEvent.VK_N);
			on.setActionCommand("on");
			if (switchState == SwitchState.ON)
				on.setSelected(true);
			return on;
		}

		private JRadioButton createOffButton() {
			JRadioButton off = new JRadioButton("Turn Off");
			off.setMnemonic(KeyEvent.VK_F);
			off.setActionCommand("off");
			if (switchState == SwitchState.OFF)
				off.setSelected(true);
			return off;
		}

		private JPanel createFlashPanel() {
			flash = new JButton("Flash");
			flash.setPreferredSize(new Dimension(128, 48));
			
			JPanel flashPanel = new JPanel();
			flashPanel.add(flash);
			
			flash.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					flashBlub();
				}
			});
			
			return flashPanel;
		}
		
		private void flashBlub() {
			switchsPanel.setEnabled(false);
			flash.setEnabled(false);
			
			blubImage.setIcon(getBlubImageIcon(BlubState.ON));
			
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					blubImage.setIcon(getBlubImageIcon(BlubState.OFF));

					flash.setEnabled(true);
					switchsPanel.setEnabled(true);
				}

			}, 50);
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
				refreshFlashButtionStatus();
			} else if (actionCommand.equals("on")) {
				doTurnOn();
				refreshFlashButtionStatus();
			} else {
				doTurnOff();
				
				if (switchState != SwitchState.CONTROL)
					switchState = SwitchState.CONTROL;
				
				refreshFlashButtionStatus();
			}
		}

		private void updateStatus() {
			panel.updateStatus(getThingStatus());
		}
	}
	
	private void refreshFlashButtionStatus() {
		if (powered && switchState == SwitchState.OFF && blubState == BlubState.OFF) {
			panel.flash.setEnabled(true);
		} else {
			panel.flash.setEnabled(false);
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
		switchsPanel.setEnabled(false);
		
		panel.blubImage.setIcon(panel.getBlubImageIcon(BlubState.ON));
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				panel.blubImage.setIcon(panel.getBlubImageIcon(BlubState.OFF));				
				switchsPanel.setEnabled(true);
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

	@Override
	public void powerChanged(PowerEvent event) {
		refreshFlashButtionStatus();
	}

	@Override
	public void batteryChanged(BatteryEvent event) {}
}
