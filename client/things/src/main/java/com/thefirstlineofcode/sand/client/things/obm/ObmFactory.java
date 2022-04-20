package com.thefirstlineofcode.sand.client.things.obm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.IOxmFactory;
import com.thefirstlineofcode.basalt.oxm.OxmService;
import com.thefirstlineofcode.basalt.oxm.binary.AbstractBinaryXmppProtocolConverter;
import com.thefirstlineofcode.basalt.oxm.binary.BinaryUtils;
import com.thefirstlineofcode.basalt.oxm.binary.BinaryXmppProtocolConverter;
import com.thefirstlineofcode.basalt.oxm.binary.BxmppExtension;
import com.thefirstlineofcode.basalt.oxm.binary.DefaultBxmppExtension;
import com.thefirstlineofcode.basalt.oxm.binary.IBinaryXmppProtocolConverter;
import com.thefirstlineofcode.basalt.oxm.binary.Namespace;
import com.thefirstlineofcode.basalt.oxm.binary.ReduplicateBxmppReplacementException;
import com.thefirstlineofcode.basalt.oxm.binary.ReplacementBytes;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.protocol.core.MessageProtocolChain;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolChain;
import com.thefirstlineofcode.basalt.protocol.im.stanza.Message;
import com.thefirstlineofcode.sand.protocols.actuator.LanExecution;
import com.thefirstlineofcode.sand.protocols.actuator.oxm.LanExecutionParserFactory;
import com.thefirstlineofcode.sand.protocols.actuator.oxm.LanExecutionTranslatorFactory;
import com.thefirstlineofcode.sand.protocols.core.ITraceIdFactory;

public class ObmFactory implements IObmFactory {
	private static final byte[] MESSAGE_WRAPPER_DATA = new byte[] {(byte)0x60, (byte)0, (byte)1, (byte)0};
	
	private IOxmFactory oxmFactory;
	private AbstractBinaryXmppProtocolConverter<?> binaryXmppProtocolConverter;
	private List<Class<?>> registeredObjectTypes;
	private List<Class<?>> registeredLanActionTypes;
	
	public ObmFactory() {
		this(null);
	}
	
	public ObmFactory(ITraceIdFactory traceIdFactory) {
		oxmFactory = OxmService.createStandardOxmFactory();
		registeredObjectTypes = new ArrayList<>();
		registeredLanActionTypes = new ArrayList<>();
		
		String[] configFiles = loadBxmppExtensionConfigurations();
		binaryXmppProtocolConverter = createBinaryXmppProtocolConverter(configFiles);
		
		if (traceIdFactory != null) {			
			ProtocolChain lanExecuteProtocolChain = new MessageProtocolChain(LanExecution.PROTOCOL);
			oxmFactory.register(lanExecuteProtocolChain, new LanExecutionParserFactory(traceIdFactory));
			oxmFactory.register(LanExecution.class, new LanExecutionTranslatorFactory());
		}
	}
	
