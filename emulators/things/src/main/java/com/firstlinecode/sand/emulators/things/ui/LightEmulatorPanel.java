package com.firstlinecode.sand.emulators.things.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import com.firstlinecode.sand.emulators.things.ILight;
import com.firstlinecode.sand.emulators.things.ILight.LightState;
import com.firstlinecode.sand.emulators.things.ILight.SwitchState;
import com.firstlinecode.sand.emulators.things.emulators.ILightEmulator;
import com.firstlinecode.sand.emulators.things.emulators.ISwitchStateListener;

public class LightEmulatorPanel extends AbstractThingEmulatorPanel<ILightEmulator> implements ActionListener  {
	private static final long serialVersionUID = 7660599095831708565L;
	
	private static final String FILE_NAME_LIGHT_OFF = "light_off.png";
	private static final String FILE_NAME_LIGHT_ON = "light_on.png";

	private JLabel lightImage;
	private JButton flash;
	
	private ImageIcon lightOn;
	private ImageIcon lightOff;
	
	private ILightEmulator light;
	private ISwitchStateListener switchStateListener;
	
	public LightEmulatorPanel(ILightEmulator light) {
		super(light);
		setPreferredSize(new Dimension(720, 360));
		
		this.light = light;
		
		updateStatus();
		refreshFlashButtionStatus();
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
	protected JPanel createThingCustomizedUi(ILightEmulator light) {			
		JPanel customizedUi = new JPanel(new BorderLayout());
		
		customizedUi.add(createSwitchsPanel(light), BorderLayout.NORTH);
		customizedUi.add(createLightPanel(light), BorderLayout.WEST);			
		
		customizedUi.setPreferredSize(new Dimension(640, 480));
		
		return customizedUi;
	}

	private JPanel createLightPanel(ILightEmulator light) {
		JPanel panel = new JPanel(new BorderLayout());
		
		lightImage = new JLabel(getLightImageIcon(light.getLightState()));
		panel.add(lightImage, BorderLayout.CENTER);
		panel.add(createFlashPanel(), BorderLayout.SOUTH);
		
		return panel;
	}

	private JPanel createSwitchsPanel(ILightEmulator light) {
		JRadioButton off = createOffButton(light);
		JRadioButton on = createOnButton(light);
		JRadioButton control = createControlButton(light);
		
		ButtonGroup group = new ButtonGroup();
		group.add(off);
		group.add(on);
		group.add(control);
		
		off.addActionListener(this);
		on.addActionListener(this);
		control.addActionListener(this);
		
		JPanel panel = new JPanel(new GridLayout(1, 0));
		panel.add(off);
		panel.add(on);
		panel.add(control);
		
		return panel;
	}

	private JRadioButton createControlButton(ILightEmulator light) {
		JRadioButton control = new JRadioButton("Remote Control");
		control.setMnemonic(KeyEvent.VK_R);
		control.setActionCommand("remove_control");
		control.setSelected(true);
		
		if (light.getSwitchState() == SwitchState.CONTROL)
			control.setSelected(true);
		
		return control;
	}

	private JRadioButton createOnButton(ILightEmulator light) {
		JRadioButton on = new JRadioButton("Turn On");
		on.setMnemonic(KeyEvent.VK_N);
		on.setActionCommand("on");
		
		if (light.getSwitchState() == SwitchState.ON)
			on.setSelected(true);
		
		return on;
	}

	private JRadioButton createOffButton(ILightEmulator light) {
		JRadioButton off = new JRadioButton("Turn Off");
		off.setMnemonic(KeyEvent.VK_F);
		off.setActionCommand("off");
		
		if (light.getSwitchState() == SwitchState.OFF)
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
	
	public void refreshFlashButtionStatus() {
		if (light.isPowered() && light.getLightState() == LightState.OFF) {
			flash.setEnabled(true);
		} else {
			flash.setEnabled(false);
		}
	}
	
	public void flash() {
		if (!light.isPowered())
			return;
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				flash.setEnabled(false);
				lightImage.setIcon(getLightImageIcon(LightState.ON));
				
				repaint();
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
						refreshFlashButtionStatus();
						lightImage.setIcon(getLightImageIcon(LightState.OFF));
						
						repaint();
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
		
		boolean changed = false;
		ILight.SwitchState oldSwitchState = light.getSwitchState();
		if (actionCommand.equals("off")) {
			changed = light.changeSwitchState(ILight.SwitchState.OFF);
		} else if (actionCommand.equals("on")) {
			changed = light.changeSwitchState(ILight.SwitchState.ON);
		} else {
			changed = light.changeSwitchState(ILight.SwitchState.CONTROL);
		}
		
		if (changed && switchStateListener != null) {
			switchStateListener.switchStateChanged(oldSwitchState, light.getSwitchState());
		}
		
		refreshFlashButtionStatus();
	}
	
	public void updateStatus() {
		updateStatus(light.getThingStatus());
	}
	
	public void setSwitchStateListener(ISwitchStateListener switchStateListener) {
		this.switchStateListener = switchStateListener;
	}
	
	public void turnOn() {
		refreshFlashButtionStatus();
		
		if (light.isPowered())
			lightImage.setIcon(getLightImageIcon(LightState.ON));
	}
	
	public void turnOff() {
		refreshFlashButtionStatus();
		
		if (light.isPowered())
			lightImage.setIcon(getLightImageIcon(LightState.OFF));
	}
}
