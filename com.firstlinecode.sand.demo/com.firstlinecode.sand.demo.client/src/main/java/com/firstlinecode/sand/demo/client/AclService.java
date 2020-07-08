package com.firstlinecode.sand.demo.client;

import java.util.List;

import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.StanzaError;
import com.firstlinecode.chalk.IChatServices;
import com.firstlinecode.chalk.ITask;
import com.firstlinecode.chalk.IUnidirectionalStream;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;

public class AclService implements IAclService {
	private List<Listener> listeners;
	
	private IChatServices chatServices;
	
	public AclService(IChatServices chatServices) {
		this.chatServices = chatServices;
	}

	@Override
	public void retrieve() {
		retrieve(5 * 1000);
	}
	
	@Override
	public void retrieve(final int timeout) {
		chatServices.getTaskService().execute(new ITask<Iq>() {
			@Override
			public void trigger(IUnidirectionalStream<Iq> stream) {
				Iq iq = new Iq();
				iq.setObject(new AccessControlList());
				
				stream.send(iq, timeout);
			}

			@Override
			public void processResponse(IUnidirectionalStream<Iq> stream, Iq iq) {
				// TODO Auto-generated method stub
				processRetrived(iq);
			}

			@Override
			public boolean processError(IUnidirectionalStream<Iq> stream, StanzaError error) {
				for (Listener listener : listeners) {
					listener.occurred(error);
				}
				
				return true;
			}

			@Override
			public boolean processTimeout(IUnidirectionalStream<Iq> stream, Iq iq) {
				for (Listener listener : listeners) {
					listener.timeout(iq);
				}
				
				return true;
			}

			@Override
			public void interrupted() {}
		});
	}
	
	private void processRetrived(Iq iq) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	@Override
	public boolean removeListener(Listener listener) {
		return listeners.remove(listener);
	}

	@Override
	public void delete(AccessControlEntry entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(AccessControlEntry entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Listener[] getListeners() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setLocal(AccessControlList local) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AccessControlList getLocal() {
		// TODO Auto-generated method stub
		return null;
	}

}
