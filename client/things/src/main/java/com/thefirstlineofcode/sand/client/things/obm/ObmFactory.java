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

public class ObmFactory implements IObmFactory {
	private static final byte[] MESSAGE_WRAPPER_DATA = new byte[] {(byte)0x60, (byte)0, (byte)1, (byte)0};
	
	private IOxmFactory oxmFactory;
	private AbstractBinaryXmppProtocolConverter<?> binaryXmppProtocolConverter;
	private List<Object> registeredObjects;
	
	private static IObmFactory instance;
	
	private ObmFactory() {
		oxmFactory = OxmService.createStandardOxmFactory();
		registeredObjects = new ArrayList<>();
		
		String[] configFiles = loadBxmppExtensionConfigurations();
		binaryXmppProtocolConverter = createBinaryXmppProtocolConverter(configFiles);
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
			
			BxmppExtension defaultBxmppExtension = new DefaultBxmppExtension();
			defaultBxmppExtension.register(new ReplacementBytes((byte)0x60), "message");
			binaryXmppProtocolConverter.register(defaultBxmppExtension);
			
			for (String configFile : configFiles) {
				loadBxmppExtensionFromPropertiesFile(binaryXmppProtocolConverter,
						"META-INF/" + configFile);
			}
			
			return binaryXmppProtocolConverter;
		} catch (IOException e) {
			throw new RuntimeException("IO exception.", e);
		} catch (ReduplicateBxmppReplacementException e) {
			throw new RuntimeException("Reduplicate BXMPP replacement.", e);
		}
	}
	
	private void loadBxmppExtensionFromPropertiesFile(AbstractBinaryXmppProtocolConverter<?> bxmppProtocolConverter,
			String configurationFilePath)
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
		
		bxmppProtocolConverter.register(bxmppExtension);
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
	
	public static synchronized IObmFactory createInstance() {
		if (instance == null) {
			instance = new ObmFactory();
		}
		
		return instance;
	}
	
	@Override
	public byte[] toBinary(Object obj) {
		registerTypeIfNeed(obj.getClass());
		
		Message message = new Message();
		message.setObject(obj);
		
		byte[] data = binaryXmppProtocolConverter.toBinary(oxmFactory.getTranslatingFactory().translate(message));
		byte[] amendedData = new byte[data.length - 4];
		amendedData[0] = data[0];
		for (int i = 1; i < data.length - 4; i++) {
			amendedData[i] = data[i + 4];
		}
		
		return amendedData;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void registerTypeIfNeed(Class<?> type) {
		if (!registeredObjects.contains(type)) {
			ProtocolObject projectObject = type.getAnnotation(ProtocolObject.class);
			ProtocolChain protocolChain = new MessageProtocolChain(
					new Protocol(projectObject.namespace(), projectObject.localName()));
			oxmFactory.register(protocolChain, new NamingConventionParserFactory<>(type));
			oxmFactory.register(type, new NamingConventionTranslatorFactory(type));
		}
	}

	@Override
	public <T> T toObject(Class<T> type, byte[] data) {
		registerTypeIfNeed(type);
		
		byte[] amendedData = new byte[data.length + 4];
		
		amendedData[0] = data[0];
		for (int i = 0; i < 4; i++) {
			amendedData[i + 1] = MESSAGE_WRAPPER_DATA[i];
		}
		for (int i = 0; i < data.length - 1; i++) {
			amendedData[i + 5] = data[i + 1];
		}
		
		String xml = binaryXmppProtocolConverter.toXml(amendedData);
		Message message = (Message)oxmFactory.getParsingFactory().parse(xml);
		
		return message.getObject();
	}
	
	public IBinaryXmppProtocolConverter getBinaryXmppProtocolConverter() {
		return binaryXmppProtocolConverter;
	}
}
