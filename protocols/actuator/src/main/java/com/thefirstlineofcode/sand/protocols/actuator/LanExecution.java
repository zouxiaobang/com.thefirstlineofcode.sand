package com.thefirstlineofcode.sand.protocols.actuator;

import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.convention.validation.annotations.NotNull;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;
import com.thefirstlineofcode.sand.protocols.core.ITraceId;
import com.thefirstlineofcode.sand.protocols.core.ITraceable;

@ProtocolObject(namespace="urn:leps:iot:actuator", localName="lan-execution")
public class LanExecution implements ITraceable {
	public static final Protocol PROTOCOL = new Protocol("urn:leps:iot:actuator", "lan-execution");
	
	@NotNull
	private ITraceId traceId;
	@NotNull
	private Object lanActionObj;
	
	public LanExecution() {}
	
	public LanExecution(ITraceId traceId) {
		this(traceId, null);
	}
	
	public LanExecution(ITraceId traceId, Object lanActionObj) {
		this.traceId = traceId;
		this.lanActionObj = lanActionObj;
	}
	
	public void setTraceId(ITraceId traceId) {
		this.traceId = traceId;
	}
	
	@Override
	public ITraceId getTraceId() {
		return traceId;
	}
	
	public void setLanActionObj(Object lanActionObj) {
		this.lanActionObj = lanActionObj;
	}
	
	public Object getLanActionObj() {
		return lanActionObj;
	}
}
