package com.firstlinecode.sand.demo.server.acl;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.BadRequest;
import com.firstlinecode.basalt.protocol.core.stanza.error.Forbidden;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.firstlinecode.granite.framework.core.pipeline.stages.processing.IXepProcessor;
import com.firstlinecode.sand.demo.protocols.AccessControlList;
import com.firstlinecode.sand.demo.protocols.AccessControlList.Role;

public class AccessControlListProcessor implements IXepProcessor<Iq, AccessControlList> {
	@Dependency("access.control.list.service")
	private IAccessControlListService aclService;

	@Override
	public void process(IProcessingContext context, Iq iq, AccessControlList xep) {
		// TODO Auto-generated method stub
		if (iq.getType() == Iq.Type.GET) {
			if (xep.getEntries() != null)
				throw new ProtocolException(new BadRequest("Access control list entries must be null when IQ type is set to 'get'."));
			
			getAccessControlList(context, iq, xep);
		} else if (iq.getType() == Iq.Type.SET) {
			if (xep.getEntries() == null || xep.getEntries().isEmpty())
				throw new ProtocolException(new BadRequest("Access control list entries mustn't be empty when IQ type is set to 'set'."));
			
			setAccessControlList(context, iq, xep);
		} else {
			throw new ProtocolException(new BadRequest("Attribute 'type' should be set to 'get' or 'set'."));
		}
	}

	private void setAccessControlList(IProcessingContext context, Iq iq, AccessControlList xep) {
		// TODO Auto-generated method stub
		
	}

	private void getAccessControlList(IProcessingContext context, Iq iq, AccessControlList xep) {
		AccessControlList acl = null;
		if (xep.getDeviceId() == null) {
			acl = aclService.getByUser(context.getJid().getBareIdString(), xep.getLastModifiedTime());
		} else {
			Role role = aclService.getRole(context.getJid().getBareIdString(), xep.getDeviceId());
			if (role == null)
				throw new ProtocolException(new Forbidden());
			
			if (role == AccessControlList.Role.OWNER) {
				acl = aclService.getByOwnerAndDevice(context.getJid().getBareIdString(), xep.getDeviceId(), xep.getLastModifiedTime());
			} else {
				acl = aclService.getByUserAndDevice(context.getJid().getBareIdString(), xep.getDeviceId(), xep.getLastModifiedTime());
			}
			
			context.write(new Iq(acl, Iq.Type.RESULT, iq.getId()));
		}
	}

}
