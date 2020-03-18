package com.firstlinecode.sand.emulators.blub;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.emulators.thing.AbstractThingEmulator;
import com.firstlinecode.sand.emulators.thing.AbstractThingEmulatorPanel;
import com.firstlinecode.sand.emulators.thing.PowerEvent;

public class Blub extends AbstractThingEmulator implements IBlub {
	public static final String THING_TYPE = "Blub";
	public static final String THING_MODE = "Blub-Emulator-01";
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final BlubState DEFAULT_BLUB_STATE = BlubState.OFF;
	
	private SwitchState switchState = DEFAULT_SWITCH_STATE;
	private BlubState blubState = DEFAULT_BLUB_STATE;
	
	private JPanel switchsPanel;
	private BlubEmulatorPanel panel;
	
	public Blub(LoraCommunicator communicator) {
		this(communicator, DEFAULT_SWITCH_STATE, DEFAULT_BLUB_STATE);
	}
	
	public Blub(LoraCommunicator communicator, SwitchState switchState) {
		this(communicator, switchState, switchState == SwitchState.ON ? BlubState.ON : BlubState.OFF);
	}
	
	public Blub(LoraCommunicator communicator, SwitchState switchState, BlubState blubState) {
		super(THING_TYPE, THING_MODE, communicator);
		
		if (switchState == null)
			throw new IllegalArgumentException("Null switch state.");
		
		if (blubState == null)
			throw new IllegalArgumentException("Null blub state.");
		
		if (switchState == SwitchState.ON && blubState == BlubState.OFF ||
				switchState == SwitchState.OFF && blubState == BlubState.ON) {
			throw new IllegalStateException(String.format("Invalid blub states. Switch state: %s. Blub state: %s.", switchState, blubState));
		}
		
		this.switchState = switchState;
		this.blubState = blubState;
	}
	
	@Override
	public String getSoftwareVersion() {
		return "0.1.0.RELEASE";
	}

	private class BlubEmulatorPanel extends AbstractThingEmulatorPanel implements ActionListener {
		private static final long serialVersionUID = 7660599095831708565L;
		
		private static final String FILE_NAME_BLUB_OFF = "blub_off.png";
		private static final String FILE_NAME_BLUB_ON = "blub_on.png";

		private JLabel blubImage;
		private JButton flash;
		
		public BlubEmulatorPanel() {
			super(Blub.this);
			addThingListener(this);
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
		
		private void refreshFlashButtionStatus() {
			if (powered && switchState == SwitchState.OFF && blubState == BlubState.OFF) {
				panel.flash.setEnabled(true);
			} else {
				panel.flash.setEnabled(false);
			}
		}
		
		private void flashBlub() {
			if (!powered)
				return;
			
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
		panel.flashBlub();
	}

	@Override
	public AbstractThingEmulatorPanel getPanel() {
		panel = new BlubEmulatorPanel();
		panel.updateStatus();
		panel.refreshFlashButtionStatus();
		
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
		
		panel.refreshFlashButtionStatus();
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
		
		panel.refreshFlashButtionStatus();
	}

	@Override
	public void powerChanged(PowerEvent event) {
		panel.refreshFlashButtionStatus();
	}

	@Override
	public void configure(String key, Object value) {}

	@Override
	public Map<String, Object> getConfiguration() {
		return Collections.emptyMap();
	}

	@Override
	public String getLanId() {
		return lanId;
	}
	
}
