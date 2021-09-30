package com.firstlinecode.sand.client.ibdr;

import java.util.ArrayList;
import java.util.List;

import javax.security.cert.X509Certificate;

import com.firstlinecode.chalk.core.AbstractChatClient;
import com.firstlinecode.chalk.core.stream.AbstractStreamer;
import com.firstlinecode.chalk.core.stream.IStreamNegotiant;
import com.firstlinecode.chalk.core.stream.IStreamer;
import com.firstlinecode.chalk.core.stream.StandardStreamConfig;
import com.firstlinecode.chalk.core.stream.StreamConfig;
import com.firstlinecode.chalk.core.stream.negotiants.InitialStreamNegotiant;
import com.firstlinecode.chalk.core.stream.negotiants.tls.IPeerCertificateTruster;
import com.firstlinecode.chalk.core.stream.negotiants.tls.TlsNegotiant;
import com.firstlinecode.chalk.network.IConnection;
import com.firstlinecode.chalk.network.SocketConnection;
import com.firstlinecode.sand.protocols.ibdr.Register;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolChain;
import com.thefirstlineofcode.basalt.protocol.core.stream.Features;

class IbdrChatClient extends AbstractChatClient {
	
	private IPeerCertificateTruster peerCertificateTruster;

	public IbdrChatClient(StreamConfig streamConfig) {
		this(streamConfig, new SocketConnection());
	}
	
	public IbdrChatClient(StreamConfig streamConfig, IConnection connection) {
		super(streamConfig, connection);
	}
	
	public void setPeerCertificateTruster(IPeerCertificateTruster peerCertificateTruster) {
		this.peerCertificateTruster = peerCertificateTruster;
	}

	public IPeerCertificateTruster getPeerCertificateTruster() {
		return peerCertificateTruster;
	}

	@Override
	protected IStreamer createStreamer(StreamConfig streamConfig, IConnection connection) {
		IbdrStreamer streamer = new IbdrStreamer(getStreamConfig(), connection);
		streamer.setConnectionListener(this);
		streamer.setNegotiationListener(this);
		
		if (peerCertificateTruster != null) {
			streamer.setPeerCertificateTruster(peerCertificateTruster);
		} else {
			// always trust peer certificate
			streamer.setPeerCertificateTruster(new IPeerCertificateTruster() {				
				@Override
				public boolean accept(X509Certificate[] certificates) {
					return true;
				}
			});
		}
		
		return streamer;
	}

	private class IbdrStreamer extends AbstractStreamer {
		private IPeerCertificateTruster certificateTruster;
		
		public IbdrStreamer(StreamConfig streamConfig, IConnection connection) {
			super(streamConfig, connection);
		}
		
		@Override
		protected List<IStreamNegotiant> createNegotiants() {
			List<IStreamNegotiant> negotiants = new ArrayList<>();
			
			InitialStreamNegotiant initialStreamNegotiant = createIbdrSupportedInitialStreamNegotiant();
			negotiants.add(initialStreamNegotiant);
			
			TlsNegotiant tls = createIbdrSupportedTlsNegotiant();
			negotiants.add(tls);
			
			IbdrNegotiant ibdr = createIbdrNegotiant();
			negotiants.add(ibdr);
			
			setNegotiationReadResponseTimeout(negotiants);
			
			return negotiants;
		}

		private IbdrNegotiant createIbdrNegotiant() {
			return new IbdrNegotiant();
		}
		
		public void setPeerCertificateTruster(IPeerCertificateTruster certificateTruster) {
			this.certificateTruster = certificateTruster;
		}

		private InitialStreamNegotiant createIbdrSupportedInitialStreamNegotiant() {
			return new IbdrSupportedInitialStreamNegotiant(streamConfig.getHost(), streamConfig.getLang());
		}
		
		private TlsNegotiant createIbdrSupportedTlsNegotiant() {
			TlsNegotiant tls = new IbdrSupportedTlsNegotiant(streamConfig.getHost(), streamConfig.getLang(),
					((StandardStreamConfig)streamConfig).isTlsPreferred());
			tls.setPeerCertificateTruster(certificateTruster);
			return tls;
		}
	}
	
	private static class IbdrSupportedInitialStreamNegotiant extends InitialStreamNegotiant {
		
		static {
			oxmFactory.register(ProtocolChain.first(Features.PROTOCOL).next(Register.PROTOCOL),
					new NamingConventionParserFactory<>(Register.class));
		}

		public IbdrSupportedInitialStreamNegotiant(String hostName, String lang) {
			super(hostName, lang);
		}
		
	}
	
	private static class IbdrSupportedTlsNegotiant extends TlsNegotiant {
		
		static {
			oxmFactory.register(ProtocolChain.first(Features.PROTOCOL).next(Register.PROTOCOL),
					new NamingConventionParserFactory<>(Register.class));
		}

		public IbdrSupportedTlsNegotiant(String hostName, String lang, boolean tlsPreferred) {
			super(hostName, lang, tlsPreferred);
		}
		
	}
	
}
