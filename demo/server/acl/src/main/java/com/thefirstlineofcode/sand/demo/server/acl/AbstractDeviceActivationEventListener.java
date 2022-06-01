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
import com.thefirstlineofcode.granite.framework.im.IResource;
import com.thefirstlineofcode.granite.framework.im.IResourcesService;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlEntry;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList;
import com.thefirstlineofcode.sand.demo.protocols.AccessControlList.Role;

public class AbstractDeviceActivationEventListener implements  IServerConfigurationAware, IDataObjectFactoryAware {
	private static final Logger logger = LoggerFactory.getLogger(AbstractDeviceActivationEventListener.class);
	
	@BeanDependency
	private IAccessControlListService aclService;
	
	@BeanDependency
	private IResourcesService resourceService;
	
	private IDataObjectFactory dataObjectFactory;
	private String domainName;

	public void process(IEventContext context, String deviceId, String owner) {		
		AccessControlEntry ace = createAce(deviceId, owner);

		// Was the device authorized or confirmed in granite server console and the authorizer or confirmer not be specified?
		if (ace == null) {
			logger.warn("The authorizer or confirmer hasn't be specified. Ignore to pass ACL update stanza to the owner.");
			return;
		}
		aclService.add(ace);
		
		IResource[] resources = resourceService.getResources(JabberId.parse(String.format("%s@%s", ace.getUser(), domainName)));	
		if (resources == null || resources.length == 0 && logger.isWarnEnabled()) {
			logger.warn("Can't find any resource for authorizer or confirmer '{}'. Ignore to pass ACL update stanza to the owner.", ace.getUser());
			return;
		}
		
		AccessControlList acl = new AccessControlList();
		acl.add(ace);
		
		for (IResource resource : resources) {			
			Iq iq = new Iq(Iq.Type.SET);
			iq.setTo(resource.getJid());	
			iq.setObject(acl);
			
			context.write(iq);
		}
	}
	
	private AccessControlEntry createAce(String deviceId, String owner) {
		if (owner == null)
			return null;
		
		AccessControlEntry ace = dataObjectFactory.create(AccessControlEntry.class);
		ace.setUser(owner);
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