	private String[] loadBxmppExtensionConfigurations() {
		URL bxmppExtensionsConfigurationFile = getClass().getClassLoader().getResource("META-INF/bxmpp-extensions.txt");
		if (bxmppExtensionsConfigurationFile == null)
			return new String[0];
		
		List<String> configurationFiles = new ArrayList<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(bxmppExtensionsConfigurationFile.openStream()));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				configurationFiles.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return configurationFiles.toArray(new String[configurationFiles.size()]); 
	}
	
	private AbstractBinaryXmppProtocolConverter<?> createBinaryXmppProtocolConverter(String[] configFiles) {
		AbstractBinaryXmppProtocolConverter<?> binaryXmppProtocolConverter = new BinaryXmppProtocolConverter();
		try {
			if (configFiles == null || configFiles.length == 0)
				return binaryXmppProtocolConverter;
			
			DefaultBxmppExtension defaultBxmppExtension = new DefaultBxmppExtension();
			loadBxmppCoreExtension(defaultBxmppExtension);
			loadBxmppImExtension(defaultBxmppExtension);
			
			binaryXmppProtocolConverter.register(defaultBxmppExtension);
			
			for (String configFile : configFiles) {
				BxmppExtension bxmppExtension = loadBxmppExtensionFromPropertiesFile("META-INF/" + configFile);
				binaryXmppProtocolConverter.register(bxmppExtension);
			}
			
			return binaryXmppProtocolConverter;
		} catch (IOException e) {
			throw new RuntimeException("IO exception.", e);
		} catch (ReduplicateBxmppReplacementException e) {
			throw new RuntimeException("Reduplicate BXMPP replacement.", e);
		}
	}

	private void loadBxmppCoreExtension(BxmppExtension defaultBxmppExtension)
			throws IOException, ReduplicateBxmppReplacementException {
		URL bxmppCoreUrl = getClass().getClassLoader().getResource("META-INF/bxmpp-core.properties");
		if (bxmppCoreUrl == null) {
			throw new RuntimeException("bxmpp-core properties file not found.");
		}
		
		Properties properties = new Properties();
		properties.load(new BufferedReader(new InputStreamReader(bxmppCoreUrl.openStream())));
		
		String provider = "BXMPP-Core";
		for (Object oRepalcementByte : properties.keySet()) {
			String sReplacementBytes = (String)oRepalcementByte;
			String keyword = (String)properties.get(sReplacementBytes);
			
			ReplacementBytes replacementBytes = ReplacementBytes.parse(sReplacementBytes);				
			replacementBytes.setProvider(provider);
			
			if (!ReplacementBytes.isNamespaceReplacementBytes(replacementBytes)) {
				defaultBxmppExtension.register(replacementBytes, keyword);
			}
		}
	}
	
	private void loadBxmppImExtension(BxmppExtension defaultBxmppExtension)
			throws IOException, ReduplicateBxmppReplacementException {
		URL bxmppImUrl = getClass().getClassLoader().getResource("META-INF/bxmpp-im.properties");
		if (bxmppImUrl == null) {
			throw new RuntimeException("bxmpp-im properties file not found.");
		}
		
		Properties properties = new Properties();
		properties.load(new BufferedReader(new InputStreamReader(bxmppImUrl.openStream())));
		
		String provider = "BXMPP-IM";
		for (Object oRepalcementByte : properties.keySet()) {
			String sReplacementBytes = (String)oRepalcementByte;
			String keyword = (String)properties.get(sReplacementBytes);
			
			ReplacementBytes replacementBytes = ReplacementBytes.parse(sReplacementBytes);				
			replacementBytes.setProvider(provider);
			
			if (!ReplacementBytes.isNamespaceReplacementBytes(replacementBytes)) {
				defaultBxmppExtension.register(replacementBytes, keyword);
			}
		}
	}
	
	private BxmppExtension loadBxmppExtensionFromPropertiesFile(String configurationFilePath)
			throws IOException, ReduplicateBxmppReplacementException {
		URL configurationFileUrl = getClass().getClassLoader().getResource(configurationFilePath);
		String provider = getProvider(configurationFileUrl);
		
		Properties properties = new Properties();
		properties.load(new BufferedReader(new InputStreamReader(configurationFileUrl.openStream())));
		
		Namespace namespace = findNamespace(properties);
		if (namespace == null)
			throw new RuntimeException("Can't find namespace in BXMPP extension configuration file.");
		
		BxmppExtension bxmppExtension = new BxmppExtension(namespace);
		for (Object oRepalcementByte : properties.keySet()) {
			String sReplacementBytes = (String)oRepalcementByte;
			String keyword = (String)properties.get(sReplacementBytes);
			
			ReplacementBytes replacementBytes = ReplacementBytes.parse(sReplacementBytes);				
			replacementBytes.setProvider(provider);
			
			if (!ReplacementBytes.isNamespaceReplacementBytes(replacementBytes)) {
				bxmppExtension.register(replacementBytes, keyword);
			}
		}
		
		return bxmppExtension;
	}
	
	private Namespace findNamespace(Properties properties) {
		for (Object oRepalcementByte : properties.keySet()) {
			String sReplacementBytes = (String)oRepalcementByte;
			byte[] bytes = BinaryUtils.getBytesFromHexString(sReplacementBytes);
			if (bytes.length == 2)
				return new Namespace(new ReplacementBytes(bytes[0], bytes[1]), properties.getProperty(sReplacementBytes));
		}
		
		return null;
	}

	private String getProvider(URL url) {
		String path = url.getPath();
		int fileNameEndPosition = path.indexOf(".jar!/");
		if (fileNameEndPosition == -1) {
			fileNameEndPosition = path.indexOf("/target/classes/META-INF/");
		}
		
		path = path.substring(0, fileNameEndPosition);
		int fileNameStartPosition = path.lastIndexOf("/");
		if (fileNameEndPosition != -1) {
			return path.substring(fileNameStartPosition + 1);
		} else {
			return path;
		}
	}
	
	@Override
	public byte[] toBinary(Object obj) {
		registerObjectTypeIfNeed(obj.getClass());
		
		Message message = new Message();
		message.setObject(obj);
		
		byte[] data = binaryXmppProtocolConverter.toBinary(oxmFactory.getTranslatingFactory().translate(message));
		byte[] pureActionData = new byte[data.length - 4];
		pureActionData[0] = data[0];
		for (int i = 1; i < data.length - 4; i++) {
			pureActionData[i] = data[i + 4];
		}
		
		return pureActionData;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void registerObjectTypeIfNeed(Class<?> type) {
		if (LanExecution.class == type)
			return;
		
		if (registeredLanActionTypes.contains(type))
			return;
		
		if (!registeredObjectTypes.contains(type)) {
			ProtocolObject protocolObject = type.getAnnotation(ProtocolObject.class);
			if (protocolObject == null) {
				throw new IllegalArgumentException(String.format("Type '%s' isn't a protocol object type.", type.getName()));
			}
			
			ProtocolChain protocolChain = new MessageProtocolChain(
					new Protocol(protocolObject.namespace(), protocolObject.localName()));
			oxmFactory.register(protocolChain, new NamingConventionParserFactory<>(type));
			oxmFactory.register(type, new NamingConventionTranslatorFactory(type));
			
			registeredObjectTypes.add(type);
		}
	}

	@Override
	public <T> T toObject(Class<T> type, byte[] data) {
		registerObjectTypeIfNeed(type);
		
		return getMessage(data).getObject();
	}
	
	@Override
	public IBinaryXmppProtocolConverter getBinaryXmppProtocolConverter() {
		return binaryXmppProtocolConverter;
	}

	@Override
	public void registerLanAction(Class<?> lanActionType) {
		if (registeredLanActionTypes.contains(lanActionType))
			return;
		
		ProtocolObject protocolObject = lanActionType.getAnnotation(ProtocolObject.class);
		if (protocolObject == null) {
			throw new IllegalArgumentException(String.format("LAN action type %s isn't a protocol object type.", lanActionType.getName()));
		}
		
		ProtocolChain actionProtocolChain = new MessageProtocolChain(
				new Protocol(protocolObject.namespace(), protocolObject.localName()));
		oxmFactory.register(actionProtocolChain, new NamingConventionParserFactory<>(lanActionType));
		
		ProtocolChain lanActionProtocolChain = new MessageProtocolChain(LanExecution.PROTOCOL).
				next(new Protocol(protocolObject.namespace(), protocolObject.localName()));
		oxmFactory.register(lanActionProtocolChain, new NamingConventionParserFactory<>(lanActionType));
		oxmFactory.register(lanActionType, new NamingConventionTranslatorFactory<>(lanActionType));
		
		registeredLanActionTypes.add(lanActionType);
	}

	@Override
	public Object toObject(byte[] data) {
		return getMessage(data).getObject();
	}

	private Message getMessage(byte[] data) {
		byte[] wrappedByMessageData = new byte[data.length + 4];
		
		wrappedByMessageData[0] = data[0];
		for (int i = 0; i < 4; i++) {
			wrappedByMessageData[i + 1] = MESSAGE_WRAPPER_DATA[i];
		}
		for (int i = 0; i < data.length - 1; i++) {
			wrappedByMessageData[i + 5] = data[i + 1];
		}
		
		String xml = binaryXmppProtocolConverter.toXml(wrappedByMessageData);
		Message message = (Message)oxmFactory.getParsingFactory().parse(xml);
		
		return message;
	}

	@Override
	public Protocol readProtocol(byte[] data) {
		return binaryXmppProtocolConverter.readProtocol(data);
	}

	@Override
	public boolean unregisterLanAction(Class<?> lanActionType) {
		if (!registeredLanActionTypes.contains(lanActionType))
			return false;
		
		ProtocolObject protocolObject = lanActionType.getAnnotation(ProtocolObject.class);
		if (protocolObject == null) {
			throw new IllegalArgumentException(String.format("Type '%s' isn't a protocol object type.", lanActionType.getName()));
		}
		
		ProtocolChain lanActionProtocolChain = new MessageProtocolChain(LanExecution.PROTOCOL).
				next(new Protocol(protocolObject.namespace(), protocolObject.localName()));
		oxmFactory.unregister(lanActionProtocolChain);
		oxmFactory.unregister(lanActionType);
		
		registeredLanActionTypes.remove(lanActionType);
		
		return true;
	}
}
