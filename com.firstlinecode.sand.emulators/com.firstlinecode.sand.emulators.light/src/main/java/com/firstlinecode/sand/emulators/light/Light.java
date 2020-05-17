package com.firstlinecode.sand.emulators.light;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.gem.protocols.bxmpp.IdentifyBytes;
import com.firstlinecode.gem.protocols.bxmpp.ReplacementBytes;
import com.firstlinecode.sand.emulators.lora.LoraCommunicator;
import com.firstlinecode.sand.emulators.modes.Le01ModeDescriptor;
import com.firstlinecode.sand.emulators.thing.AbstractThingEmulator;
import com.firstlinecode.sand.emulators.thing.AbstractThingEmulatorPanel;
import com.firstlinecode.sand.emulators.thing.PowerEvent;
import com.firstlinecode.sand.protocols.emulators.light.Flash;

public class Light extends AbstractThingEmulator implements ILight {
	public static final String THING_NAME = "Light Emulator";
	public static final String THING_MODE = "LE01";
	public static final String SOFTWARE_VERSION = "0.1.0.RELEASE";
	
	private static final SwitchState DEFAULT_SWITCH_STATE = SwitchState.OFF;
	private static final LightState DEFAULT_LIGHT_STATE = LightState.OFF;
	
	private static final int DATA_RECEIVING_INTERVAL = 1000;
	
	private SwitchState switchState = DEFAULT_SWITCH_STATE;
	private LightState lightState = DEFAULT_LIGHT_STATE;
	
	private JPanel switchsPanel;
	private LightEmulatorPanel panel;
	
	private Timer dataReceivingTimer;
	
	public Light(LoraCommunicator communicator) {
		this(communicator, DEFAULT_SWITCH_STATE, DEFAULT_LIGHT_STATE);
	}
	
	public Light(LoraCommunicator communicator, SwitchState switchState) {
		this(communicator, switchState, switchState == SwitchState.ON ? LightState.ON : LightState.OFF);
	}
	
	public Light(LoraCommunicator communicator, SwitchState switchState, LightState lightState) {
		super(THING_MODE, communicator);
		
		if (switchState == null)
			throw new IllegalArgumentException("Null switch state.");
		
		if (lightState == null)
			throw new IllegalArgumentException("Null light state.");
		
		if (switchState == SwitchState.ON && lightState == LightState.OFF ||
				switchState == SwitchState.OFF && lightState == LightState.ON) {
			throw new IllegalStateException(String.format("Invalid light states. Switch state: %s. Light state: %s.", switchState, lightState));
		}
		
		this.switchState = switchState;
		this.lightState = lightState;
	}
	
	@Override
	public String getSoftwareVersion() {
		return SOFTWARE_VERSION;
	}

	private class LightEmulatorPanel extends AbstractThingEmulatorPanel implements ActionListener {
		private static final long serialVersionUID = 7660599095831708565L;
		
		private static final String FILE_NAME_LIGHT_OFF = "light_off.png";
		private static final String FILE_NAME_LIGHT_ON = "light_on.png";

		private JLabel lightImage;
		private JButton flash;
		
		private ImageIcon lightOn;
		private ImageIcon lightOff;
		
		public LightEmulatorPanel() {
			super(Light.this);
			
			addThingListener(this);
		}
		
		private void createLightIcons() {
			lightOn = createLightIcon(LightState.ON);
			lightOff = createLightIcon(LightState.OFF);

		}

		private ImageIcon createLightIcon(LightState lightState) {
			String path = lightState == LightState.ON ? "/images/" + FILE_NAME_LIGHT_ON : "/images/" + FILE_NAME_LIGHT_OFF;
			java.net.URL imgURL = getClass().getResource(path);
			if (imgURL != null) {
				return new ImageIcon(imgURL);
			} else {
				throw new RuntimeException("Couldn't find file: " + path);
			}
		}

