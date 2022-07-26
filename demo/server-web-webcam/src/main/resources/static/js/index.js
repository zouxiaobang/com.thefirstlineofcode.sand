var videoOutput;
var localVideoTrack;
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
	
	/*const mediaStreamConstraints = {
		audio: true,
		video: true
	};
	navigator.mediaDevices.getUserMedia(mediaStreamConstraints).
		then(mediaStream => {
			var videoTracks = mediaStream.getVideoTracks();
			if (videoTracks.length == 1) {
				localVideoTrack = videoTracks[0];
				createPeerConnection();
			}
     	}).catch(error => {
        	alert("Can't get user media.");
     	});*/
}

function createPeerConnection() {
	if (!peerConnection) {
		peerConnection = new RTCPeerConnection();
		peerConnection.ontrack = (event) => {
			alert("Track event object: " + event);
		};
	}
}

function offer() {
	if (!peerConnection) {
		createPeerConnection();
		
		if (!peerConnection) {
			alert("No peer connection available.");
			return;
		}
	}
	
	var options = {
		offerToReceiveAudio: false,
		offerToReceiveVideo: true
	};
	peerConnection.createOffer().
		then(function(offer) {
			androidApp.processJavascriptSignal("OFFER", offer.sdp);
		}).catch(function(error) {
			alert("Can't generate offer. Error object: " + error);
		});
}

function showSpinner() {
	videoOutput.poster = './img/transparent-1px.png';
	videoOutput.style.background = 'center transparent url("./img/spinner.gif") no-repeat';
}

function onIceCandidate(candidate) {
	var message = {
		id : 'onIceCandidate',
		candidate : candidate
	};
	sendMessage(message);
}
