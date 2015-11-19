'use strict';

var React = require('react-native');
var {
    NativeModules
} = React;

var MQTT = NativeModules.CMMCMQTTModule;

var modules = {
	MQTT: MQTT
}

module.exports = modules;
