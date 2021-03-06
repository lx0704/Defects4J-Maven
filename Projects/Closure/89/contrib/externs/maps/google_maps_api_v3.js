/*
 * Copyright 2010 The Closure Compiler Authors.
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
 * @fileoverview Externs for the Google Maps V3 API.
 * @see http://code.google.com/apis/maps/documentation/javascript/reference.html
 * @externs
 */

google.maps = {};

/**
 * @constructor
 * @param {Node} mapDiv
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.Map = function(mapDiv, opt_opts) {};

/**
 * @param {google.maps.LatLngBounds} bounds
 * @return {undefined}
 */
google.maps.Map.prototype.fitBounds = function(bounds) {};

/**
 * @nosideeffects
 * @return {google.maps.LatLngBounds}
 */
google.maps.Map.prototype.getBounds = function() {};

/**
 * @nosideeffects
 * @return {google.maps.LatLng}
 */
google.maps.Map.prototype.getCenter = function() {};

/**
 * @nosideeffects
 * @return {Node}
 */
google.maps.Map.prototype.getDiv = function() {};

/**
 * @nosideeffects
 * @return {google.maps.MapTypeId}
 */
google.maps.Map.prototype.getMapTypeId = function() {};

/**
 * @nosideeffects
 * @return {google.maps.Projection}
 */
google.maps.Map.prototype.getProjection = function() {};

/**
 * @nosideeffects
 * @return {google.maps.StreetViewPanorama}
 */
google.maps.Map.prototype.getStreetView = function() {};

/**
 * @nosideeffects
 * @return {number}
 */
google.maps.Map.prototype.getZoom = function() {};

/**
 * @param {number} x
 * @param {number} y
 * @return {undefined}
 */
google.maps.Map.prototype.panBy = function(x, y) {};

/**
 * @param {google.maps.LatLng} latLng
 * @return {undefined}
 */
google.maps.Map.prototype.panTo = function(latLng) {};

/**
 * @param {google.maps.LatLngBounds} latLngBounds
 * @return {undefined}
 */
google.maps.Map.prototype.panToBounds = function(latLngBounds) {};

/**
 * @param {google.maps.LatLng} latlng
 * @return {undefined}
 */
google.maps.Map.prototype.setCenter = function(latlng) {};

/**
 * @param {google.maps.MapTypeId} mapTypeId
 * @return {undefined}
 */
google.maps.Map.prototype.setMapTypeId = function(mapTypeId) {};

/**
 * @param {Object.<string, *>} options
 * @return {undefined}
 */
google.maps.Map.prototype.setOptions = function(options) {};

/**
 * @param {google.maps.StreetViewPanorama} panorama
 * @return {undefined}
 */
google.maps.Map.prototype.setStreetView = function(panorama) {};

/**
 * @param {number} zoom
 * @return {undefined}
 */
google.maps.Map.prototype.setZoom = function(zoom) {};

/**
 * @type {Array.<google.maps.MVCArray.<Node>>}
 */
google.maps.Map.prototype.controls;

/**
 * @type {google.maps.MapTypeRegistry}
 */
google.maps.Map.prototype.mapTypes;

/**
 * @type {google.maps.MVCArray.<google.maps.MapType>}
 */
google.maps.Map.prototype.overlayMapTypes;

/**
 * @constructor
 */
google.maps.MapOptions = function() {};

/**
 * @type {string}
 */
google.maps.MapOptions.prototype.backgroundColor;

/**
 * @type {google.maps.LatLng}
 */
google.maps.MapOptions.prototype.center;

/**
 * @type {boolean}
 */
google.maps.MapOptions.prototype.disableDefaultUI;

/**
 * @type {boolean}
 */
google.maps.MapOptions.prototype.disableDoubleClickZoom;

/**
 * @type {boolean}
 */
google.maps.MapOptions.prototype.draggable;

/**
 * @type {string}
 */
google.maps.MapOptions.prototype.draggableCursor;

/**
 * @type {string}
 */
google.maps.MapOptions.prototype.draggingCursor;

/**
 * @type {boolean}
 */
google.maps.MapOptions.prototype.keyboardShortcuts;

/**
 * @type {boolean}
 */
google.maps.MapOptions.prototype.mapTypeControl;

/**
 * @type {Object.<string, *>}
 */
google.maps.MapOptions.prototype.mapTypeControlOptions;

/**
 * @type {google.maps.MapTypeId}
 */
google.maps.MapOptions.prototype.mapTypeId;

/**
 * @type {boolean}
 */
google.maps.MapOptions.prototype.navigationControl;

/**
 * @type {Object.<string, *>}
 */
google.maps.MapOptions.prototype.navigationControlOptions;

/**
 * @type {boolean}
 */
google.maps.MapOptions.prototype.noClear;

/**
 * @type {boolean}
 */
google.maps.MapOptions.prototype.scaleControl;

/**
 * @type {Object.<string, *>}
 */
google.maps.MapOptions.prototype.scaleControlOptions;

/**
 * @type {boolean}
 */
google.maps.MapOptions.prototype.scrollwheel;

/**
 * @type {google.maps.StreetViewPanorama}
 */
google.maps.MapOptions.prototype.streetView;

/**
 * @type {boolean}
 */
google.maps.MapOptions.prototype.streetViewControl;

/**
 * @type {Object.<string, *>}
 */
google.maps.MapOptions.prototype.streetViewControlOptions;

/**
 * @type {number}
 */
google.maps.MapOptions.prototype.zoom;

/**
 * @constructor
 */
google.maps.MapTypeId = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.MapTypeId.HYBRID;

/**
 * @type {string|number}
 * @const
 */
google.maps.MapTypeId.ROADMAP;

/**
 * @type {string|number}
 * @const
 */
google.maps.MapTypeId.SATELLITE;

/**
 * @type {string|number}
 * @const
 */
google.maps.MapTypeId.TERRAIN;

/**
 * @constructor
 */
google.maps.MapTypeControlOptions = function() {};

/**
 * @type {Array.<google.maps.MapTypeId>|Array.<string>}
 */
google.maps.MapTypeControlOptions.prototype.mapTypeIds;

/**
 * @type {google.maps.ControlPosition}
 */
google.maps.MapTypeControlOptions.prototype.position;

/**
 * @type {google.maps.MapTypeControlStyle}
 */
google.maps.MapTypeControlOptions.prototype.style;

/**
 * @constructor
 */
google.maps.MapTypeControlStyle = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.MapTypeControlStyle.DEFAULT;

/**
 * @type {string|number}
 * @const
 */
google.maps.MapTypeControlStyle.DROPDOWN_MENU;

/**
 * @type {string|number}
 * @const
 */
google.maps.MapTypeControlStyle.HORIZONTAL_BAR;

/**
 * @constructor
 */
google.maps.NavigationControlOptions = function() {};

/**
 * @type {google.maps.ControlPosition}
 */
google.maps.NavigationControlOptions.prototype.position;

/**
 * @type {google.maps.NavigationControlStyle}
 */
google.maps.NavigationControlOptions.prototype.style;

/**
 * @constructor
 */
google.maps.NavigationControlStyle = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.NavigationControlStyle.ANDROID;

/**
 * @type {string|number}
 * @const
 */
google.maps.NavigationControlStyle.DEFAULT;

/**
 * @type {string|number}
 * @const
 */
google.maps.NavigationControlStyle.SMALL;

/**
 * @type {string|number}
 * @const
 */
google.maps.NavigationControlStyle.ZOOM_PAN;

/**
 * @constructor
 */
google.maps.ScaleControlOptions = function() {};

/**
 * @type {google.maps.ControlPosition}
 */
google.maps.ScaleControlOptions.prototype.position;

/**
 * @type {google.maps.ScaleControlStyle}
 */
google.maps.ScaleControlOptions.prototype.style;

/**
 * @constructor
 */
google.maps.ScaleControlStyle = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.ScaleControlStyle.DEFAULT;

/**
 * @constructor
 */
google.maps.StreetViewControlOptions = function() {};

/**
 * @type {google.maps.ControlPosition}
 */
google.maps.StreetViewControlOptions.prototype.position;

/**
 * @constructor
 */
google.maps.ControlPosition = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.BOTTOM_CENTER;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.BOTTOM_LEFT;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.BOTTOM_RIGHT;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.LEFT_BOTTOM;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.LEFT_CENTER;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.LEFT_TOP;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.RIGHT_BOTTOM;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.RIGHT_CENTER;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.RIGHT_TOP;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.TOP_CENTER;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.TOP_LEFT;

/**
 * @type {string|number}
 * @const
 */
