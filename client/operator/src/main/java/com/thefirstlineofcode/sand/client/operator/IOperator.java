package com.thefirstlineofcode.sand.client.operator;

import java.util.List;

import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;

public interface IOperator {
	public enum AuthorizationErrorReason {
		INVALID_DEVICE_ID,
		DEVICE_HAS_REGISTERED,
		TIMEOUT,
		UNKNOWN
	}
	
	public enum ConfirmationError {
		
	}
	
	void authorize(String deviceId);
	void cancelAuthorization(String deviceId);
	void confirm(String concentratorId, String nodeId);
	void cancelComfirmation(String concentratorId, String nodeId);
	void addListener(Listener listener);
	boolean removeListener(Listener listener);
	List<Listener> getListeners();
	
	public interface Listener {
		void authorized(String deviceId);
		void confirmed(String concentratorDeviceId, String nodeDeviceId, String lanId);
		void canceled(String deviceId);
		void canceled(String concentratorDeviceId, String nodeDeviceId);
		void occurred(AuthorizationError error, String deviceId);
		void occurred(ConfirmationError error, String concentratorDeviceId, String nodeDeviceId);
	}
	
	public class AuthorizationError {
		private AuthorizationErrorReason reason;
		private StanzaError error;
		
		public AuthorizationError(AuthorizationErrorReason reason, StanzaError error) {
			this.reason = reason;
			this.error = error;
		}

		public AuthorizationErrorReason getReason() {
			return reason;
		}

		public void setReason(AuthorizationErrorReason reason) {
			this.reason = reason;
		}

		public StanzaError getError() {
			return error;
		}

		public void setError(StanzaError error) {
			this.error = error;
		}
		
	}
}
