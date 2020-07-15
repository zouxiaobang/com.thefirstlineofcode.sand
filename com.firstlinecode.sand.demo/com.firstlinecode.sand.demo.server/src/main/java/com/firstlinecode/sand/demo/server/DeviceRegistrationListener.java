package com.firstlinecode.sand.demo.server;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.granite.framework.core.config.IApplicationConfiguration;
import com.firstlinecode.granite.framework.core.config.IApplicationConfigurationAware;
import com.firstlinecode.granite.framework.core.event.IEventContext;
import com.firstlinecode.granite.framework.core.event.IEventListener;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;
import com.firstlinecode.sand.server.ibdr.DeviceRegistrationEvent;

public class DeviceRegistrationListener implements IEventListener<DeviceRegistrationEvent>, IApplicationConfigurationAware {
	private String domainName;

	@Override
	public void process(IEventContext context, DeviceRegistrationEvent event) {		
		AccessControlEntry ace = (AccessControlEntry)event.getCustomizedTaskResult();
		if (ace == null)
			throw new RuntimeException("Access control entry is null.");
		
		// Was the device authorized in OSGi console?
		if (domainName.equals(ace.getUser())) {
			return;
		}
		
		Iq iq = new Iq(Iq.Type.SET);
		iq.setTo(JabberId.parse(ace.getUser()));
		
		AccessControlList acl = new AccessControlList();
		ace.setUser(null);
		acl.add(ace);
		iq.setObject(acl);
		
		context.write(iq);
	}

	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		this.domainName = appConfiguration.getDomainName();
	}

}
