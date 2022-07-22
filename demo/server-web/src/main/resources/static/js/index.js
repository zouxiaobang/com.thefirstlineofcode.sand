var videoOutput;
var webRtcPeer;

window.onload = function() {
	videoOutput = document.getElementById('videoOutput');
	showSpinner();
	
	var options = {
		remoteVideo : videoOutput,
		onicecandidate : onIceCandidate,
		onerror : onError
	};
	
	webRtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendrecv(options,
		function(error) {
			if (error) {
				alert("Can't create WEB RTC Peer. Error object is " + error);
				return;
			}
			
			webRtcPeer.generateOffer(onOfferSdp);
		}
	);
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

function onError() {
	alert("Error occurred.");
}
		
function onOfferSdp(error, offerSdp) {
	if (error) {
		alert("Error generating the offer. Error object is " + error);
		return;
	}
	
	androidApp.offerSdp(offerSdp);
}
