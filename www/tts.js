// cordova.define("cordova-plugin-tts.tts", function(require, exports, module) {
// /*

//     Cordova Text-to-Speech Plugin
//     https://github.com/vilic/cordova-plugin-tts

//     by VILIC VANE
//     https://github.com/vilic

//     MIT License

// */

// exports.speak = function (text) {
//     return new Promise(function (resolve, reject) {
//         var options = {};

//         if (typeof text == 'string') {
//             options.text = text;
//         } else {
//             options = text;
//         }

//         cordova.exec(resolve, reject, 'TTS', 'speak', [options]);
//     });
// };

// exports.stop = function() {
//     return new Promise(function (resolve, reject) {
//         cordova.exec(resolve, reject, 'TTS', 'stop', []);
//     });
// };

// exports.checkLanguage = function() {
//     return new Promise(function (resolve, reject) {
//         cordova.exec(resolve, reject, 'TTS', 'checkLanguage', []);
//     });
// };

// exports.openInstallTts = function() {
//     return new Promise(function (resolve, reject) {
//         cordova.exec(resolve, reject, 'TTS', 'openInstallTts', []);
//     });
// };

// });


cordova.define("cordova-plugin-tts.tts", function(require, exports, module) {
    /*
     *
     * Licensed to the Apache Software Foundation (ASF) under one
     * or more contributor license agreements.  See the NOTICE file
     * distributed with this work for additional information
     * regarding copyright ownership.  The ASF licenses this file
     * to you under the Apache License, Version 2.0 (the
     * "License"); you may not use this file except in compliance
     * with the License.  You may obtain a copy of the License at
     *
     *   http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing,
     * software distributed under the License is distributed on an
     * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
     * KIND, either express or implied.  See the License for the
     * specific language governing permissions and limitations
     * under the License.
     *
    */
    
    var exec = require('cordova/exec');
    
    module.exports  = {
        speak: function(text, successCallback, errorCallback) {    
            var options = {};
            if (typeof text == 'string') {
                options.text = text;
            } else {
                options = text;
            }    
            return exec(successCallback, errorCallback, 'TTS', 'speak', [options]);
        },
        stop: function(successCallback, errorCallback) {     
            return exec(successCallback, errorCallback, 'TTS', 'stop', []);
        }
    }
});
    