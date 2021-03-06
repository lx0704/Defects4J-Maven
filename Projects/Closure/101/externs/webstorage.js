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
 * @fileoverview Definitions for W3C's WebStorage specificiation.
 * This file depends on html5.js.
*
 */

/**
 * @interface
 * @see http://www.w3.org/TR/2009/WD-webstorage-20091029/#the-storage-interface
 */
function Storage() {}

/**
 * @type {number}
 * @const
 */
Storage.prototype.length;

/**
 * @param {number} index
 * @return {*}
 */
Storage.prototype.key = function(index) {};

/**
 * @param {string} key
 * @return {*}
 */
Storage.prototype.getItem = function(key) {};

/**
 * @param {string} key
 * @param {*} data
 * @return {void}
 */
Storage.prototype.setItem = function(key, data) {};

/**
 * @param {string} key
 * @return {void}
 */
Storage.prototype.removeItem = function(key) {};

/**
 * @return {void}
 */
Storage.prototype.clear = function() {};

/**
 * @interface
 * @see http://www.w3.org/TR/2009/WD-webstorage-20091029/#the-sessionstorage-attribute
 */
function WindowSessionStorage() {}

/**
 * @type {Storage}
 */
WindowSessionStorage.prototype.sessionStorage;

/**
 * Window implements WindowSessionStorage
 *
 * @type {Storage}
 */
Window.prototype.sessionStorage;

/**
 * @interface
 * @see http://www.w3.org/TR/2009/WD-webstorage-20091029/#the-localstorage-attribute
 */
function WindowLocalStorage() {}

/**
 * @type {Storage}
 */
WindowLocalStorage.prototype.localStorage;

/**
 * Window implements WindowLocalStorage
 *
 * @type {Storage}
 */
Window.prototype.localStorage;

/**
 * This is the storage event interface.
 * @see http://www.w3.org/TR/2009/WD-webstorage-20091029/#the-storage-event
 * @extends {Event}
 * @constructor
 */
function StorageEvent() {}

/**
 * @type {string}
 */
StorageEvent.prototype.key;

/**
 * @type {*}
 */
StorageEvent.prototype.oldValue;

/**
 * @type {*}
 */
StorageEvent.prototype.newValue;

/**
 * @type {string}
 */
StorageEvent.prototype.url;

/**
 * @type {Storage}
 */
StorageEvent.prototype.storageArea;

/**
 * @param {string} typeArg
 * @param {boolean} canBubbleArg
 * @param {boolean} cancelableArg
 * @param {string} keyArg
 * @param {*} oldValueArg
 * @param {*} newValueArg
 * @param {string} urlArg
 * @param {Storage} storageAreaArg
 * @return {void}
 */
StorageEvent.prototype.initStorageEvent = function(typeArg, canBubbleArg,
                                                   cancelableArg, keyArg,
                                                   oldValueArg, newValueArg,
                                                   urlArg, storageAreaArg) {};

