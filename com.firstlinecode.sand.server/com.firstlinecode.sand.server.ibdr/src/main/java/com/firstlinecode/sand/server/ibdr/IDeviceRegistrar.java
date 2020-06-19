package com.firstlinecode.sand.server.ibdr;

import com.firstlinecode.sand.protocols.core.DeviceIdentity;

public interface IDeviceRegistrar {
	RegistrationResult register(String deviceId);
	void remove(String deviceId);
	
	public class RegistrationResult {
		private DeviceIdentity identity;
		private Object customizedTaskResult;
		
		public RegistrationResult(DeviceIdentity identity) {
			this(identity, null);
		}
		
		public RegistrationResult(DeviceIdentity identity, Object customizedTaskResult) {
			this.identity = identity;
			this.customizedTaskResult = customizedTaskResult;
		}
		
		public DeviceIdentity getIdentity() {
			return identity;
		}
		
		public void setIdentity(DeviceIdentity identity) {
			this.identity = identity;
		}
		
		public Object getCustomizedTaskResult() {
			return customizedTaskResult;
		}
		
		public void setCustomizedTaskResult(Object customizedTaskResult) {
			this.customizedTaskResult = customizedTaskResult;
		}
		
	}
}
