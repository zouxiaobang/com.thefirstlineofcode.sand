package com.thefirstlineofcode.sand.client.location;

import java.util.ArrayList;
import java.util.List;

import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Stanza;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.chalk.core.ITask;
import com.thefirstlineofcode.chalk.core.IUnidirectionalStream;
import com.thefirstlineofcode.sand.protocols.location.LocateDevices;

public class DeviceLocator implements IDeviceLocator {
	private IChatServices chatServices;
	
	private List<Listener> listeners;
	
	public DeviceLocator() {
		listeners = new ArrayList<>();
	}
	
	@Override
	public void locateDevices(final List<String> deviceIds) {
		chatServices.getTaskService().execute(new ITask<Iq>() {

			@Override
			public void trigger(IUnidirectionalStream<Iq> stream) {
				LocateDevices locateDevices = new LocateDevices();
				locateDevices.setDeviceIds(deviceIds);
				
				Iq iq = new Iq(Iq.Type.GET, locateDevices, Stanza.generateId("lct"));
				stream.send(iq);
			}

			@Override
			public void processResponse(IUnidirectionalStream<Iq> stream, Iq iq) {
				LocateDevices locateDevices = iq.getObject();
				for (Listener listener : listeners) {
					listener.located(locateDevices.getDeviceLocations());
				}
			}

			@Override
			public boolean processError(IUnidirectionalStream<Iq> stream, StanzaError error) {
				for (Listener listener : listeners) {
					listener.occurred(error);
				}
				
				return true;
			}

			@Override
			public boolean processTimeout(IUnidirectionalStream<Iq> stream, Iq stanza) {
				for (Listener listener : listeners) {
					listener.timeout();
				}
				
				return true;
			}

			@Override
			public void interrupted() {}
			
		});
	}

	@Override
	public void addListener(Listener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public boolean removeListener(Listener listener) {
		return listeners.remove(listener);
	}

}
