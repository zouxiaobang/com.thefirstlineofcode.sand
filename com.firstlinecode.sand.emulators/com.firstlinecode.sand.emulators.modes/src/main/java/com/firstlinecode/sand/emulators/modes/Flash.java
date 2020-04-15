package com.firstlinecode.sand.emulators.modes;

import com.firstlinecode.sand.protocols.core.IAction;

public class Flash implements IAction {
	private int repeat;

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	
}