google.maps.ControlPosition.TOP_RIGHT;

/**
 * @constructor
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.Marker = function(opt_opts) {};

/**
 * @nosideeffects
 * @return {boolean}
 */
google.maps.Marker.prototype.getClickable = function() {};

/**
 * @nosideeffects
 * @return {string}
 */
google.maps.Marker.prototype.getCursor = function() {};

/**
 * @nosideeffects
 * @return {boolean}
 */
google.maps.Marker.prototype.getDraggable = function() {};

/**
 * @nosideeffects
 * @return {boolean}
 */
google.maps.Marker.prototype.getFlat = function() {};

/**
 * @nosideeffects
 * @return {string|google.maps.MarkerImage}
 */
google.maps.Marker.prototype.getIcon = function() {};

/**
 * @nosideeffects
 * @return {google.maps.Map|google.maps.StreetViewPanorama}
 */
google.maps.Marker.prototype.getMap = function() {};

/**
 * @nosideeffects
 * @return {google.maps.LatLng}
 */
google.maps.Marker.prototype.getPosition = function() {};

/**
 * @nosideeffects
 * @return {string|google.maps.MarkerImage}
 */
google.maps.Marker.prototype.getShadow = function() {};

/**
 * @nosideeffects
 * @return {google.maps.MarkerShape}
 */
google.maps.Marker.prototype.getShape = function() {};

/**
 * @nosideeffects
 * @return {string}
 */
google.maps.Marker.prototype.getTitle = function() {};

/**
 * @nosideeffects
 * @return {boolean}
 */
google.maps.Marker.prototype.getVisible = function() {};

/**
 * @nosideeffects
 * @return {number}
 */
google.maps.Marker.prototype.getZIndex = function() {};

/**
 * @param {boolean} flag
 * @return {undefined}
 */
google.maps.Marker.prototype.setClickable = function(flag) {};

/**
 * @param {string} cursor
 * @return {undefined}
 */
google.maps.Marker.prototype.setCursor = function(cursor) {};

/**
 * @param {boolean} flag
 * @return {undefined}
 */
google.maps.Marker.prototype.setDraggable = function(flag) {};

/**
 * @param {boolean} flag
 * @return {undefined}
 */
google.maps.Marker.prototype.setFlat = function(flag) {};

/**
 * @param {string|google.maps.MarkerImage} icon
 * @return {undefined}
 */
google.maps.Marker.prototype.setIcon = function(icon) {};

/**
 * @param {google.maps.Map|google.maps.StreetViewPanorama} map
 * @return {undefined}
 */
google.maps.Marker.prototype.setMap = function(map) {};

/**
 * @param {Object.<string, *>} options
 * @return {undefined}
 */
google.maps.Marker.prototype.setOptions = function(options) {};

/**
 * @param {google.maps.LatLng} latlng
 * @return {undefined}
 */
google.maps.Marker.prototype.setPosition = function(latlng) {};

/**
 * @param {string|google.maps.MarkerImage} shadow
 * @return {undefined}
 */
google.maps.Marker.prototype.setShadow = function(shadow) {};

/**
 * @param {google.maps.MarkerShape} shape
 * @return {undefined}
 */
google.maps.Marker.prototype.setShape = function(shape) {};

/**
 * @param {string} title
 * @return {undefined}
 */
google.maps.Marker.prototype.setTitle = function(title) {};

/**
 * @param {boolean} visible
 * @return {undefined}
 */
google.maps.Marker.prototype.setVisible = function(visible) {};

/**
 * @param {number} zIndex
 * @return {undefined}
 */
google.maps.Marker.prototype.setZIndex = function(zIndex) {};

/**
 * @constructor
 */
google.maps.MarkerOptions = function() {};

/**
 * @type {boolean}
 */
google.maps.MarkerOptions.prototype.clickable;

/**
 * @type {string}
 */
google.maps.MarkerOptions.prototype.cursor;

/**
 * @type {boolean}
 */
google.maps.MarkerOptions.prototype.draggable;

/**
 * @type {boolean}
 */
google.maps.MarkerOptions.prototype.flat;

/**
 * @type {string|google.maps.MarkerImage}
 */
google.maps.MarkerOptions.prototype.icon;

/**
 * @type {google.maps.Map|google.maps.StreetViewPanorama}
 */
google.maps.MarkerOptions.prototype.map;

/**
 * @type {google.maps.LatLng}
 */
google.maps.MarkerOptions.prototype.position;

/**
 * @type {string|google.maps.MarkerImage}
 */
google.maps.MarkerOptions.prototype.shadow;

/**
 * @type {google.maps.MarkerShape}
 */
google.maps.MarkerOptions.prototype.shape;

/**
 * @type {string}
 */
google.maps.MarkerOptions.prototype.title;

/**
 * @type {boolean}
 */
google.maps.MarkerOptions.prototype.visible;

/**
 * @type {number}
 */
google.maps.MarkerOptions.prototype.zIndex;

/**
 * @constructor
 * @param {string} url
 * @param {google.maps.Size=} size
 * @param {google.maps.Point=} origin
 * @param {google.maps.Point=} anchor
 * @param {google.maps.Size=} scaledSize
 */
google.maps.MarkerImage = function(url, size, origin, anchor, scaledSize) {};

/**
 * @type {google.maps.Size}
 */
google.maps.MarkerImage.prototype.size;

/**
 * @constructor
 */
google.maps.MarkerShape = function() {};

/**
 * @type {Array.<number>}
 */
google.maps.MarkerShape.prototype.coord;

/**
 * @type {string}
 */
google.maps.MarkerShape.prototype.type;

/**
 * @constructor
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.InfoWindow = function(opt_opts) {};

/**
 * @return {undefined}
 */
google.maps.InfoWindow.prototype.close = function() {};

/**
 * @nosideeffects
 * @return {string|Node}
 */
google.maps.InfoWindow.prototype.getContent = function() {};

/**
 * @nosideeffects
 * @return {google.maps.LatLng}
 */
google.maps.InfoWindow.prototype.getPosition = function() {};

/**
 * @nosideeffects
 * @return {number}
 */
google.maps.InfoWindow.prototype.getZIndex = function() {};

/**
 * @param {google.maps.Map|google.maps.StreetViewPanorama} map
 * @param {google.maps.MVCObject=} anchor
 * @return {undefined}
 */
google.maps.InfoWindow.prototype.open = function(map, anchor) {};

/**
 * @param {string|Node} content
 * @return {undefined}
 */
google.maps.InfoWindow.prototype.setContent = function(content) {};

/**
 * @param {Object.<string, *>} options
 * @return {undefined}
 */
google.maps.InfoWindow.prototype.setOptions = function(options) {};

/**
 * @param {google.maps.LatLng} position
 * @return {undefined}
 */
google.maps.InfoWindow.prototype.setPosition = function(position) {};

/**
 * @param {number} zIndex
 * @return {undefined}
 */
google.maps.InfoWindow.prototype.setZIndex = function(zIndex) {};

/**
 * @constructor
 */
google.maps.InfoWindowOptions = function() {};

/**
 * @type {string|Node}
 */
google.maps.InfoWindowOptions.prototype.content;

/**
 * @type {boolean}
 */
google.maps.InfoWindowOptions.prototype.disableAutoPan;

/**
 * @type {number}
 */
google.maps.InfoWindowOptions.prototype.maxWidth;

/**
 * @type {google.maps.Size}
 */
google.maps.InfoWindowOptions.prototype.pixelOffset;

/**
 * @type {google.maps.LatLng}
 */
google.maps.InfoWindowOptions.prototype.position;

/**
 * @type {number}
 */
google.maps.InfoWindowOptions.prototype.zIndex;

/**
 * @constructor
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.Polyline = function(opt_opts) {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.Polyline.prototype.getMap = function() {};

/**
 * @nosideeffects
 * @return {google.maps.MVCArray.<google.maps.LatLng>}
 */
google.maps.Polyline.prototype.getPath = function() {};

/**
 * @param {google.maps.Map} map
 * @return {undefined}
 */
google.maps.Polyline.prototype.setMap = function(map) {};

/**
 * @param {Object.<string, *>} options
 * @return {undefined}
 */
google.maps.Polyline.prototype.setOptions = function(options) {};

/**
 * @param {google.maps.MVCArray.<google.maps.LatLng>|Array.<google.maps.LatLng>} path
 * @return {undefined}
 */
google.maps.Polyline.prototype.setPath = function(path) {};

/**
 * @constructor
 */
google.maps.PolylineOptions = function() {};

/**
 * @type {boolean}
 */
