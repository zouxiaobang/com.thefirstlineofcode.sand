package com.firstlinecode.sand.client.operator;

import com.firstlinecode.basalt.protocol.core.stanza.error.StanzaError;

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
	
	public interface Listener {
		void authorized(String deviceId);
		void confirmed(String concentratorId, String nodeId, String lanId);
		void canceled(String deviceId);
		void canceled(String concentratorId, String nodeId);
		void occurred(AuthorizationError error, String deviceId);
		void occurred(ConfirmationError error, String concentratorId, String nodeId);
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
