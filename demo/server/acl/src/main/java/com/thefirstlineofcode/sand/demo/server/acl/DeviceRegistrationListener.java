package com.thefirstlineofcode.sand.demo.server.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.granite.framework.core.adf.data.IDataObjectFactory;
import com.thefirstlineofcode.granite.framework.core.adf.data.IDataObjectFactoryAware;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventListener;
import com.thefirstlineofcode.granite.framework.im.IResource;
import com.thefirstlineofcode.granite.framework.im.IResourcesService;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlEntry;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList.Role;
import com.thefirstlineofcode.sand.server.ibdr.DeviceRegistrationEvent;

public class DeviceRegistrationListener implements IEventListener<DeviceRegistrationEvent>,
		IServerConfigurationAware, IDataObjectFactoryAware {
	private static final Logger logger = LoggerFactory.getLogger(DeviceRegistrationListener.class);
	
	@BeanDependency
	private IAccessControlListService aclService;
	
	@BeanDependency
	private IResourcesService resourceService;
	
	private IDataObjectFactory dataObjectFactory;
	private String domainName;

	@Override
	public void process(IEventContext context, DeviceRegistrationEvent event) {		
		AccessControlEntry ace = createOwnerAce(event.getDeviceId(), event.getAuthorizer());

		// Was the device authorized in granite server console and the authorizer not specified?
		if (ace == null) {
			logger.warn("The authorizer hasn't be specified. Ignore to pass ACL update stanza to authorizer.");
			return;
		}
		aclService.add(ace);
		
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
	
	private AccessControlEntry createOwnerAce(String deviceId, String authorizer) {
		if (authorizer == null)
			return null;
		
		AccessControlEntry ace = dataObjectFactory.create(AccessControlEntry.class);
		ace.setUser(authorizer);
		ace.setDeviceId(deviceId);
		ace.setRole(Role.OWNER);
		
		return ace;
	}

	@Override
	public void setServerConfiguration(IServerConfiguration serverConfiguration) {
		this.domainName = serverConfiguration.getDomainName();
	}

	@Override
	public void setDataObjectFactory(IDataObjectFactory dataObjectFactory) {
		this.dataObjectFactory = dataObjectFactory;
	}

}