google.maps.PolylineOptions.prototype.clickable;

/**
 * @type {boolean}
 */
google.maps.PolylineOptions.prototype.geodesic;

/**
 * @type {google.maps.Map}
 */
google.maps.PolylineOptions.prototype.map;

/**
 * @type {google.maps.MVCArray.<google.maps.LatLng>|Array.<google.maps.LatLng>}
 */
google.maps.PolylineOptions.prototype.path;

/**
 * @type {string}
 */
google.maps.PolylineOptions.prototype.strokeColor;

/**
 * @type {number}
 */
google.maps.PolylineOptions.prototype.strokeOpacity;

/**
 * @type {number}
 */
google.maps.PolylineOptions.prototype.strokeWeight;

/**
 * @type {number}
 */
google.maps.PolylineOptions.prototype.zIndex;

/**
 * @constructor
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.Polygon = function(opt_opts) {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.Polygon.prototype.getMap = function() {};

/**
 * @nosideeffects
 * @return {google.maps.MVCArray.<google.maps.LatLng>}
 */
google.maps.Polygon.prototype.getPath = function() {};

/**
 * @nosideeffects
 * @return {google.maps.MVCArray.<google.maps.MVCArray.<google.maps.LatLng>>}
 */
google.maps.Polygon.prototype.getPaths = function() {};

/**
 * @param {google.maps.Map} map
 * @return {undefined}
 */
google.maps.Polygon.prototype.setMap = function(map) {};

/**
 * @param {Object.<string, *>} options
 * @return {undefined}
 */
google.maps.Polygon.prototype.setOptions = function(options) {};

/**
 * @param {google.maps.MVCArray.<google.maps.LatLng>|Array.<google.maps.LatLng>} path
 * @return {undefined}
 */
google.maps.Polygon.prototype.setPath = function(path) {};

/**
 * @param {google.maps.MVCArray.<google.maps.MVCArray.<google.maps.LatLng>>|google.maps.MVCArray.<google.maps.LatLng>|Array.<Array.<google.maps.LatLng>>|Array.<google.maps.LatLng>} paths
 * @return {undefined}
 */
google.maps.Polygon.prototype.setPaths = function(paths) {};

/**
 * @constructor
 */
google.maps.PolygonOptions = function() {};

/**
 * @type {boolean}
 */
google.maps.PolygonOptions.prototype.clickable;

/**
 * @type {string}
 */
google.maps.PolygonOptions.prototype.fillColor;

/**
 * @type {number}
 */
google.maps.PolygonOptions.prototype.fillOpacity;

/**
 * @type {boolean}
 */
google.maps.PolygonOptions.prototype.geodesic;

/**
 * @type {google.maps.Map}
 */
google.maps.PolygonOptions.prototype.map;

/**
 * @type {google.maps.MVCArray.<google.maps.MVCArray.<google.maps.LatLng>>|google.maps.MVCArray.<google.maps.LatLng>|Array.<Array.<google.maps.LatLng>>|Array.<google.maps.LatLng>}
 */
google.maps.PolygonOptions.prototype.paths;

/**
 * @type {string}
 */
google.maps.PolygonOptions.prototype.strokeColor;

/**
 * @type {number}
 */
google.maps.PolygonOptions.prototype.strokeOpacity;

/**
 * @type {number}
 */
google.maps.PolygonOptions.prototype.strokeWeight;

/**
 * @type {number}
 */
google.maps.PolygonOptions.prototype.zIndex;

/**
 * @constructor
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.Rectangle = function(opt_opts) {};

/**
 * @nosideeffects
 * @return {google.maps.LatLngBounds}
 */
