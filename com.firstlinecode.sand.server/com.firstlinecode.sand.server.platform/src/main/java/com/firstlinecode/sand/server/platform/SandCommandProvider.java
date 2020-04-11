package com.firstlinecode.sand.server.platform;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.sand.server.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.IDeviceManager;

public class SandCommandProvider implements CommandProvider {
	private static final String COMMAND_SAND = "sand";
	private static final String PARAM_DEVICES = "devices";
	private static final String PARAM_AUTHORIZE = "authorize";
	private static final String PARAM_CONFIRM = "confirm";
	private static final String PARAM_HELP = "help";
	private static final String SYSTEM_CONSOLE_AUTHORIZER = "System.Console";
	private static final int DEFAULT_VALIDITY_TIME = 1000 * 60 * 30;

	private static final String MSG_HELP = "sand - Monitoring and managing sand application.\r\n";
	
	private static final String MSG_DETAIL_HELP =
			"\tsand authorize <device_id> - Authorize a device to register.\r\n" +
			"\tsand confirm <concentrator_device_id> <node_device_id> - Confirm to add a node to concentrator.\r\n" +
			"\tsand devices [start_index] - Display registered devices. Twenty items each page.\r\n" +
			"\tsand help - Display help information.\r\n";
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Dependency("concentrator.factory")
	private IConcentratorFactory concentratorFactory;
	
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
				interpreter.print(String.format("Error: Concentrator which's device ID is %s not existed.\n", concentratorDeviceId));	
				return;
			}
			
			if (!concentratorFactory.isConcentrator(device)) {				
				interpreter.print(String.format("Error: Device which's device ID is %s isn't a concentrator.", concentratorDeviceId));	
				return;
			}
			
			concentratorFactory.getConcentrator(device).confirm(SYSTEM_CONSOLE_AUTHORIZER, nodeDeviceId);
			interpreter.print(String.format("Node %s has already been confirmed to add to concentrator %s.\n", device.getDeviceId(), nodeDeviceId));
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
		} else {
			printDetailHelp(interpreter);
		}	
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
