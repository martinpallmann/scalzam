
var errorHandler = function() {
  console.log("Noes.");
};

navigator.getUserMedia({audio: true}, function(mediaStream) {
  window.recordRTC = RecordRTC(mediaStream);
}, errorHandler);

var startRecording = function() {
  recordRTC.startRecording();
};

var stopRecording = function() {
  recordRTC.stopRecording(function(audioURL) {
    var blob = recordRTC.getBlob();
    postAudio(blob);
  });
};

var postAudio = function(blob) {

  var fileType = 'audio';
  var fileName = 'audio.wav';

  var formData = new FormData();
  formData.append(fileType + '-filename', fileName);
  formData.append(fileType + '-blob', blob);

  jQuery.ajax({
    url: "/_audio",
    type: "POST",
    data: formData,
    processData: false,
    contentType: false,
    success: function (res) {
      console.log(res);
    }
  });
};


var viewSpectrum = function() {
  jQuery.ajax({
    url: "/_spectrum",
    type: "GET",
    success: function(res) {
      console.loc(res);
    }
  });
};

