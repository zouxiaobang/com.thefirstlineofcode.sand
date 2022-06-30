package com.thefirstlineofcode.sand.client.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.Conflict;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.NotAcceptable;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.chalk.core.ITask;
import com.thefirstlineofcode.chalk.core.IUnidirectionalStream;
import com.thefirstlineofcode.sand.protocols.operator.AuthorizeDevice;

public class Operator implements IOperator {
	private IChatServices chatServices;
	private List<IOperator.Listener> listeners;
	
	public Operator(IChatServices chatServices) {
		this.chatServices = chatServices;
		listeners = new ArrayList<IOperator.Listener>();
	}

	@Override
	public void authorize(final String deviceId) {
		chatServices.getTaskService().execute(new ITask<Iq>() {

			@Override
			public void trigger(IUnidirectionalStream<Iq> stream) {
				stream.send(new Iq(Iq.Type.SET, new AuthorizeDevice(deviceId)));
			}

			@Override
			public void processResponse(IUnidirectionalStream<Iq> stream, Iq iq) {
				if (iq.getType() != Iq.Type.RESULT) {
					throw new RuntimeException("Attribute type must be 'result'.");
				}
				
				if (iq.getObject() != null) {
					throw new RuntimeException("Protocol object should be null.");
				}
				
				for (IOperator.Listener listener : listeners) {
					listener.authorized(deviceId);
				}
			}

			@Override
			public boolean processError(IUnidirectionalStream<Iq> stream, StanzaError error) {
				AuthorizationError authError = getAuthError(error);
				for (IOperator.Listener listener : listeners) {
					listener.occurred(authError, deviceId);
				}
				
				return true;
			}

			private AuthorizationError getAuthError(StanzaError error) {
				if (error instanceof NotAcceptable) {
					return new AuthorizationError(AuthorizationErrorReason.INVALID_DEVICE_ID, error);
				} else if (error instanceof Conflict) {
					return new AuthorizationError(AuthorizationErrorReason.DEVICE_HAS_REGISTERED, error);
				} else {
					return new AuthorizationError(AuthorizationErrorReason.UNKNOWN, error);
				}
			}

			@Override
			public boolean processTimeout(IUnidirectionalStream<Iq> stream, Iq stanza) {
				for (IOperator.Listener listener : listeners) {
					listener.occurred(new AuthorizationError(AuthorizationErrorReason.TIMEOUT, null), deviceId);
				}
				
				return true;
			}

			@Override
			public void interrupted() {}
		});
	}

	@Override
	public void cancelAuthorization(String deviceId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void confirm(String concentratorId, String nodeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelComfirmation(String concentratorId, String nodeId) {
		// TODO Auto-generated method stub

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

	@Override
	public List<Listener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}
}
