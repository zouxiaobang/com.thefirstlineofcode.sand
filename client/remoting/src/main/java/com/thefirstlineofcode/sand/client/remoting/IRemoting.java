package com.thefirstlineofcode.sand.client.remoting;

import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;

public interface IRemoting {
	void execute(JabberId target, Object action);
	void execute(JabberId target, Object action, Callback callback);
	void execute(JabberId target, Execution execution);
	void execute(JabberId target, Execution execution, Callback callback);
	
	public interface Callback {
		void executed(Object xep);
		void occurred(StanzaError error);
		void timeout();
	}
}
