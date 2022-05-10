package com.thefirstlineofcode.sand.demo.server.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.thefirstlineofcode.granite.framework.im.IResource;
import com.thefirstlineofcode.granite.framework.im.IResourcesService;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlEntry;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.server.ibdr.DeviceRegistrationEvent;

public class DeviceRegistrationListener implements IEventListener<DeviceRegistrationEvent>, IServerConfigurationAware {
	private static final Logger logger = LoggerFactory.getLogger(DeviceRegistrationListener.class);
	
	@BeanDependency
	private IResourcesService resourceService;
	
	private String domainName;

	@Override
	public void process(IEventContext context, DeviceRegistrationEvent event) {		
		AccessControlEntry ace = (AccessControlEntry)event.getCustomizedTaskResult();

		// Was the device authorized in granite server console and the authorizer not specified?
		if (ace == null) {
			logger.warn("The authorizer hasn't be specified. Ignore to pass ACL update stanza to authorizer.");
			return;
		}
		
		IResource[] resources = resourceService.getResources(JabberId.parse(String.format("%s@%s", ace.getUser(), domainName)));	
		if (resources == null || resources.length == 0 && logger.isWarnEnabled()) {
			logger.warn("Can't find any resource for authorizer '{}'. Ignore to pass ACL update stanza to authorizer.", ace.getUser());
			return;
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
