/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @fileoverview Definitions for the JSON specification.
 * @see http://www.json.org/json2.js.
 * @externs
 */

var JSON = {};

/**
 * @param {string} jsonStr The string to parse.
 * @param {(function(string, *) : *)=} opt_reviver
 * @return {*} The JSON object.
 */
JSON.parse = function(jsonStr, opt_reviver) {};

/**
 * @param {*} jsonObj Input object.
 * @param {(Array.<string>|(function(string, *) : *)|null)=} opt_replacer
 * @param {(number|string)=} opt_space
 * @return {string} json string which represents jsonObj.
 */
JSON.stringify = function(jsonObj, opt_replacer, opt_space) {};

/**
 * As per EcmaScript 5, 15.12.3.
 * @param {string=} opt_key The JSON key for this object.
 * @return {*} The serializable representation of this object. Note that this
 *     need not be a string. See http://goo.gl/PEUvs.
 */
Object.prototype.toJSON = function(opt_key) {};
