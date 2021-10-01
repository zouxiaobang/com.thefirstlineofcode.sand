package com.thefirstlineofcode.sand.emulators.wifi.light;

import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.sand.client.things.autuator.ExecutionException;
import com.thefirstlineofcode.sand.client.things.autuator.IExecutor;
import com.thefirstlineofcode.sand.emulators.things.NotRemoteControlStateException;
import com.thefirstlineofcode.sand.emulators.things.NotTurnedOffStateException;
import com.thefirstlineofcode.sand.protocols.devices.light.Flash;

public class FlashExecutor implements IExecutor<Flash> {
	private Light light;
	
	public FlashExecutor(Light light) {
		this.light = light;
	}

	@Override
	public void execute(Iq iq, Flash action) throws ExecutionException {
		int repeat = action.getRepeat();
		if (repeat == 0)
			repeat = 1;
		
		if (repeat == 1) {
			executeFlash(light);
		} else {
			for (int i = 0; i < repeat; i++) {
				executeFlash(light);
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// Ignore
				}
			}
		}
	}
	
	private void executeFlash(final Light light) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					light.flash();
				} catch (NotRemoteControlStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotTurnedOffStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
}
