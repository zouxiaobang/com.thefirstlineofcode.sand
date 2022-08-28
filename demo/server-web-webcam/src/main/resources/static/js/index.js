var videoOutput;
var peerConnection;

window.onload = function() {
	videoOutput = document.getElementById('videoOutput');
	showSpinner();
	
	createPeerConnection();
	
	if (!peerConnection) {
		alert("Can't create peer connection.");
		return;
	}
	
	offer();
}

function createPeerConnection() {
	if (!peerConnection) {
		var configuration = {
			iceServers: [
				{
					"urls": "stun:47.115.36.99:3478",
				},
				{
					"urls": "turn:47.115.36.99:3478",
					"username": "webrtc",
					"credential": "18814358626"
				}
			]
		};
		peerConnection = new RTCPeerConnection(configuration);
		
		peerConnection.addEventListener("icecandidate", onIceCandidate);
		peerConnection.addEventListener("track", onTrack);
		peerConnection.addEventListener("iceconnectionstatechange", onIceConnectionStateChange);
		peerConnection.addEventListener("signalingstatechange", onSignalingStateChange);
		peerConnection.addEventListener("connectionstatechange", onConnectionStateChange);
		
		try {
			var transceiver = peerConnection.addTransceiver("video",
				{
					direction: "recvonly"
				}
			);
		} catch(error) {
			alert("Failed to add video track to peer connection.");
		}
	}
}

function onConnectionStateChange(event) {
	alert("Connection state changed. Current state: " + peerConnection.connectionState);
}

function onSignalingStateChange(event) {
	alert("Signaling state changed. Current state: " + peerConnection.signalingState);
}

function onIceConnectionStateChange(event) {
	alert("ICE connection state changed. Current state: " + peerConnection.iceConnectionState);
}

function offer() {
	if (!peerConnection) {
		createPeerConnection();
		
		if (!peerConnection) {
			alert("No peer connection available.");
			return;
		}
	}
	
	peerConnection.createOffer().
		then((offer) => {
			localSessionDescription = offer;
			androidApp.processJavascriptSignal("OFFER", localSessionDescription.sdp);
		}).catch((error) => {
			alert("Can't create offer. Error object: " + error);
		});
}

function showSpinner() {
	videoOutput.poster = './img/transparent-1px.png';
	videoOutput.style.background = 'center transparent url("./img/spinner.gif") no-repeat';
}

function onIceCandidate(event) {
	if (event.candidate != null) {
		androidApp.processJavascriptSignal("ICE_CANDIDATE_FOUND", JSON.stringify(event.candidate.toJSON()));
	}
}

function answered(lineSeparatorsHiddenAnswerSdp) {
	var answerSdpLines = lineSeparatorsHiddenAnswerSdp.split("$$");
	
	var answerSdp = new String();
	for (var i = 0; i < answerSdpLines.length; i++) {
		answerSdp += answerSdpLines[i];
		answerSdp += "\n";
	}
	
	peerConnection.setLocalDescription(localSessionDescription).
		then(() => {
			var sessionDescriptionInit = {type: "answer", sdp: answerSdp};
			peerConnection.setRemoteDescription(sessionDescriptionInit).
				then(() => {
					// Everything is ok!
					// Do nothing.
				}).catch((error) => {
					alert("Can't set remote session description. Error object: " + error);
				});
		}).catch((error) => {
			alert("Can't set local session description. Error object: " + error);
		});
}

function showTransceivers() {
	peerConnection.getTransceivers().forEach((transceiver) => {
		if (transceiver.sender == null && transceiver.receiver == null) {
			alert("Both of sender and receiver are null!");
			return;
		}
			
		if (transceiver.sender != null && transceiver.receiver != null) {
			alert("Found sender and receiver.");
			showSenderReceiverTrackKind(transceiver.sender, transceiver.receiver);
			return;
		}
		
		if (transceiver.sender != null) {
			showSenderTrackKind(transceiver.sender);
		} else {
			showReceiverTrackKind(transceiver.receiver);
		}
	});
}

function showSenderReceiverTrackKind(sender, receiver) {
	if (sender.track == null && receiver.track == null) {
		alert("Both track of sender and receiver are null.");
	}
	
	if (sender.track != null && receiver.track != null) {
		alert("Track kind of sender: " + sender.track.kind +
			", Track kind of receiver: " + receiver.track.kind);
	} else if (sender.track != null) {
		alert("Track kind of sender: " + sender.track.kind +
			", Track of receiver is null.");
	} else {
		alert("Track kind of receiver: " + receiver.track.kind +
			", Track of sender is null.");
	}
}

function showSenderTrackKind(sender) {
	if (sender.track != null) {
		alert("Found a sender. Track kind of sender: " + sender.track.kind);
	} else {
		alert("Found a sender. But it's track is null.");
	}
}

function showReceiverTrackKind(receiver) {
	if (receiver.track != null) {
		alert("Found a receiver. Track kind of receiver: " + receiver.track.kind);
	} else {
		alert("Found a receiver. But it's track is null.");
	}
}

function iceCandidateFound(quotesHiddenCandidate) {
	var candidateFragments = quotesHiddenCandidate.split("$$");
	
	var sCandidate = new String();
	for (var i = 0; i < candidateFragments.length; i++) {
		sCandidate += candidateFragments[i];
		if (i != (candidateFragments.length -1))
			sCandidate += "\"";
	}
	
	var jsonCandidate;
	try {
		jsonCandidate = JSON.parse(sCandidate);
	} catch(error) {
		alert("Can't parse ICE candidate info. Error object: " + error);
		return;
	}
	
	var candidate = new RTCIceCandidate(
		{
			candidate: jsonCandidate["candidate"],
			sdpMid: jsonCandidate["sdpMid"],
			sdpMLineIndex: jsonCandidate["sdpMLineIndex"]
		}
	);
	
	peerConnection.addIceCandidate(candidate).
		then(() => {
			alert("ICE candidate has added to peer connection. Candidate info: " + sCandidate);
		}).catch((error) => {
			alert("Can't add ICE candidate to peer connection. Error object: " + error);
		});
}

function onTrack(event) {
alert("onTrack.");
	videoOutput.srcObject = new MediaStream([event.track]);
}