google.maps.Rectangle.prototype.getBounds = function() {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.Rectangle.prototype.getMap = function() {};

/**
 * @param {google.maps.LatLngBounds} bounds
 * @return {undefined}
 */
google.maps.Rectangle.prototype.setBounds = function(bounds) {};

/**
 * @param {google.maps.Map} map
 * @return {undefined}
 */
google.maps.Rectangle.prototype.setMap = function(map) {};

/**
 * @param {Object.<string, *>} options
 * @return {undefined}
 */
google.maps.Rectangle.prototype.setOptions = function(options) {};

/**
 * @constructor
 */
google.maps.RectangleOptions = function() {};

/**
 * @type {google.maps.LatLngBounds}
 */
google.maps.RectangleOptions.prototype.bounds;

/**
 * @type {boolean}
 */
google.maps.RectangleOptions.prototype.clickable;

/**
 * @type {string}
 */
google.maps.RectangleOptions.prototype.fillColor;

/**
 * @type {number}
 */
google.maps.RectangleOptions.prototype.fillOpacity;

/**
 * @type {google.maps.Map}
 */
google.maps.RectangleOptions.prototype.map;

/**
 * @type {string}
 */
google.maps.RectangleOptions.prototype.strokeColor;

/**
 * @type {number}
 */
google.maps.RectangleOptions.prototype.strokeOpacity;

/**
 * @type {number}
 */
google.maps.RectangleOptions.prototype.strokeWeight;

/**
 * @type {number}
 */
google.maps.RectangleOptions.prototype.zIndex;

/**
 * @constructor
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.Circle = function(opt_opts) {};

/**
 * @nosideeffects
 * @return {google.maps.LatLngBounds}
 */
google.maps.Circle.prototype.getBounds = function() {};

/**
 * @nosideeffects
 * @return {google.maps.LatLng}
 */
google.maps.Circle.prototype.getCenter = function() {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.Circle.prototype.getMap = function() {};

/**
 * @nosideeffects
 * @return {number}
 */
google.maps.Circle.prototype.getRadius = function() {};

/**
 * @param {google.maps.LatLng} center
 * @return {undefined}
 */
google.maps.Circle.prototype.setCenter = function(center) {};

/**
 * @param {google.maps.Map} map
 * @return {undefined}
 */
google.maps.Circle.prototype.setMap = function(map) {};

/**
 * @param {Object.<string, *>} options
 * @return {undefined}
 */
google.maps.Circle.prototype.setOptions = function(options) {};

/**
 * @param {number} radius
 * @return {undefined}
 */
google.maps.Circle.prototype.setRadius = function(radius) {};

/**
 * @constructor
 */
google.maps.CircleOptions = function() {};

/**
 * @type {google.maps.LatLng}
 */
google.maps.CircleOptions.prototype.center;

/**
 * @type {boolean}
 */
google.maps.CircleOptions.prototype.clickable;

/**
 * @type {string}
 */
google.maps.CircleOptions.prototype.fillColor;

/**
 * @type {number}
 */
google.maps.CircleOptions.prototype.fillOpacity;

/**
 * @type {google.maps.Map}
 */
google.maps.CircleOptions.prototype.map;

/**
 * @type {number}
 */
google.maps.CircleOptions.prototype.radius;

/**
 * @type {string}
 */
google.maps.CircleOptions.prototype.strokeColor;

/**
 * @type {number}
 */
google.maps.CircleOptions.prototype.strokeOpacity;

/**
 * @type {number}
 */
google.maps.CircleOptions.prototype.strokeWeight;

/**
 * @type {number}
 */
google.maps.CircleOptions.prototype.zIndex;

/**
 * @constructor
 * @param {string} url
 * @param {google.maps.LatLngBounds} bounds
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.GroundOverlay = function(url, bounds, opt_opts) {};

/**
 * @nosideeffects
 * @return {google.maps.LatLngBounds}
 */
google.maps.GroundOverlay.prototype.getBounds = function() {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.GroundOverlay.prototype.getMap = function() {};

/**
 * @nosideeffects
 * @return {string}
 */
google.maps.GroundOverlay.prototype.getUrl = function() {};

/**
 * @param {google.maps.Map} map
 * @return {undefined}
 */
google.maps.GroundOverlay.prototype.setMap = function(map) {};

/**
 * @constructor
 */
google.maps.GroundOverlayOptions = function() {};

/**
 * @type {boolean}
 */
google.maps.GroundOverlayOptions.prototype.clickable;

/**
 * @type {google.maps.Map}
 */
google.maps.GroundOverlayOptions.prototype.map;

/**
 * @constructor
 * @extends {google.maps.MVCObject}
 */
google.maps.OverlayView = function() {};

/**
 * @return {undefined}
 */
google.maps.OverlayView.prototype.draw = function() {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.OverlayView.prototype.getMap = function() {};

/**
 * @nosideeffects
 * @return {google.maps.MapPanes}
 */
google.maps.OverlayView.prototype.getPanes = function() {};

/**
 * @nosideeffects
 * @return {google.maps.MapCanvasProjection}
 */
google.maps.OverlayView.prototype.getProjection = function() {};

/**
 * @return {undefined}
 */
google.maps.OverlayView.prototype.onAdd = function() {};

/**
 * @return {undefined}
 */
google.maps.OverlayView.prototype.onRemove = function() {};

/**
 * @param {google.maps.Map|google.maps.StreetViewPanorama} map
 * @return {undefined}
 */
google.maps.OverlayView.prototype.setMap = function(map) {};

/**
 * @constructor
 */
google.maps.MapPanes = function() {};

/**
 * @type {Node}
 */
google.maps.MapPanes.prototype.floatPane;

/**
 * @type {Node}
 */
google.maps.MapPanes.prototype.floatShadow;

/**
 * @type {Node}
 */
google.maps.MapPanes.prototype.mapPane;

/**
 * @type {Node}
 */
google.maps.MapPanes.prototype.overlayImage;

/**
 * @type {Node}
 */
google.maps.MapPanes.prototype.overlayLayer;

/**
 * @type {Node}
 */
google.maps.MapPanes.prototype.overlayMouseTarget;

/**
 * @type {Node}
 */
google.maps.MapPanes.prototype.overlayShadow;

/**
 * @constructor
 */
google.maps.MapCanvasProjection = function() {};

/**
 * @param {google.maps.Point} pixel
 * @return {google.maps.LatLng}
 */
google.maps.MapCanvasProjection.prototype.fromContainerPixelToLatLng = function(pixel) {};

/**
 * @param {google.maps.Point} pixel
 * @return {google.maps.LatLng}
 */
google.maps.MapCanvasProjection.prototype.fromDivPixelToLatLng = function(pixel) {};

/**
 * @param {google.maps.LatLng} latLng
 * @return {google.maps.Point}
 */
google.maps.MapCanvasProjection.prototype.fromLatLngToContainerPixel = function(latLng) {};

/**
 * @param {google.maps.LatLng} latLng
 * @return {google.maps.Point}
 */
google.maps.MapCanvasProjection.prototype.fromLatLngToDivPixel = function(latLng) {};

/**
 * @nosideeffects
 * @return {number}
 */
google.maps.MapCanvasProjection.prototype.getWorldWidth = function() {};

/**
 * @constructor
 */
google.maps.Geocoder = function() {};

/**
 * @param {(google.maps.GeocoderRequest|Object.<string, *>)} request
 * @param {function(Array.<(google.maps.GeocoderResult|Object.<string, *>)>, google.maps.GeocoderStatus)} callback
 * @return {undefined}
 */
google.maps.Geocoder.prototype.geocode = function(request, callback) {};

/**
 * @constructor
 */
google.maps.GeocoderRequest = function() {};

/**
 * @type {string}
 */
google.maps.GeocoderRequest.prototype.address;

/**
 * @type {google.maps.LatLngBounds}
 */
google.maps.GeocoderRequest.prototype.bounds;

/**
 * @type {string}
 */
google.maps.GeocoderRequest.prototype.language;

/**
 * @type {google.maps.LatLng}
 */
google.maps.GeocoderRequest.prototype.location;

/**
 * @type {string}
 */
google.maps.GeocoderRequest.prototype.region;

/**
 * @constructor
 */
google.maps.GeocoderStatus = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderStatus.ERROR;

/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderStatus.INVALID_REQUEST;

/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderStatus.OK;

/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderStatus.OVER_QUERY_LIMIT;

/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderStatus.REQUEST_DENIED;

/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderStatus.UNKNOWN_ERROR;

/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderStatus.ZERO_RESULTS;

/**
 * @constructor
 */
google.maps.GeocoderResult = function() {};

/**
 * @type {Array.<google.maps.GeocoderAddressComponent>}
 */
google.maps.GeocoderResult.prototype.address_components;

/**
 * @type {google.maps.GeocoderGeometry}
 */
google.maps.GeocoderResult.prototype.geometry;

/**
 * @type {Array.<string>}
 */
google.maps.GeocoderResult.prototype.types;

/**
 * @constructor
 */
google.maps.GeocoderAddressComponent = function() {};

/**
 * @type {string}
 */
google.maps.GeocoderAddressComponent.prototype.long_name;

/**
 * @type {string}
 */
google.maps.GeocoderAddressComponent.prototype.short_name;

/**
 * @type {Array.<string>}
 */
google.maps.GeocoderAddressComponent.prototype.types;

/**
 * @constructor
 */
google.maps.GeocoderGeometry = function() {};

/**
 * @type {google.maps.LatLngBounds}
 */
google.maps.GeocoderGeometry.prototype.bounds;

/**
 * @type {google.maps.LatLng}
 */
google.maps.GeocoderGeometry.prototype.location;

/**
 * @type {google.maps.GeocoderLocationType}
 */
google.maps.GeocoderGeometry.prototype.location_type;

/**
 * @type {google.maps.LatLngBounds}
 */
google.maps.GeocoderGeometry.prototype.viewport;

/**
 * @constructor
 */
google.maps.GeocoderLocationType = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderLocationType.APPROXIMATE;

/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderLocationType.GEOMETRIC_CENTER;

/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderLocationType.RANGE_INTERPOLATED;

/**
 * @type {string|number}
 * @const
 */
google.maps.GeocoderLocationType.ROOFTOP;

/**
 * @constructor
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.DirectionsRenderer = function(opt_opts) {};

/**
 * @nosideeffects
 * @return {(google.maps.DirectionsResult|Object.<string, *>)}
 */
google.maps.DirectionsRenderer.prototype.getDirections = function() {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.DirectionsRenderer.prototype.getMap = function() {};

/**
 * @nosideeffects
 * @return {Node}
 */
google.maps.DirectionsRenderer.prototype.getPanel = function() {};

/**
 * @nosideeffects
 * @return {number}
 */
google.maps.DirectionsRenderer.prototype.getRouteIndex = function() {};

/**
 * @param {(google.maps.DirectionsResult|Object.<string, *>)} directions
 * @return {undefined}
 */
google.maps.DirectionsRenderer.prototype.setDirections = function(directions) {};

/**
 * @param {google.maps.Map} map
 * @return {undefined}
 */
google.maps.DirectionsRenderer.prototype.setMap = function(map) {};

/**
 * @param {Object.<string, *>} options
 * @return {undefined}
 */
google.maps.DirectionsRenderer.prototype.setOptions = function(options) {};

/**
 * @param {Node} panel
 * @return {undefined}
 */
google.maps.DirectionsRenderer.prototype.setPanel = function(panel) {};

/**
 * @param {number} routeIndex
 * @return {undefined}
 */
google.maps.DirectionsRenderer.prototype.setRouteIndex = function(routeIndex) {};

/**
 * @constructor
 */
google.maps.DirectionsRendererOptions = function() {};

/**
 * @type {(google.maps.DirectionsResult|Object.<string, *>)}
 */
google.maps.DirectionsRendererOptions.prototype.directions;

/**
 * @type {boolean}
 */
google.maps.DirectionsRendererOptions.prototype.draggable;

/**
 * @type {boolean}
 */
google.maps.DirectionsRendererOptions.prototype.hideRouteList;

/**
 * @type {google.maps.InfoWindow}
 */
google.maps.DirectionsRendererOptions.prototype.infoWindow;

/**
 * @type {google.maps.Map}
 */
google.maps.DirectionsRendererOptions.prototype.map;

/**
 * @type {Object.<string, *>}
 */
google.maps.DirectionsRendererOptions.prototype.markerOptions;

/**
 * @type {Node}
 */
google.maps.DirectionsRendererOptions.prototype.panel;

/**
 * @type {Object.<string, *>}
 */
google.maps.DirectionsRendererOptions.prototype.polylineOptions;

/**
 * @type {boolean}
 */
google.maps.DirectionsRendererOptions.prototype.preserveViewport;

/**
 * @type {number}
 */
google.maps.DirectionsRendererOptions.prototype.routeIndex;

/**
 * @type {boolean}
 */
google.maps.DirectionsRendererOptions.prototype.suppressBicyclingLayer;

/**
 * @type {boolean}
 */
google.maps.DirectionsRendererOptions.prototype.suppressInfoWindows;

/**
 * @type {boolean}
 */
google.maps.DirectionsRendererOptions.prototype.suppressMarkers;

/**
 * @type {boolean}
 */
google.maps.DirectionsRendererOptions.prototype.suppressPolylines;

/**
 * @constructor
 */
google.maps.DirectionsService = function() {};

/**
 * @param {(google.maps.DirectionsRequest|Object.<string, *>)} request
 * @param {function((google.maps.DirectionsResult|Object.<string, *>), google.maps.DirectionsStatus)} callback
 * @return {undefined}
 */
google.maps.DirectionsService.prototype.route = function(request, callback) {};

/**
 * @constructor
 */
google.maps.DirectionsRequest = function() {};

/**
 * @type {boolean}
 */
google.maps.DirectionsRequest.prototype.avoidHighways;

/**
 * @type {boolean}
 */
google.maps.DirectionsRequest.prototype.avoidTolls;

/**
 * @type {google.maps.LatLng|string}
 */
google.maps.DirectionsRequest.prototype.destination;

/**
 * @type {boolean}
 */
google.maps.DirectionsRequest.prototype.optimizeWaypoints;

/**
 * @type {google.maps.LatLng|string}
 */
google.maps.DirectionsRequest.prototype.origin;

/**
 * @type {boolean}
 */
google.maps.DirectionsRequest.prototype.provideRouteAlternatives;

/**
 * @type {string}
 */
google.maps.DirectionsRequest.prototype.region;

/**
 * @type {google.maps.DirectionsTravelMode}
 */
google.maps.DirectionsRequest.prototype.travelMode;

/**
 * @type {google.maps.DirectionsUnitSystem}
 */
google.maps.DirectionsRequest.prototype.unitSystem;

/**
 * @type {Array.<google.maps.DirectionsWaypoint>}
 */
google.maps.DirectionsRequest.prototype.waypoints;

/**
 * @constructor
 */
google.maps.DirectionsTravelMode = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsTravelMode.BICYCLING;

/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsTravelMode.DRIVING;

/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsTravelMode.WALKING;

/**
 * @constructor
 */
google.maps.DirectionsUnitSystem = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsUnitSystem.IMPERIAL;

/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsUnitSystem.METRIC;

/**
 * @constructor
 */
google.maps.DirectionsWaypoint = function() {};

/**
 * @type {google.maps.LatLng|string}
 */
google.maps.DirectionsWaypoint.prototype.location;

/**
 * @type {boolean}
 */
google.maps.DirectionsWaypoint.prototype.stopover;

/**
 * @constructor
 */
google.maps.DirectionsStatus = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsStatus.INVALID_REQUEST;

/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsStatus.MAX_WAYPOINTS_EXCEEDED;

/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsStatus.NOT_FOUND;

/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsStatus.OK;

/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsStatus.OVER_QUERY_LIMIT;

/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsStatus.REQUEST_DENIED;

/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsStatus.UNKNOWN_ERROR;

/**
 * @type {string|number}
 * @const
 */
google.maps.DirectionsStatus.ZERO_RESULTS;

/**
 * @constructor
 */
google.maps.DirectionsResult = function() {};

/**
 * @type {Array.<google.maps.DirectionsRoute>}
 */
google.maps.DirectionsResult.prototype.routes;

/**
 * @constructor
 */
google.maps.DirectionsRoute = function() {};

/**
 * @type {google.maps.LatLngBounds}
 */
google.maps.DirectionsRoute.prototype.bounds;

/**
 * @type {string}
 */
google.maps.DirectionsRoute.prototype.copyrights;

/**
 * @type {Array.<google.maps.DirectionsLeg>}
 */
google.maps.DirectionsRoute.prototype.legs;

/**
 * @type {Array.<google.maps.LatLng>}
 */
google.maps.DirectionsRoute.prototype.overview_path;

/**
 * @type {Array.<string>}
 */
google.maps.DirectionsRoute.prototype.warnings;

/**
 * @type {Array.<number>}
 */
google.maps.DirectionsRoute.prototype.waypoint_order;

/**
 * @constructor
 */
google.maps.DirectionsLeg = function() {};

/**
 * @type {google.maps.DirectionsDistance}
 */
google.maps.DirectionsLeg.prototype.distance;

/**
 * @type {google.maps.DirectionsDuration}
 */
google.maps.DirectionsLeg.prototype.duration;

/**
 * @type {string}
 */
google.maps.DirectionsLeg.prototype.end_address;

/**
 * @type {google.maps.LatLng}
 */
google.maps.DirectionsLeg.prototype.end_location;

/**
 * @type {string}
 */
google.maps.DirectionsLeg.prototype.start_address;

/**
 * @type {google.maps.LatLng}
 */
google.maps.DirectionsLeg.prototype.start_location;

/**
 * @type {Array.<google.maps.DirectionsStep>}
 */
google.maps.DirectionsLeg.prototype.steps;

/**
 * @constructor
 */
google.maps.DirectionsStep = function() {};

/**
 * @type {google.maps.DirectionsDistance}
 */
google.maps.DirectionsStep.prototype.distance;

/**
 * @type {google.maps.DirectionsDuration}
 */
google.maps.DirectionsStep.prototype.duration;

/**
 * @type {google.maps.LatLng}
 */
google.maps.DirectionsStep.prototype.end_location;

/**
 * @type {string}
 */
google.maps.DirectionsStep.prototype.instructions;

/**
 * @type {Array.<google.maps.LatLng>}
 */
google.maps.DirectionsStep.prototype.path;

/**
 * @type {google.maps.LatLng}
 */
google.maps.DirectionsStep.prototype.start_location;

/**
 * @type {google.maps.DirectionsTravelMode}
 */
google.maps.DirectionsStep.prototype.travel_mode;

/**
 * @constructor
 */
google.maps.DirectionsDistance = function() {};

/**
 * @type {string}
 */
google.maps.DirectionsDistance.prototype.text;

/**
 * @type {number}
 */
google.maps.DirectionsDistance.prototype.value;

/**
 * @constructor
 */
google.maps.DirectionsDuration = function() {};

/**
 * @type {string}
 */
google.maps.DirectionsDuration.prototype.text;

/**
 * @type {number}
 */
google.maps.DirectionsDuration.prototype.value;

/**
 * @constructor
 */
google.maps.ElevationService = function() {};

/**
 * @param {(google.maps.PathElevationRequest|Object.<string, *>)} request
 * @param {function(Array.<(google.maps.ElevationResult|Object.<string, *>)>, google.maps.ElevationStatus)} callback
 * @return {undefined}
 */
google.maps.ElevationService.prototype.getElevationAlongPath = function(request, callback) {};

/**
 * @param {(google.maps.LocationElevationRequest|Object.<string, *>)} request
 * @param {function(Array.<(google.maps.ElevationResult|Object.<string, *>)>, google.maps.ElevationStatus)} callback
 * @return {undefined}
 */
google.maps.ElevationService.prototype.getElevationForLocations = function(request, callback) {};

/**
 * @constructor
 */
google.maps.LocationElevationRequest = function() {};

/**
 * @type {Array.<google.maps.LatLng>}
 */
google.maps.LocationElevationRequest.prototype.locations;

/**
 * @constructor
 */
google.maps.PathElevationRequest = function() {};

/**
 * @type {Array.<google.maps.LatLng>}
 */
google.maps.PathElevationRequest.prototype.path;

/**
 * @type {number}
 */
google.maps.PathElevationRequest.prototype.samples;

/**
 * @constructor
 */
google.maps.ElevationResult = function() {};

/**
 * @type {number}
 */
google.maps.ElevationResult.prototype.elevation;

/**
 * @type {google.maps.LatLng}
 */
google.maps.ElevationResult.prototype.location;

/**
 * @constructor
 */
google.maps.ElevationStatus = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.ElevationStatus.INVALID_REQUEST;

/**
 * @type {string|number}
 * @const
 */
google.maps.ElevationStatus.OK;

/**
 * @type {string|number}
 * @const
 */
google.maps.ElevationStatus.OVER_QUERY_LIMIT;

/**
 * @type {string|number}
 * @const
 */
google.maps.ElevationStatus.REQUEST_DENIED;

/**
 * @type {string|number}
 * @const
 */
google.maps.ElevationStatus.UNKNOWN_ERROR;

/**
 * @constructor
 */
google.maps.MapType = function() {};

/**
 * @param {google.maps.Point} tileCoord
 * @param {number} zoom
 * @param {Node} ownerDocument
 * @return {Node}
 */
google.maps.MapType.prototype.getTile = function(tileCoord, zoom, ownerDocument) {};

/**
 * @param {Node} tile
 * @return {undefined}
 */
google.maps.MapType.prototype.releaseTile = function(tile) {};

/**
 * @type {string}
 */
google.maps.MapType.prototype.alt;

/**
 * @type {number}
 */
google.maps.MapType.prototype.maxZoom;

/**
 * @type {number}
 */
google.maps.MapType.prototype.minZoom;

/**
 * @type {string}
 */
google.maps.MapType.prototype.name;

/**
 * @type {google.maps.Projection}
 */
google.maps.MapType.prototype.projection;

/**
 * @type {number}
 */
google.maps.MapType.prototype.radius;

/**
 * @type {google.maps.Size}
 */
google.maps.MapType.prototype.tileSize;

/**
 * @constructor
 * @extends {google.maps.MVCObject}
 */
google.maps.MapTypeRegistry = function() {};

/**
 * @param {string} id
 * @param {google.maps.MapType} mapType
 * @return {undefined}
 */
google.maps.MapTypeRegistry.prototype.set = function(id, mapType) {};

/**
 * @constructor
 */
google.maps.Projection = function() {};

/**
 * @param {google.maps.LatLng} latLng
 * @param {google.maps.Point=} point
 * @return {google.maps.Point}
 */
google.maps.Projection.prototype.fromLatLngToPoint = function(latLng, point) {};

/**
 * @param {google.maps.Point} pixel
 * @param {boolean=} nowrap
 * @return {google.maps.LatLng}
 */
google.maps.Projection.prototype.fromPointToLatLng = function(pixel, nowrap) {};

/**
 * @constructor
 * @param {Object.<string, *>} opt_opts
 */
google.maps.ImageMapType = function(opt_opts) {};

/**
 * @constructor
 */
google.maps.ImageMapTypeOptions = function() {};

/**
 * @type {string}
 */
google.maps.ImageMapTypeOptions.prototype.alt;

/**
 * @type {function(google.maps.Point, number)}
 */
google.maps.ImageMapTypeOptions.prototype.getTileUrl;

/**
 * @type {boolean}
 */
google.maps.ImageMapTypeOptions.prototype.isPng;

/**
 * @type {number}
 */
google.maps.ImageMapTypeOptions.prototype.maxZoom;

/**
 * @type {number}
 */
google.maps.ImageMapTypeOptions.prototype.minZoom;

/**
 * @type {string}
 */
google.maps.ImageMapTypeOptions.prototype.name;

/**
 * @type {number}
 */
google.maps.ImageMapTypeOptions.prototype.opacity;

/**
 * @type {google.maps.Size}
 */
google.maps.ImageMapTypeOptions.prototype.tileSize;

/**
 * @constructor
 * @param {Array.<google.maps.MapTypeStyle>} style
 * @param {Object.<string, *>=} options
 * @extends {google.maps.MVCObject}
 */
google.maps.StyledMapType = function(style, options) {};

/**
 * @constructor
 */
google.maps.StyledMapTypeOptions = function() {};

/**
 * @type {string}
 */
google.maps.StyledMapTypeOptions.prototype.alt;

/**
 * @type {google.maps.StyledMapType}
 */
google.maps.StyledMapTypeOptions.prototype.baseMapType;

/**
 * @type {number}
 */
google.maps.StyledMapTypeOptions.prototype.maxZoom;

/**
 * @type {number}
 */
google.maps.StyledMapTypeOptions.prototype.minZoom;

/**
 * @type {string}
 */
google.maps.StyledMapTypeOptions.prototype.name;

/**
 * @constructor
 */
google.maps.MapTypeStyle = function() {};

/**
 * @type {Array.<google.maps.MapTypeStyler>}
 */
google.maps.MapTypeStyle.prototype.stylers;

/**
 * @constructor
 */
google.maps.MapTypeStyler = function() {};

/**
 * @type {number}
 */
google.maps.MapTypeStyler.prototype.gamma;

/**
 * @type {string}
 */
google.maps.MapTypeStyler.prototype.hue;

/**
 * @type {boolean}
 */
google.maps.MapTypeStyler.prototype.invert_lightness;

/**
 * @type {number}
 */
google.maps.MapTypeStyler.prototype.lightness;

/**
 * @type {number}
 */
google.maps.MapTypeStyler.prototype.saturation;

/**
 * @type {string}
 */
google.maps.MapTypeStyler.prototype.visibility;

/**
 * @constructor
 * @extends {google.maps.MVCObject}
 */
google.maps.BicyclingLayer = function() {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.BicyclingLayer.prototype.getMap = function() {};

/**
 * @param {google.maps.Map} map
 * @return {undefined}
 */
google.maps.BicyclingLayer.prototype.setMap = function(map) {};

/**
 * @constructor
 * @param {string} tableId
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.FusionTablesLayer = function(tableId, opt_opts) {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.FusionTablesLayer.prototype.getMap = function() {};

/**
 * @nosideeffects
 * @return {string}
 */
google.maps.FusionTablesLayer.prototype.getQuery = function() {};

/**
 * @nosideeffects
 * @return {number}
 */
google.maps.FusionTablesLayer.prototype.getTableId = function() {};

/**
 * @param {google.maps.Map} map
 * @return {undefined}
 */
google.maps.FusionTablesLayer.prototype.setMap = function(map) {};

/**
 * @param {Object.<string, *>} options
 * @return {undefined}
 */
google.maps.FusionTablesLayer.prototype.setOptions = function(options) {};

/**
 * @param {string} query
 * @return {undefined}
 */
google.maps.FusionTablesLayer.prototype.setQuery = function(query) {};

/**
 * @param {number} tableId
 * @return {undefined}
 */
google.maps.FusionTablesLayer.prototype.setTableId = function(tableId) {};

/**
 * @constructor
 */
google.maps.FusionTablesLayerOptions = function() {};

/**
 * @type {boolean}
 */
google.maps.FusionTablesLayerOptions.prototype.heatmap;

/**
 * @type {google.maps.Map}
 */
google.maps.FusionTablesLayerOptions.prototype.map;

/**
 * @type {string}
 */
google.maps.FusionTablesLayerOptions.prototype.query;

/**
 * @type {boolean}
 */
google.maps.FusionTablesLayerOptions.prototype.suppressInfoWindows;

/**
 * @constructor
 */
google.maps.FusionTablesMouseEvent = function() {};

/**
 * @type {string}
 */
google.maps.FusionTablesMouseEvent.prototype.infoWindowHtml;

/**
 * @type {google.maps.LatLng}
 */
google.maps.FusionTablesMouseEvent.prototype.latLng;

/**
 * @type {google.maps.Size}
 */
google.maps.FusionTablesMouseEvent.prototype.pixelOffset;

/**
 * @type {Object}
 */
google.maps.FusionTablesMouseEvent.prototype.row;

/**
 * @constructor
 */
google.maps.FusionTablesCell = function() {};

/**
 * @type {string}
 */
google.maps.FusionTablesCell.prototype.columnName;

/**
 * @type {string}
 */
google.maps.FusionTablesCell.prototype.value;

/**
 * @constructor
 * @param {string} url
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.KmlLayer = function(url, opt_opts) {};

/**
 * @nosideeffects
 * @return {google.maps.LatLngBounds}
 */
google.maps.KmlLayer.prototype.getDefaultViewport = function() {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.KmlLayer.prototype.getMap = function() {};

/**
 * @nosideeffects
 * @return {google.maps.KmlLayerMetadata}
 */
google.maps.KmlLayer.prototype.getMetadata = function() {};

/**
 * @nosideeffects
 * @return {string}
 */
google.maps.KmlLayer.prototype.getUrl = function() {};

/**
 * @param {google.maps.Map} map
 * @return {undefined}
 */
google.maps.KmlLayer.prototype.setMap = function(map) {};

/**
 * @constructor
 */
google.maps.KmlLayerOptions = function() {};

/**
 * @type {google.maps.Map}
 */
google.maps.KmlLayerOptions.prototype.map;

/**
 * @type {boolean}
 */
google.maps.KmlLayerOptions.prototype.preserveViewport;

/**
 * @type {boolean}
 */
google.maps.KmlLayerOptions.prototype.suppressInfoWindows;

/**
 * @constructor
 */
google.maps.KmlLayerMetadata = function() {};

/**
 * @type {google.maps.KmlAuthor}
 */
google.maps.KmlLayerMetadata.prototype.author;

/**
 * @type {string}
 */
google.maps.KmlLayerMetadata.prototype.description;

/**
 * @type {string}
 */
google.maps.KmlLayerMetadata.prototype.name;

/**
 * @type {string}
 */
google.maps.KmlLayerMetadata.prototype.snippet;

/**
 * @constructor
 */
google.maps.KmlMouseEvent = function() {};

/**
 * @type {google.maps.KmlFeatureData}
 */
google.maps.KmlMouseEvent.prototype.featureData;

/**
 * @type {google.maps.LatLng}
 */
google.maps.KmlMouseEvent.prototype.latLng;

/**
 * @type {google.maps.Size}
 */
google.maps.KmlMouseEvent.prototype.pixelOffset;

/**
 * @constructor
 */
google.maps.KmlFeatureData = function() {};

/**
 * @type {google.maps.KmlAuthor}
 */
google.maps.KmlFeatureData.prototype.author;

/**
 * @type {string}
 */
google.maps.KmlFeatureData.prototype.description;

/**
 * @type {string}
 */
google.maps.KmlFeatureData.prototype.id;

/**
 * @type {string}
 */
google.maps.KmlFeatureData.prototype.infoWindowHtml;

/**
 * @type {string}
 */
google.maps.KmlFeatureData.prototype.name;

/**
 * @type {string}
 */
google.maps.KmlFeatureData.prototype.snippet;

/**
 * @constructor
 */
google.maps.KmlAuthor = function() {};

/**
 * @type {string}
 */
google.maps.KmlAuthor.prototype.email;

/**
 * @type {string}
 */
google.maps.KmlAuthor.prototype.name;

/**
 * @type {string}
 */
google.maps.KmlAuthor.prototype.uri;

/**
 * @constructor
 * @extends {google.maps.MVCObject}
 */
google.maps.TrafficLayer = function() {};

/**
 * @nosideeffects
 * @return {google.maps.Map}
 */
google.maps.TrafficLayer.prototype.getMap = function() {};

/**
 * @param {google.maps.Map} map
 * @return {undefined}
 */
google.maps.TrafficLayer.prototype.setMap = function(map) {};

/**
 * @constructor
 * @param {Node} container
 * @param {Object.<string, *>=} opt_opts
 * @extends {google.maps.MVCObject}
 */
google.maps.StreetViewPanorama = function(container, opt_opts) {};

/**
 * @nosideeffects
 * @return {Array.<google.maps.StreetViewLink>}
 */
google.maps.StreetViewPanorama.prototype.getLinks = function() {};

/**
 * @nosideeffects
 * @return {string}
 */
google.maps.StreetViewPanorama.prototype.getPano = function() {};

/**
 * @nosideeffects
 * @return {google.maps.LatLng}
 */
google.maps.StreetViewPanorama.prototype.getPosition = function() {};

/**
 * @nosideeffects
 * @return {google.maps.StreetViewPov}
 */
google.maps.StreetViewPanorama.prototype.getPov = function() {};

/**
 * @nosideeffects
 * @return {boolean}
 */
google.maps.StreetViewPanorama.prototype.getVisible = function() {};

/**
 * @param {function(string)} provider
 * @return {undefined}
 */
google.maps.StreetViewPanorama.prototype.registerPanoProvider = function(provider) {};

/**
 * @param {string} pano
 * @return {undefined}
 */
google.maps.StreetViewPanorama.prototype.setPano = function(pano) {};

/**
 * @param {google.maps.LatLng} latLng
 * @return {undefined}
 */
google.maps.StreetViewPanorama.prototype.setPosition = function(latLng) {};

/**
 * @param {google.maps.StreetViewPov} pov
 * @return {undefined}
 */
google.maps.StreetViewPanorama.prototype.setPov = function(pov) {};

/**
 * @param {boolean} flag
 * @return {undefined}
 */
google.maps.StreetViewPanorama.prototype.setVisible = function(flag) {};

/**
 * @type {Array.<google.maps.MVCArray.<Node>>}
 */
google.maps.StreetViewPanorama.prototype.controls;

/**
 * @constructor
 */
google.maps.StreetViewPanoramaOptions = function() {};

/**
 * @type {boolean}
 */
google.maps.StreetViewPanoramaOptions.prototype.addressControl;

/**
 * @type {Object.<string, *>}
 */
google.maps.StreetViewPanoramaOptions.prototype.addressControlOptions;

/**
 * @type {boolean}
 */
google.maps.StreetViewPanoramaOptions.prototype.disableDoubleClickZoom;

/**
 * @type {boolean}
 */
google.maps.StreetViewPanoramaOptions.prototype.enableCloseButton;

/**
 * @type {boolean}
 */
google.maps.StreetViewPanoramaOptions.prototype.linksControl;

/**
 * @type {boolean}
 */
google.maps.StreetViewPanoramaOptions.prototype.navigationControl;

/**
 * @type {Object.<string, *>}
 */
google.maps.StreetViewPanoramaOptions.prototype.navigationControlOptions;

/**
 * @type {string}
 */
google.maps.StreetViewPanoramaOptions.prototype.pano;

/**
 * @type {function(string)}
 */
google.maps.StreetViewPanoramaOptions.prototype.panoProvider;

/**
 * @type {google.maps.LatLng}
 */
google.maps.StreetViewPanoramaOptions.prototype.position;

/**
 * @type {google.maps.StreetViewPov}
 */
google.maps.StreetViewPanoramaOptions.prototype.pov;

/**
 * @type {boolean}
 */
google.maps.StreetViewPanoramaOptions.prototype.scrollwheel;

/**
 * @type {boolean}
 */
google.maps.StreetViewPanoramaOptions.prototype.visible;

/**
 * @constructor
 */
google.maps.StreetViewAddressControlOptions = function() {};

/**
 * @type {google.maps.ControlPosition}
 */
google.maps.StreetViewAddressControlOptions.prototype.position;

/**
 * @type {Object.<string>}
 */
google.maps.StreetViewAddressControlOptions.prototype.style;

/**
 * @constructor
 */
google.maps.StreetViewLink = function() {};

/**
 * @type {string}
 */
google.maps.StreetViewLink.prototype.description;

/**
 * @type {number}
 */
google.maps.StreetViewLink.prototype.heading;

/**
 * @type {string}
 */
google.maps.StreetViewLink.prototype.pano;

/**
 * @type {string}
 */
google.maps.StreetViewLink.prototype.roadColor;

/**
 * @type {number}
 */
google.maps.StreetViewLink.prototype.roadOpacity;

/**
 * @constructor
 */
google.maps.StreetViewPov = function() {};

/**
 * @type {number}
 */
google.maps.StreetViewPov.prototype.heading;

/**
 * @type {number}
 */
google.maps.StreetViewPov.prototype.pitch;

/**
 * @type {number}
 */
google.maps.StreetViewPov.prototype.zoom;

/**
 * @constructor
 */
google.maps.StreetViewPanoramaData = function() {};

/**
 * @type {string}
 */
google.maps.StreetViewPanoramaData.prototype.copyright;

/**
 * @type {Array.<google.maps.StreetViewLink>}
 */
google.maps.StreetViewPanoramaData.prototype.links;

/**
 * @type {google.maps.StreetViewLocation}
 */
google.maps.StreetViewPanoramaData.prototype.location;

/**
 * @type {google.maps.StreetViewTileData}
 */
google.maps.StreetViewPanoramaData.prototype.tiles;

/**
 * @constructor
 */
google.maps.StreetViewLocation = function() {};

/**
 * @type {string}
 */
google.maps.StreetViewLocation.prototype.description;

/**
 * @type {google.maps.LatLng}
 */
google.maps.StreetViewLocation.prototype.latLng;

/**
 * @type {string}
 */
google.maps.StreetViewLocation.prototype.pano;

/**
 * @constructor
 */
google.maps.StreetViewTileData = function() {};

/**
 * @param {string} pano
 * @param {number} tileZoom
 * @param {number} tileX
 * @param {number} tileY
 * @return {string}
 */
google.maps.StreetViewTileData.prototype.getTileUrl = function(pano, tileZoom, tileX, tileY) {};

/**
 * @type {number}
 */
google.maps.StreetViewTileData.prototype.centerHeading;

/**
 * @type {google.maps.Size}
 */
google.maps.StreetViewTileData.prototype.tileSize;

/**
 * @type {google.maps.Size}
 */
google.maps.StreetViewTileData.prototype.worldSize;

/**
 * @constructor
 */
google.maps.StreetViewService = function() {};
/**
 * @param {string} pano
 * @param {function(google.maps.StreetViewPanoramaData, google.maps.StreetViewStatus)} callback
 * @return {undefined}
 */
google.maps.StreetViewService.prototype.getPanoramaById = function(pano, callback) {};

/**
 * @param {google.maps.LatLng} latlng
 * @param {number} radius
 * @param {function(google.maps.StreetViewPanoramaData, google.maps.StreetViewStatus)} callback
 * @return {undefined}
 */
google.maps.StreetViewService.prototype.getPanoramaByLocation = function(latlng, radius, callback) {};

/**
 * @constructor
 */
google.maps.StreetViewStatus = function() {};
/**
 * @type {string|number}
 * @const
 */
google.maps.StreetViewStatus.OK;

/**
 * @type {string|number}
 * @const
 */
google.maps.StreetViewStatus.UNKNOWN_ERROR;

/**
 * @type {string|number}
 * @const
 */
google.maps.StreetViewStatus.ZERO_RESULTS;

/**
 * @constructor
 */
google.maps.MapsEventListener = function() {};

google.maps.event = function() {};

/**
 * @param {Object} instance
 * @param {string} eventName
 * @param {function(...[*])} handler
 * @param {boolean=} capture
 * @return {google.maps.MapsEventListener}
 */
google.maps.event.addDomListener = function(instance, eventName, handler, capture) {};

/**
 * @param {Object} instance
 * @param {string} eventName
 * @param {function(...[*])} handler
 * @param {boolean=} capture
 * @return {google.maps.MapsEventListener}
 */
google.maps.event.addDomListenerOnce = function(instance, eventName, handler, capture) {};

/**
 * @param {Object} instance
 * @param {string} eventName
 * @param {function(...[*])} handler
 * @return {google.maps.MapsEventListener}
 */
google.maps.event.addListener = function(instance, eventName, handler) {};

/**
 * @param {Object} instance
 * @param {string} eventName
 * @param {function(...[*])} handler
 * @return {google.maps.MapsEventListener}
 */
google.maps.event.addListenerOnce = function(instance, eventName, handler) {};

/**
 * @param {Object} instance
 * @return {undefined}
 */
google.maps.event.clearInstanceListeners = function(instance) {};

/**
 * @param {Object} instance
 * @param {string} eventName
 * @return {undefined}
 */
google.maps.event.clearListeners = function(instance, eventName) {};

/**
 * @param {google.maps.MapsEventListener} listener
 * @return {undefined}
 */
google.maps.event.removeListener = function(listener) {};

/**
 * @param {Object} instance
 * @param {string} eventName
 * @param {...*} var_args
 * @return {undefined}
 */
google.maps.event.trigger = function(instance, eventName, var_args) {};

/**
 * @constructor
 */
google.maps.MouseEvent = function() {};

/**
 * @type {google.maps.LatLng}
 */
google.maps.MouseEvent.prototype.latLng;

/**
 * @constructor
 * @param {number} lat
 * @param {number} lng
 * @param {boolean=} noWrap
 */
google.maps.LatLng = function(lat, lng, noWrap) {};

/**
 * @param {google.maps.LatLng} other
 * @return {boolean}
 */
google.maps.LatLng.prototype.equals = function(other) {};

/**
 * @return {number}
 */
google.maps.LatLng.prototype.lat = function() {};

/**
 * @return {number}
 */
google.maps.LatLng.prototype.lng = function() {};

/**
 * @return {string}
 */
google.maps.LatLng.prototype.toString = function() {};

/**
 * @param {number=} precision
 * @return {string}
 */
google.maps.LatLng.prototype.toUrlValue = function(precision) {};

/**
 * @constructor
 * @param {google.maps.LatLng=} sw
 * @param {google.maps.LatLng=} ne
 */
google.maps.LatLngBounds = function(sw, ne) {};

/**
 * @param {google.maps.LatLng} latLng
 * @return {boolean}
 */
google.maps.LatLngBounds.prototype.contains = function(latLng) {};

/**
 * @param {google.maps.LatLngBounds} other
 * @return {boolean}
 */
google.maps.LatLngBounds.prototype.equals = function(other) {};

/**
 * @param {google.maps.LatLng} point
 * @return {google.maps.LatLngBounds}
 */
google.maps.LatLngBounds.prototype.extend = function(point) {};

/**
 * @nosideeffects
 * @return {google.maps.LatLng}
 */
google.maps.LatLngBounds.prototype.getCenter = function() {};

/**
 * @nosideeffects
 * @return {google.maps.LatLng}
 */
google.maps.LatLngBounds.prototype.getNorthEast = function() {};

/**
 * @nosideeffects
 * @return {google.maps.LatLng}
 */
google.maps.LatLngBounds.prototype.getSouthWest = function() {};

/**
 * @param {google.maps.LatLngBounds} other
 * @return {boolean}
 */
google.maps.LatLngBounds.prototype.intersects = function(other) {};

/**
 * @return {boolean}
 */
google.maps.LatLngBounds.prototype.isEmpty = function() {};

/**
 * @return {google.maps.LatLng}
 */
google.maps.LatLngBounds.prototype.toSpan = function() {};

/**
 * @return {string}
 */
google.maps.LatLngBounds.prototype.toString = function() {};

/**
 * @param {number=} precision
 * @return {string}
 */
google.maps.LatLngBounds.prototype.toUrlValue = function(precision) {};

/**
 * @param {google.maps.LatLngBounds} other
 * @return {google.maps.LatLngBounds}
 */
google.maps.LatLngBounds.prototype.union = function(other) {};

/**
 * @constructor
 * @param {number} x
 * @param {number} y
 */
google.maps.Point = function(x, y) {};

/**
 * @param {google.maps.Point} other
 * @return {boolean}
 */
google.maps.Point.prototype.equals = function(other) {};

/**
 * @return {string}
 */
google.maps.Point.prototype.toString = function() {};

/**
 * @type {number}
 */
google.maps.Point.prototype.x;

/**
 * @type {number}
 */
google.maps.Point.prototype.y;

/**
 * @constructor
 * @param {number} width
 * @param {number} height
 * @param {string=} widthUnit
 * @param {string=} heightUnit
 */
google.maps.Size = function(width, height, widthUnit, heightUnit) {};

/**
 * @param {google.maps.Size} other
 * @return {boolean}
 */
google.maps.Size.prototype.equals = function(other) {};

/**
 * @return {string}
 */
google.maps.Size.prototype.toString = function() {};

/**
 * @type {number}
 */
google.maps.Size.prototype.height;

/**
 * @type {number}
 */
google.maps.Size.prototype.width;

/**
 * @constructor
 */
google.maps.MVCObject = function() {};

/**
 * @param {string} key
 * @param {google.maps.MVCObject} target
 * @param {string=} targetKey
 * @param {boolean=} noNotify
 * @return {undefined}
 */
google.maps.MVCObject.prototype.bindTo = function(key, target, targetKey, noNotify) {};

/**
 * @param {string} key
 * @return {undefined}
 */
google.maps.MVCObject.prototype.changed = function(key) {};

/**
 * @param {string} key
 * @return {*}
 */
google.maps.MVCObject.prototype.get = function(key) {};

/**
 * @param {string} key
 * @return {undefined}
 */
google.maps.MVCObject.prototype.notify = function(key) {};

/**
 * @param {string} key
 * @param {*} value
 * @return {undefined}
 */
google.maps.MVCObject.prototype.set = function(key, value) {};

/**
 * @param {Object|undefined} values
 * @return {undefined}
 */
google.maps.MVCObject.prototype.setValues = function(values) {};

/**
 * @param {string} key
 * @return {undefined}
 */
google.maps.MVCObject.prototype.unbind = function(key) {};

/**
 * @return {undefined}
 */
google.maps.MVCObject.prototype.unbindAll = function() {};

/**
 * @constructor
 * @param {Array=} array
 * @extends {google.maps.MVCObject}
 */
google.maps.MVCArray = function(array) {};

/**
 * @param {function(*, number)} callback
 * @return {undefined}
 */
google.maps.MVCArray.prototype.forEach = function(callback) {};

/**
 * @nosideeffects
 * @return {Array}
 */
google.maps.MVCArray.prototype.getArray = function() {};

/**
 * @param {number} i
 * @return {*}
 */
google.maps.MVCArray.prototype.getAt = function(i) {};

/**
 * @nosideeffects
 * @return {number}
 */
google.maps.MVCArray.prototype.getLength = function() {};

/**
 * @param {number} i
 * @param {*} elem
 * @return {undefined}
 */
google.maps.MVCArray.prototype.insertAt = function(i, elem) {};

/**
 * @return {*}
 */
google.maps.MVCArray.prototype.pop = function() {};

/**
 * @param {*} elem
 * @return {number}
 */
google.maps.MVCArray.prototype.push = function(elem) {};

/**
 * @param {number} i
 * @return {*}
 */
google.maps.MVCArray.prototype.removeAt = function(i) {};

/**
 * @param {number} i
 * @param {*} elem
 * @return {undefined}
 */
google.maps.MVCArray.prototype.setAt = function(i, elem) {};
