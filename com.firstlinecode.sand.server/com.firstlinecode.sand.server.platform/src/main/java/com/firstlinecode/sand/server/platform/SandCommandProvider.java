package com.firstlinecode.sand.server.platform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.MalformedJidException;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.event.IEventService;
import com.firstlinecode.sand.protocols.actuator.Execute;
import com.firstlinecode.sand.server.actuator.ExecutionEvent;
import com.firstlinecode.sand.server.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.IDeviceManager;

public class SandCommandProvider implements CommandProvider {
	private static final String COMMAND_SAND = "sand";
	private static final String PARAM_AUTHORIZE = "authorize";
	private static final String PARAM_DEVICES = "devices";
	private static final String PARAM_CONFIRM = "confirm";
	private static final String PARAM_EXECUTE = "execute";
	private static final String PARAM_HELP = "help";
	
	private static final String SYSTEM_CONSOLE_AUTHORIZER = "System.Console";
	private static final int DEFAULT_VALIDITY_TIME = 1000 * 60 * 30;
	private static final String LAN_ID_CONCENTRATOR = "00";

	private static final String MSG_HELP = "sand - Monitoring and managing sand application.\r\n";
	
	private static final String MSG_DETAIL_HELP =
			"\tsand authorize <device_id> - Authorize a device to register.\r\n" +
			"\tsand devices [start_index] - Display registered devices. Twenty items each page.\r\n" +
			"\tsand confirm <concentrator_device_id> <node_device_id> - Confirm to add a node to concentrator.\r\n" +
			"\tsand execute <device_jabber_id> <ACTION_NAME> [PARAMS...] - Execute an action on the specified device.\r\n" +
			"\tsand help - Display help information.\r\n";
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Dependency("concentrator.factory")
	private IConcentratorFactory concentratorFactory;
	
	@Dependency("event.service")
	private IEventService eventService;
	
	@Override
	public String getHelp() {
		return MSG_HELP;
	}
	
	public Object _help(CommandInterpreter interpreter) {
		String commandName = interpreter.nextArgument();
		if (COMMAND_SAND.equals(commandName))
			return getDetailHelp();
		
		return false;
	}
	
	private String getDetailHelp() {
		return MSG_DETAIL_HELP;
	}
	
	public void _sand(CommandInterpreter interpreter) {
		String nextArg = interpreter.nextArgument();
		
		if (nextArg == null || PARAM_HELP.equals(nextArg)) {
			printDetailHelp(interpreter);
		} else if (PARAM_AUTHORIZE.equals(nextArg)) {
			String deviceId = interpreter.nextArgument();
			if (deviceId == null) {
				interpreter.print("Error: You must provide a device ID.\n");	
				return;
			}
			
			authorize(interpreter, deviceId);
		} else if (PARAM_DEVICES.equals(nextArg)) {
			String sStartIndex = interpreter.nextArgument();
			
			int startIndex = 0;
			if (sStartIndex != null) {
				try {					
					startIndex = Integer.parseInt(sStartIndex);
				} catch (NumberFormatException e) {
					interpreter.print("Invalid start index. Start index must be a number.\n");
					return;
				}
			}
			
			displayDevices(startIndex);
		} else if (PARAM_CONFIRM.equals(nextArg)) {
			String concentratorDeviceId = interpreter.nextArgument();
			String nodeDeviceId = null;
			if (concentratorDeviceId != null) {
				nodeDeviceId = interpreter.nextArgument();				
			}
			
			if (nodeDeviceId == null) {
				interpreter.print("Error: You must provide a concentrator device ID and a node device ID.\n");	
				return;
			}
			
			Device device = deviceManager.getByDeviceId(concentratorDeviceId);
			if (device == null) {
				interpreter.print(String.format("Error: Concentrator which's device ID is '%s' not existed.\n", concentratorDeviceId));	
				return;
			}
			
			if (!concentratorFactory.isConcentrator(device)) {				
				interpreter.print(String.format("Error: Device which's device ID is '%s' isn't a concentrator.", concentratorDeviceId));	
				return;
			}
			
			concentratorFactory.getConcentrator(device).confirm(SYSTEM_CONSOLE_AUTHORIZER, nodeDeviceId);
			interpreter.print(String.format("Node '%s' has already been confirmed to add to concentrator '%s'.\n", device.getDeviceId(), nodeDeviceId));
		} else if (PARAM_EXECUTE.equals(nextArg)) {
			String sDeviceJid = interpreter.nextArgument();
			
			if (sDeviceJid == null) {
				interpreter.print("Error: You must provide Jabber ID of the device which executes the action.\n");	
				return;
			}
			
			JabberId deviceJid = null;
			try {
				deviceJid = JabberId.parse(sDeviceJid);
			} catch (MalformedJidException e) {
				interpreter.print("Error: Malformed jabber ID.\n");	
				return;
			}
			
			String actionName = interpreter.nextArgument();
			if (actionName == null) {
				interpreter.print("Error: You must provide the action name and parameters to be executed on the device.\n");	
				return;
			}
			
			List<String> params = new ArrayList<>();
			String param = null;
			while ((param = interpreter.nextArgument()) != null) {
				params.add(param);
			}
			
			execute(interpreter, deviceJid, actionName, params.toArray(new String[params.size()]));
		} else {
			printDetailHelp(interpreter);
		}	
	}
	
	private void execute(CommandInterpreter interpreter, JabberId deviceJid, String actionName, String[] params) {
		// TODO Auto-generated method stub
		if (!deviceManager.deviceNameExists(deviceJid.getName())) {
			interpreter.print(String.format("Error: Device which's name is '%s' not existed.\n", deviceJid.getName()));	
			return;
		}
		
		Device device = deviceManager.getByDeviceName(deviceJid.getName());
		if (deviceManager.isConcentrator(deviceManager.getMode(device.getDeviceId())) && deviceJid.getResource() == null) {
			deviceJid.setResource(LAN_ID_CONCENTRATOR);
		}
		
		//eventService.fire(new ExecutionEvent(new Execute("flash", new Flash())));
	}

	private void displayDevices(int startIndex) {
		// TODO Auto-generated method stub
		
	}

	private void authorize(CommandInterpreter interpreter, String deviceId) {
		if (!deviceManager.isValid(deviceId)) {			
			interpreter.print(String.format("Error: Invalid device ID '%s'.\n", deviceId));
			return;
		}
		
		if (deviceManager.deviceIdExists(deviceId)) {
			interpreter.print(String.format("Error: Device which's ID is '%s' has already registered.\n", deviceId));
			return;
		}
		
		deviceManager.authorize(deviceId, SYSTEM_CONSOLE_AUTHORIZER, DEFAULT_VALIDITY_TIME);
		interpreter.print(String.format("Device which's ID is '%s' has authorized.\n", deviceId));
	}

	private void printDetailHelp(CommandInterpreter interpreter) {
		interpreter.print(getDetailHelp());
	}

}
