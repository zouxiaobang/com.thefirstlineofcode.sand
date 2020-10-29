package com.firstlinecode.sand.emulators.things.emulators;

import com.firstlinecode.sand.emulators.things.ILight;

public interface ILightEmulator extends ILight, IThingEmulator {
	boolean changeSwitchState(ILight.SwitchState switchState);
	void addSwitchStateListener(ISwitchStateListener switchStateListener);
	boolean removeSwitchStateListener(ISwitchStateListener switchStateListener);
	void addLightStateChangeListener(ILightStateListener lightStateListener);
	boolean removeLightStateListener(ILightStateListener lightStateListener);
}
