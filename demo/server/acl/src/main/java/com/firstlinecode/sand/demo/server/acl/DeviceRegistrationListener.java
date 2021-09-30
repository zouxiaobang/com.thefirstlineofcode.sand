package com.firstlinecode.sand.demo.server.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.config.IServerConfiguration;
import com.firstlinecode.granite.framework.core.config.IServerConfigurationAware;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.firstlinecode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.firstlinecode.granite.framework.im.IResource;
import com.firstlinecode.granite.framework.im.IResourcesService;
import com.firstlinecode.sand.demo.protocols.AccessControlEntry;
import com.firstlinecode.sand.demo.protocols.AccessControlList;
import com.firstlinecode.sand.server.ibdr.DeviceRegistrationEvent;
import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;

public class DeviceRegistrationListener implements IEventListener<DeviceRegistrationEvent>, IServerConfigurationAware {
	private static final Logger logger = LoggerFactory.getLogger(DeviceRegistrationListener.class);
	
	@Dependency("resources.service")
	private IResourcesService resourceService;
	
	private String domainName;

	@Override
	public void process(IEventContext context, DeviceRegistrationEvent event) {		
		AccessControlEntry ace = (AccessControlEntry)event.getCustomizedTaskResult();

		// Was the device authorized in OSGi console and not specify the authorizer?
		if (ace == null) {
			logger.warn("The authorizer hasn't be specified. Ignore to pass ACL update stanza to authorizer.");
			return;
		}
		
		IResource[] resources = resourceService.getResources(JabberId.parse(String.format("%s@%s", ace.getUser(), domainName)));	
		if (resources == null || resources.length == 0 && logger.isWarnEnabled()) {
			logger.warn("Can't find any resource for authorizer '{}'. Ignore to pass ACL update stanza to authorizer.", ace.getUser());
		}
		
		AccessControlList acl = new AccessControlList();
		ace.setUser(null);
		acl.add(ace);
		
		for (IResource resource : resources) {			
			Iq iq = new Iq(Iq.Type.SET);
			iq.setTo(resource.getJid());	
			iq.setObject(acl);
			
			context.write(iq);
		}
	}

	@Override
	public void setServerConfiguration(IServerConfiguration serverConfiguration) {
		this.domainName = serverConfiguration.getDomainName();
	}

}
