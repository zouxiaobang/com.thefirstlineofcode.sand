package com.thefirstlineofcode.sand.client.remoting;

import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Stanza;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.chalk.core.IChatServices;
import com.thefirstlineofcode.chalk.core.ITask;
import com.thefirstlineofcode.chalk.core.IUnidirectionalStream;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;

public class Remoting implements IRemoting {
	private IChatServices chatServices;
	
	@Override
	public void execute(JabberId target, Object action) {
		execute(target, action, null);
	}

	@Override
	public void execute(JabberId target, Execution execution) {
		execute(target, execution, null);
	}

	@Override
	public void execute(JabberId target, Object action, Callback callback) {
		execute(target, new Execution(action), callback);
	}

	@Override
	public void execute(final JabberId target, final Execution execution, final Callback callback) {
		chatServices.getTaskService().execute(new ITask<Iq>() {
			@Override
			public void trigger(IUnidirectionalStream<Iq> stream) {
				Iq iq = new Iq(Iq.Type.SET, execution, Stanza.generateId("exec"));
				iq.setTo(target);
				
				stream.send(iq);
			}

			@Override
			public void processResponse(IUnidirectionalStream<Iq> stream, Iq iq) {
				if (callback != null) {
					callback.executed(iq.getObject());
				}
			}
			
			@Override
			public boolean processError(IUnidirectionalStream<Iq> stream, StanzaError error) {
				if (callback != null) {
					callback.occurred(error);
					return true;
				}
				
				return false;
			}
			
			@Override
			public boolean processTimeout(IUnidirectionalStream<Iq> stream, Iq stanza) {
				if (callback != null) {
					callback.timeout();
					return true;
				}
				
				return false;
			}

			@Override
			public void interrupted() {}
			
		});
	}
	
}