		@Override
		protected JPanel createThingCustomizedUi() {			
			JPanel customizedUi = new JPanel(new BorderLayout());
			
			lightImage = new JLabel(getLightImageIcon(lightState));
			switchsPanel = createSwitchsPanel();
			
			customizedUi.add(switchsPanel, BorderLayout.NORTH);
			customizedUi.add(lightImage, BorderLayout.CENTER);			
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
					flash();
				}
			});
			
			return flashPanel;
		}
		
		private void refreshFlashButtionStatus() {
			if (powered && switchState == SwitchState.OFF && lightState == LightState.OFF) {
				panel.flash.setEnabled(true);
			} else {
				panel.flash.setEnabled(false);
			}
		}
		
		private void flash() {
			if (!powered)
				return;
			
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					switchsPanel.setEnabled(false);
					flash.setEnabled(false);
					lightImage.setIcon(getLightImageIcon(LightState.ON));
					
					switchsPanel.repaint();
					flash.repaint();
					lightImage.repaint();
				}
			});
			
			new Thread(new Runnable() {		
				@Override
				public void run() {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							switchsPanel.setEnabled(true);
							flash.setEnabled(true);
							lightImage.setIcon(getLightImageIcon(LightState.OFF));
							
							switchsPanel.repaint();
							flash.repaint();
							lightImage.repaint();
						}
					});
				}
			}).start();
		}
		
		protected ImageIcon getLightImageIcon(LightState lightState) {
			if (lightState == null) {
				throw new IllegalArgumentException("Null light state.");
			}
			
			if (lightOn == null || lightOff == null)
				createLightIcons();
			
			if (LightState.ON == lightState) {
				return lightOn;
			} else {
				return lightOff;
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
		out.writeObject(lightState);
		out.writeObject(switchState);
	}

	@Override
	protected void doReadExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		lightState = (LightState)in.readObject();
		switchState = (SwitchState)in.readObject();
	}
	
	public SwitchState getSwitchState() {
		return switchState;
	}

	@Override
	public LightState getLightState() {
		return lightState;
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
			light();
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
			unlight();
		}	
	}

	@Override
	public void flash() throws NotRemoteControlStateException, NotTurnOffStateException {
		panel.flash();
	}

	@Override
	public AbstractThingEmulatorPanel getPanel() {
		if (panel == null) {
			panel = new LightEmulatorPanel();
			panel.updateStatus();
			panel.refreshFlashButtionStatus();
		}
		
		return panel;
	}

	@Override
	protected void doReset() {
		doTurnOff();
	}

	@Override
	protected void doPowerOn() {
		if (switchState == SwitchState.ON || lightState == LightState.ON)
			light();
		
		panel.refreshFlashButtionStatus();
	}

	private void light() {
		lightState = LightState.ON;
		panel.lightImage.setIcon(panel.getLightImageIcon(lightState));
	}

	private void unlight() {
		lightState = LightState.OFF;
		panel.lightImage.setIcon(panel.getLightImageIcon(lightState));
	}

	@Override
	protected void doPowerOff() {		
		unlight();
		
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
	public String getThingName() {
		return THING_NAME;
	}

	@Override
	protected void doStartToReceiveData() {
		dataReceivingTimer = new Timer(String.format("%s '%s' Data Receiving Timer", getThingName(), deviceId));
		dataReceivingTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				((LoraCommunicator)communicator).receive();
			}
		}, 100, DATA_RECEIVING_INTERVAL);
	}

	@Override
	protected void doStopDataReceiving() {
		if (dataReceivingTimer != null) {
			dataReceivingTimer.cancel();
			dataReceivingTimer = null;
		}
	}

	@Override
	protected Map<IdentifyBytes, Class<?>> getIdentifyBytesToActionTypes() {
		Map<IdentifyBytes, Class<?>> identifyBytesToTypes = new HashMap<>();
		
		IdentifyBytes flashIdentifierBytes = new IdentifyBytes(new ReplacementBytes((byte)0x95),
				new ReplacementBytes((byte)0x96));
		identifyBytesToTypes.put(flashIdentifierBytes, Flash.class);
		
		return identifyBytesToTypes;
	}

	@Override
	protected void processAction(Object action) throws ExecutionException {
		if (action instanceof Flash) {
			Flash flash = (Flash)action;
			
			int repeat = flash.getRepeat();
			if (repeat == 0)
				repeat = 1;
			
			if (repeat == 1) {
				executeFlashAction();
			} else {
				for (int i = 0; i < repeat; i++) {
					executeFlashAction();
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// Ignore
					}
				}
			}
		} else {
			throw new ExecutionException(new IllegalArgumentException(String.format("Unsupported action type: %s", action.getClass())));
		}
	}

	private void executeFlashAction() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub					
				try {
					flash();
				} catch (NotRemoteControlStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotTurnOffStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	protected Map<Protocol, Class<?>> createSupportedActions() {
		return new Le01ModeDescriptor().getSupportedActions();
	}
	
}
