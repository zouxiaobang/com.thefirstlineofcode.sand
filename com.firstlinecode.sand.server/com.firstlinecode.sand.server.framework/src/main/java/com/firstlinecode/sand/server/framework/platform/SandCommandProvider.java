package com.firstlinecode.sand.server.framework.platform;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.sand.server.framework.things.IDeviceManager;

public class SandCommandProvider implements CommandProvider {
	private static final String COMMAND_SAND = "sand";
	private static final String PARAM_DEVICES = "devices";
	private static final String PARAM_AUTHORIZE = "authorize";
	private static final String PARAM_HELP = "help";
	private static final String SYSTEM_CONSOLE_AUTHORIZER = "System.Console";
	private static final int DEFAULT_VALIDITY_TIME = 1000 * 60 * 30;

	private static final String MSG_HELP = "sand - Monitoring and managing sand application.\r\n";
	
	private static final String MSG_DETAIL_HELP =
			"\tsand authorize <device_id> - Authorize a device to register.\r\n" +
			"\tsand devices [start_index] - Display registered devices. Twenty items each page.\r\n" +
			"\tsand help - Display help information.\r\n";
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
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
				interpreter.print(String.format("Error: You must provide a device ID.\n"));	
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
					interpreter.print(String.format("Invalid start index. Start index must be a number.\n"));
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
