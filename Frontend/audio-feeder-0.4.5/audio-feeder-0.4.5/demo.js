var demo =
/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};

/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {

/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;

/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};

/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);

/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;

/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}


/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;

/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;

/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";

/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	module.exports = function demo() {

	  // Note: this demo is using the pre-built AudioFeeder.js rather than
	  // including it locally via webpack.
	  /* global AudioFeeder */

	  var demoHtml = __webpack_require__(1);

	  var start = document.getElementById('start'),
	    stop = document.getElementById('stop'),
	    channels = 1,
	    rate = 48000,
	    sampleCounter = 0,
	    feeder = new AudioFeeder();

	  start.disabled = true;
	  feeder.init(channels, rate);
	  feeder.waitUntilReady(function() {
	    start.disabled = false;
	  });

	  function bufferSineWave(time) {
	    var freq = 261, // middle C
	      chunkSamples = Math.round(time * rate), // buffer 1s at a time
	      samples = Math.ceil(chunkSamples / freq) * freq,
	      buffer = new Float32Array(samples),
	      packet = [buffer];

	    for (var i = 0; i < samples; i++) {
	      buffer[i] = Math.sin((sampleCounter / rate) * freq * 2 * Math.PI);
	      sampleCounter++;
	    }

	    feeder.bufferData(packet);
	  }

	  start.addEventListener('click', function() {
	    start.disabled = true;
	    stop.disabled = false;

	    bufferSineWave(1); // pre-buffer 1s
	    feeder.start();
	  });

	  stop.addEventListener('click', function() {
	    stop.disabled = true;
	    start.disabled = false;
	    feeder.stop();
	  });

	  feeder.onbufferlow = function() {
	    console.log('buffer low');
	    while (feeder.durationBuffered < feeder.bufferThreshold * 2) {
	      bufferSineWave(1);
	    }
	  };

	  feeder.onstarved = function() {
	    console.log('starving');
	    bufferSineWave();
	  };

	  var muted = document.querySelector('input[name=muted]');
	  muted.addEventListener('click', function() {
	    feeder.muted = this.checked;
	  });

	  var volumes = document.querySelectorAll('input[name=volume]');
	  for (var i = 0; i < volumes.length; i++) {
	    volumes[i].addEventListener('click', function() {
	      feeder.volume = parseInt(this.value) / 100;
	    });
	  }

	  start.disabled = false;
	};


/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	module.exports = __webpack_require__.p + "demo.html";

/***/ }
/******/ ]);