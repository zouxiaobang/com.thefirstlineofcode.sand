package com.thefirstlineofcode.sand.emulators.things.emulators;

import com.thefirstlineofcode.sand.emulators.things.ILight;

public interface ILightEmulator extends ILight, IThingEmulator {
	void changeSwitchState(ILight.SwitchState switchState);
	void addSwitchStateListener(ISwitchStateListener switchStateListener);
	boolean removeSwitchStateListener(ISwitchStateListener switchStateListener);
	void addLightStateChangeListener(ILightStateListener lightStateListener);
	boolean removeLightStateListener(ILightStateListener lightStateListener);
}
