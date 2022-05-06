package com.thefirstlineofcode.sand.emulators.commons;

import com.thefirstlineofcode.sand.emulators.things.ILight;

public interface ILightEmulator extends ILight, IThingEmulator {
	void changeSwitchState(ILight.SwitchState switchState);
}
