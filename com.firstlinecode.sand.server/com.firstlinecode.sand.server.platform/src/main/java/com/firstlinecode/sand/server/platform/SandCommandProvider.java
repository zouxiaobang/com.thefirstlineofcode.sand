package com.firstlinecode.sand.server.platform;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.event.IEventProducer;
import com.firstlinecode.granite.framework.core.event.IEventProducerAware;
import com.firstlinecode.sand.protocols.actuator.Execute;
import com.firstlinecode.sand.server.actuator.ExecutionEvent;
import com.firstlinecode.sand.server.concentrator.IConcentrator;
import com.firstlinecode.sand.server.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.concentrator.Node;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.IDeviceManager;

public class SandCommandProvider implements CommandProvider, IEventProducerAware {
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
			"\tsand execute <device_location> <ACTION_NAME> [PARAMS...] - Execute an action on the specified device.\r\n" +
			"\tsand help - Display help information.\r\n";
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Dependency("concentrator.factory")
	private IConcentratorFactory concentratorFactory;
	
	private IEventProducer eventProducer;
	
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
			interpreter.print(String.format("Device '%s' has already been confirmed to be a node of concentrator '%s'.\n", device.getDeviceId(), nodeDeviceId));
		} else if (PARAM_EXECUTE.equals(nextArg)) {
			String deviceLocation = interpreter.nextArgument();
			
			if (deviceLocation == null) {
				interpreter.print("Error: You must provide device location of the device which should execute the action.\n");	
				return;
			}
			
			String actionName = interpreter.nextArgument();
			if (actionName == null) {
				interpreter.print("Error: You must provide the action name and parameters to be executed on the device.\n");	
				return;
			}
			
			String sParams = interpreter.nextArgument();
			Map<String, String> params = null;
			if (sParams != null) {
				params = new HashMap<>();
				
				StringTokenizer tokenizer = new StringTokenizer(sParams, ",");
				while (tokenizer.hasMoreTokens()) {
					String param = tokenizer.nextToken();
					int equalMarkIndex = param.indexOf('=');
					
					if (equalMarkIndex == -1) {
						interpreter.print(String.format("Error: Illegal parameter: %s.\n", param));
						return;
					}
					
					if (equalMarkIndex == param.length() - 1) {
						interpreter.print(String.format("Error: Illegal parameter: %s.\n", param));
						return;
					}
					
					String paramName = param.substring(0, equalMarkIndex);
					String paramValue = param.substring(equalMarkIndex + 1, param.length());
					params.put(paramName, paramValue);
				}
			}
			
			execute(interpreter, deviceLocation, actionName, params);
		} else {
			printDetailHelp(interpreter);
		}	
	}
	
	private void execute(CommandInterpreter interpreter, String deviceLocation, String actionName, Map<String, String> params) {
		String deviceId = null;
		String lanId = null;
		
		int slashIndex = deviceLocation.indexOf('/');
		
		if (slashIndex == deviceLocation.length() - 1)
			interpreter.print("Error: Invalid device location.\n");
			
		if (slashIndex == -1) {
			deviceId = deviceLocation;
		} else {
			deviceId = deviceLocation.substring(0, slashIndex);
			lanId = deviceLocation.substring(slashIndex + 1, deviceLocation.length());
		}
		
		if (!deviceManager.deviceIdExists(deviceId)) {
			interpreter.print("Error: Device which's device ID is '%s' not existed.\n");	
			return;
		}
		
		Device device = deviceManager.getByDeviceId(deviceId);
		if (!concentratorFactory.isConcentrator(device) || lanId == null || LAN_ID_CONCENTRATOR.equals(lanId)) {
			executeOnDevice(interpreter, device, actionName, params);
		} else {
			executeOnNode(interpreter, device, lanId, actionName, params);			
		}
	}
	
	private boolean isActionSupported(CommandInterpreter interpreter, Device device, String actionName) {
		String mode = deviceManager.getMode(device.getDeviceId());
		if (!deviceManager.isActionSupported(mode, actionName)) {
			interpreter.print(String.format("Error: Action '%s' isn't supported by device which's device ID is '%s'.\n",
					actionName, device.getDeviceId()));
			
			return false;
		}
		
		return true;
	}
	
	private void executeOnDevice(CommandInterpreter interpreter, Device device, String actionName, Map<String, String> params) {
		if (!isActionSupported(interpreter, device, actionName))
			return;
		
		Object actionObject = createActionObject(interpreter, device.getMode(), actionName, params);
		eventProducer.fire(new ExecutionEvent(device, null, new Execute(actionName, actionObject)));
	}
	
	private void executeOnNode(CommandInterpreter interpreter, Device concentratorDevice, String lanId, String actionName, Map<String, String> params) {
		Device nodeDevice = getNodeDevice(interpreter, concentratorDevice, lanId);
		if (nodeDevice == null)
			return;
		
		if (!isActionSupported(interpreter, nodeDevice, actionName))
			return;
		
		Object actionObject = createActionObject(interpreter, nodeDevice.getMode(), actionName, params);
		eventProducer.fire(new ExecutionEvent(concentratorDevice, lanId, new Execute(actionName, actionObject)));
	}
	
	private Device getNodeDevice(CommandInterpreter interpreter, Device concentratorDevice, String lanId) {
		IConcentrator concentrator = concentratorFactory.getConcentrator(concentratorDevice);
		Node node = concentrator.getNode(lanId);
		
		if (node != null) {
			return deviceManager.getByDeviceId(node.getDeviceId());
		}
		
		interpreter.print(String.format("Error: Node not existed. Concentrator's device ID is '%s'. Lan ID is '%s'.\n",
				concentratorDevice.getDeviceId(), lanId));
		
		return null;
	}

	private Object createActionObject(CommandInterpreter interpreter, String mode, String actionName, Map<String, String> params) {		
		Class<?> actionType = deviceManager.getActionType(mode, actionName);
		try {
			Object action = actionType.newInstance();
			if (params != null && !params.isEmpty()) {				
				populateProperties(action, params);
			}
			
			return action;
		} catch (InstantiationException | IllegalAccessException e) {
			interpreter.print(String.format("Error: Can't initialize action object. Action type is %s.\n", actionType));
		} catch (IllegalArgumentException e) {
			interpreter.print(String.format("Error: Can't populate action's properties. Detail info is: %s.\n", e.getMessage()));
		}
		
		return null;
	}

	private void populateProperties(Object action, Map<String, String> params) {
		for (String paramName : params.keySet()) {
			try {
				PropertyDescriptor pd = new PropertyDescriptor(paramName, action.getClass());
				if (pd.getWriteMethod() != null) {
					Object value = getParam(pd.getPropertyType(), params.get(paramName));
					if (value == null)
						continue;
					
					pd.getWriteMethod().invoke(action, value);
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	private Object getParam(Class<?> propertyType, String paramValue) {
		if (!isPrimitiveType(propertyType))
			throw new IllegalArgumentException(String.format("Unsupported property type: %s", propertyType.getName()));
		
		return convertStringToPrimitiveType(propertyType, paramValue);
	}
	
	private Object convertStringToPrimitiveType(Class<?> type, String value) {
		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return Boolean.valueOf(value);
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			return Integer.valueOf(value);
		} else if (type.equals(long.class) || type.equals(Long.class)) {
			return Long.valueOf(value);
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			return Float.valueOf(value);
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return Double.valueOf(value);
		} else if (type.equals(BigInteger.class)) {
			return new BigInteger(value);
		} else if (type.equals(BigDecimal.class)) {
			return new BigDecimal(value);
		} else {
			return value;
		}
	}
	
	private boolean isPrimitiveType(Class<?> fieldType) {
		return fieldType.equals(String.class) ||
				fieldType.equals(boolean.class) ||
				fieldType.equals(Boolean.class) ||
				fieldType.equals(int.class) ||
				fieldType.equals(Integer.class) ||
				fieldType.equals(long.class) ||
				fieldType.equals(Long.class) ||
				fieldType.equals(float.class) ||
				fieldType.equals(Float.class) ||
				fieldType.equals(double.class) ||
				fieldType.equals(Double.class) ||
				fieldType.equals(BigInteger.class) ||
				fieldType.equals(BigDecimal.class) ||
				fieldType.isEnum();
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

	@Override
	public void setEventProducer(IEventProducer eventProducer) {
		this.eventProducer = eventProducer;
	}

}
