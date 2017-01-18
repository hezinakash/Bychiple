var map;
var service;
var geocoder;
var infowindow;
var directionsService;

var userMarker;
var currUserLocation;
var isRoute;
var startRouteDate;
var endRouteDate;
var startMarker;
var endMarker;

var userInterval;
var parkingInterval;
var chipDetailsInterval;
var focusPositionMapInterval; 

var routePoints = [];
var theftPlacesMarkers = [];
var repairPlacesMarkers = [];
var bicyclingPath;

var userMarkerPath = 'css/images/userMarker.png';
var startRouteMarkerPath = 'css/images/start.png';
var finishRouteMarketPath = 'css/images/finish.png';
var theftPlacesMarkerPath = 'css/images/alert_icon.png';
var repairPlacesMarkerPath = 'css/images/repairPlacesMarker.png';

var WATCH_OUT = "Watch Out";
var LOW_LEVEL_BATTERY = "Low Battery";
var THEFT_SUSPICION = "Theft Suspicion";

var USER_MARKER_ZINDEX = 10;
var ROUTE_MARKER_ZINDEX = 6;
var THEFT_MARKER_ZINDEX = 5;
var REPAIR_MARKER_ZINDEX = 3;


//--------------------------------
//-          On Load             -
//--------------------------------

$(loadHomePage);

function loadHomePage()
{
	checkIfUserConnected();
	
	$(function() {
		$('#dl-menu').dlmenu();
	});
	
	$("#repairPlacesCheckBox").bind( "change", function(event, ui) {
		repairPlacesCheckBoxClicked();
	});
	$("#parkingCheckBox").bind( "change", function(event, ui) {
		parkingCheckBoxClicked();
	});
	$("#theftPlacesCheckBox").bind( "change", function(event, ui) {
		theftPlacesCheckBoxClicked();
	});
};

function checkIfUserConnected()
{
	$.ajax({
		url: "Bychiple/connection/userConnectionCheck",
		type: "POST",
		dataType: 'json',
		success: function(ConnectionData){
			if(ConnectionData.isLoggedIn === true)
			{
				initUserDetails();
				initMap();
				isRoute = false;
				
				// add icon the 'back' button in menu
				$(".dl-back a").append('<img id="backImg" src="css/images/back_icon.png">');
			}
			else {
				window.location.href='index.html';
			}
		},
		error: function() {
        }
	});
}

function initUserDetails()
{
	$.ajax({
		url: "Bychiple/userChipsManager/initCurrentChipID",
		type: "POST",
		dataType: 'json',
		success: function(isAdmin)
		{
			// show user on map
			getUserLocation(); 
			
			if (!isAdmin) {
				$("#simulator").hide();
				userInterval = setInterval(getUserLocation, 2000);
			}
			//TODO - check first if there is a location of the user (maybe the chip is offline)
			
			countUnreadMessages();
			
			// show battery of selected chip  every 5 min
			getChipDetails();
			chipDetailsInterval = setInterval(getChipDetails, 30000);
			
			// focus the current position of marker
			focusPositionOnMap();
			focusPositionMapInterval = setInterval(focusPositionOnMap, 30000);
		},
		error: function() {
        }
	});
}

function initMap()
{
    var mapDiv = document.getElementById('map');
    //TODO - get the user coordinates
    var telAviv = new google.maps.LatLng(32.0714053, 34.7847534);
    
    var mapProp = {
        center: telAviv,
        zoom: 15,
        mapTypeId:google.maps.MapTypeId.ROADMAP
    };
      
    map = new google.maps.Map(mapDiv, mapProp);
    google.maps.event.trigger(map, 'resize');
    infowindow = new google.maps.InfoWindow();
	service = new google.maps.places.PlacesService(map);
	geocoder = new google.maps.Geocoder();
	directionsService = new google.maps.DirectionsService();
}

function countUnreadMessages()
{
	$.ajax({
		url: "Bychiple/messageBoxManager/getNumOfUnreadMsg",
		type: "POST",
		dataType: 'json',
		success: function(numOfUnreadMsg){
			$(".badge").empty();
			if (numOfUnreadMsg !== 0){
			  	$(".badge").append(numOfUnreadMsg);
			}
		},
		error: function() {
			console.log("couldn't get num of unread messages");
        }
	});
}

function logoutUserClicked()
{
	$.ajax({
		url: "Bychiple/connection/disconnectUser",
		type: "POST",
		success: function(){
			window.location.href='index.html';
		},
		error: function() {
        }
	});
}

function getChipDetails() 
{
	$.ajax({
	      url: "Bychiple/hc/chipDetails",
	      type: "GET",
	      dataType: 'json',
	      success: function(chipDetailsList) {
	    	  
	    	  var battery = chipDetailsList[0];
	    	  var chipUpdateTimeStr = chipDetailsList[1];
	    	  
	    	  if (battery != null)
	    	  {
	    		  var col;
	    		  $(".fill").css({"width": battery+ "%"});
	    		  $("#lev").text(battery+"%");
	    		  
	    		  if(battery <= 20)
	    		  {
	    			col = "#df0a00";
	    			$(".fill").css({"box-shadow": "0 0 10px #df0a00"});
	    		      
	    		    //Send alert to inbox
	    		    if (battery <= 10)
	    		    {
	    		       if (!isBatteryAlertShown)
	    		       {
	    		    	   var titleMsg = LOW_LEVEL_BATTERY;
	    		    	   addMessageToInbox(titleMsg);
		 	    	       showLowBatteryAlert(titleMsg);
	    		       }    
	    		    }
	    		    else
	    		    	isBatteryAlertShown = false;
	    		   
	    		  }
	    		  else if (battery <= 30)
	    		  {
	    			  	col = "#dbb300";
	    			  	$(".fill").css({"box-shadow": "0 0 10px #dbb300"});
	    			  	isBatteryAlertShown = false;
	    		  }
	    		  else
	    		  {
	    			  col = "#60b939";
	    			  $(".fill").css({"box-shadow":"0 0 10px #60b939"});
	    			  isBatteryAlertShown = false;
	    		  }
	    		  
	    		  $(".fill").css({"background-color":col});
	    	  }
	    	  else
	    	  {
	    		  $("#lev").text("N/A");
	    	  }
	    	  
	    	  $("#status").empty();
	    	  if(chipUpdateTimeStr != null)
	    	  {
	    		  var chipUpdateDate = new Date(chipUpdateTimeStr);
	    		  var now = new Date();    		  
	    		  var diff = now - chipUpdateDate;
	    		   
	    		  if (diff > 150000) // chip not send requests in last 2 minutes
	    		  { 
	    			  $("#status").text("No GPS signal");
	    		  }
	    	  }
	    	  else //chip was not update time in the first time!
	    	  {
    			  $("#status").text("No GPS signal");
	    	  }
	    	 
	      },
	      error: function() {
	          console.log("Failed to check battery value");
	      }
	  });
}

function showLowBatteryAlert(titleMsg) 
{
	isBatteryAlertShown = true;
	bootbox.dialog({
		message: "Please charge your chip battery",
		title: titleMsg,
		buttons: {
			main: {
			    label: "Ok",
			    className: "btn-primary",
			    callback: function() {
			    }
			}
		}
	});
}

function btnSettingsClicked()
{
	window.location.href='Settings.html';
}

function btnSimulatorClicked()
{
	$("#btn-dl-menu").click(); //close menu
	
	if ($("#simulator").attr('value') === "off")
	{
		$("#simulator").attr('value', "on");
		
		 setTimeout(function(){
			 $("#simulator a").text('Stop Simulator');
			 $("#simulator a").find('img').remove();
		     $("#simulator a").append('<img id="stopSimulatorImg" src="css/images/stopSimulator_icon.png">');
		 }, 400);
		 
		getUserLocation();
		userInterval = setInterval(getUserLocation, 2000);
	}
	else {
		$("#simulator").attr('value', "off");
		
		setTimeout(function(){
			$("#simulator a").text('Play Simulator');
			$("#simulator a").find('img').remove();
		    $("#simulator a").append('<img id="playSimulatorImg" src="css/images/playSimulator_icon.png">');
		 }, 400);
		clearInterval(userInterval);
	}
}




//--------------------------------
//-         User Location        -
//--------------------------------

function getUserLocation()
{
    $.ajax({
        url: "Bychiple/mapManager/userCurrLocation",
        type: "POST",
        dataType: 'json',
        success: function(userLocation) {
        	if (userMarker !== undefined) {
        		userMarker.setMap(null);
        	}
        	if (userLocation !== null) {
        		currUserLocation = new google.maps.LatLng(userLocation.lat, userLocation.lng);
        		userMarker = createMarker(currUserLocation, userMarkerPath, USER_MARKER_ZINDEX);
        	    
        	    
        		// if we are in a route state
        		if (isRoute)
        		{
        			if(routePoints.length === 0)
        			{
        				routePoints.push(userLocation.lat);
        				routePoints.push(userLocation.lng);
        			}
        			else
        			{
        				var lastRoutePointLat = routePoints[0];
        				var lastRoutePointLng = routePoints[1];
        			
        				if((lastRoutePointLat !== userLocation.lat) || (lastRoutePointLng !== userLocation.lng))
        				{
        					routePoints.push(userLocation.lat);
        					routePoints.push(userLocation.lng);
        				}
        			}
        		}
        	}
        	else {
        		console.log("user location is null");
        	}
        },
        error: function() {
            console.log("Failed to show user on map");
        }
    });
}

function createMarker(point, iconPath, zindexMarker)
{
    return new google.maps.Marker({
    		map: map,
            position: point,
//            animation: google.maps.Animation.BOUNCE,
            icon: iconPath,
            zIndex: zindexMarker
        });
}






//--------------------------------
//-        Repair Places         -
//--------------------------------

function repairPlacesCheckBoxClicked()
{
	var repairPlacesOn = $("#repairPlacesCheckBox").is(':checked');
	if (repairPlacesOn) {
		showRepairBicyclePlaces();
	}
	else {
		clearMarkers(repairPlacesMarkers);
	}
}

function showRepairBicyclePlaces()
{
	//TODO - if we want to search in varius types, we should get it as a parameter
	$.ajax({
        url: "Bychiple/mapManager/userCurrLocation",
        type: "POST",
        dataType: 'json',
        success: function(userLocation) {
        	var currLocation = new google.maps.LatLng(userLocation.lat, userLocation.lng);
        	var request = {
        			location: currLocation,
        			radius: '2000',
        			types: ['bicycle_store']
        		};

        		service.nearbySearch(request, addResultsToMap);
        },
        error: function() {
            console.log("Failed to show repair bycicle places on map");
        }
    });	
}

function addResultsToMap(results, status) 
{
	if (status == google.maps.places.PlacesServiceStatus.OK) {
	    for (var i = 0; i < results.length; i++) {
	        createRepairPlaceMarker(results[i]);
	    }
	}
}

function createRepairPlaceMarker(place) 
{
    var marker = new google.maps.Marker({
      map: map,
      position: place.geometry.location,
      icon: repairPlacesMarkerPath,
      zIndex: REPAIR_MARKER_ZINDEX	
    });
    
    google.maps.event.addListener(marker, 'click', function()
    {
        service.getDetails({
            placeId: place.place_id
        }, function(place, status) {    	
            if (status === google.maps.places.PlacesServiceStatus.OK) {
            	var phone_number = place.formatted_phone_number;
            	infowindow.setContent('<div><strong>' + place.name + '</strong><br>' +
                		place.vicinity + '<br>' + phone_number + '</div>');
            }
            infowindow.open(map, marker);
        });
    });

    repairPlacesMarkers.push(marker);
}

function clearMarkers(markers)
{
	for (var i = 0; i < markers.length; i++) {
		markers[i].setMap(null);
    }
	
	markers.length = 0;
}






//--------------------------------
//-        Parking Place         -
//--------------------------------

function parkingCheckBoxClicked()
{
	var isParking = $("#parkingCheckBox").is(':checked');
	
	switchParkingMode(isParking);
	if (isParking) {
		parkingInterval = setInterval(checkIfUserLocationChanged, 1000);
	}
	else {
		clearInterval(parkingInterval);
	}
}

function switchParkingMode(isParking)
{
	$.ajax({
      url: "Bychiple/mapManager/saveParkingState",
      type: "POST",
      data: {parking: isParking},
      dataType: 'json',
      success: function(data) {
    	  if (isParking)
    	  {
    		  var theftPlacesList = data[0];
    		  var parkingLocation = data[1];
    		  checkTheftPlacesNearby(theftPlacesList, parkingLocation);
    	  }
      },
      error: function() {
          console.log("Failed to save parking state");
      }
  });
}

function checkTheftPlacesNearby(theftPlacesList, loc)
{
	var nearbyTheftPlace = false;
	var parkingLoc = new google.maps.LatLng(loc.lat, loc.lng);
	
	$.each(theftPlacesList, function(i, theftPlace){
		var theftLoc = new google.maps.LatLng(theftPlace.lat, theftPlace.lng);
		var distanceInMeters = google.maps.geometry.spherical.computeDistanceBetween(parkingLoc, theftLoc);
		
		if (distanceInMeters < 200) {
			nearbyTheftPlace = true;
		}
	});
	
	if (nearbyTheftPlace) {
		var titleMsg = WATCH_OUT;
		addMessageToInbox(titleMsg);
  		showWatchOutAlert(titleMsg);
	}
}

function showWatchOutAlert(titleMsg)
{
	bootbox.dialog({
		message: "We found this place dangerous!",
		title: titleMsg,
		buttons: {
			main: {
			    label: "OK",
			    className: "btn-primary",
			    callback: function() {
			    	//Example.show("Primary button");
			    }
			}
		}
	});
}

function checkIfUserLocationChanged()
{
	$.ajax({
      url: "Bychiple/mapManager/checkParkingState",
      type: "POST",
      dataType: 'json',
      success: function(locList) {
      	// calculate distance in meters between two locations
      	var loc1 = new google.maps.LatLng(locList[0].lat, locList[0].lng);
      	var loc2 = new google.maps.LatLng(locList[1].lat, locList[1].lng);
      	var distanceInMeters = google.maps.geometry.spherical.computeDistanceBetween(loc1, loc2);
      	
      	if (distanceInMeters > 5)
      	{
      		//clear interval because we dont want to keep sending alert messages
      		$("#parkingCheckBox").click();
      		var titleMsg = THEFT_SUSPICION;
      		addMessageToInbox(titleMsg);
      		showTheftAlert(titleMsg);
      		sendSMSAlert(titleMsg);
      		
      	}
      },
      error: function() {
          console.log("Failed to check if user location changed");
      }
  });
}

function showTheftAlert(titleMsg)
{
	bootbox.dialog({
		message: "We found a theft suspicion!",
		title: titleMsg,
		buttons: {
			danger: {
				label: "Real time theft",
				className: "btn-danger",
				callback: function() {
					//Example.show("Go catch them!");
					addTheftPlace();
				}
			},
			success: {
				label: "False alarm",
				className: "btn-success",
				callback: function() {
					//Example.show("You may left the parking button on");
				}
			}
		}
	});
}

function sendSMSAlert(titleMsg)
{
	$.ajax({
	      url: "Bychiple/hc/addMessage",
	      type: "POST",
	      data: {message: titleMsg},
	      dataType: 'json',
	      success: function() {
	      },
	      error: function() {
	          console.log("Failed to send SMS Alert");
	      }
	  });
}






//--------------------------------
//-        Inbox Message         -
//--------------------------------

function addMessageToInbox(titleMsg)
{
	$.ajax({
	      url: "Bychiple/messageBoxManager/addMessageToInbox",
	      type: "POST",
	      data: {msg: titleMsg},
	      dataType: 'json',
	      success: function() {
	    	  var numUnreadMsg = $(".badge").text();
	    	  if (numUnreadMsg === "") {
	    		  $(".badge").append("1");
	    	  }
	    	  else {
	    		  var numUnreadMsgInt = parseInt(numUnreadMsg) + 1;
	    		  $(".badge").empty();
	    		  $(".badge").append(numUnreadMsgInt);
	    	  }
	      },
	      error: function() {
	          console.log("Failed to save message");
	      }
	});
}

function msgBoxClicked()
{
	window.location.href='MessageBox.html'
}






//--------------------------------
//-         Theft Place          -
//--------------------------------

function theftPlacesCheckBoxClicked()
{
	var theftPlacesOn = $("#theftPlacesCheckBox").is(':checked');
	if (theftPlacesOn) {
		showTheftPlaces();
	}
	else {
		clearMarkers(theftPlacesMarkers);
	}
}

function showTheftPlaces()
{
	$.ajax({
	      url: "Bychiple/theftPlacesManager/getTheftPlaces",
	      type: "POST",
	      dataType: 'json',
	      success: function(theftPlacesList) {
	    	  $.each(theftPlacesList, function(i, theftPlace) { 
	    		  createTheftPlaceMarker(theftPlace)
			  });
	      },
	      error: function() {
	          console.log("Failed to create new theft place");
	      }
	  });
}

function createTheftPlaceMarker(theftPlace) 
{
	var location = new google.maps.LatLng(theftPlace.lat, theftPlace.lng);
		
	var marker = createMarker(location, theftPlacesMarkerPath, THEFT_MARKER_ZINDEX); 
    
    google.maps.event.addListener(marker, 'click', function()
    {
    	infowindow.setContent('<div><strong>' + theftPlace.address + '</strong></div>');
    	infowindow.open(map, marker);
    });

    theftPlacesMarkers.push(marker);
}

function addTheftPlace()
{
	$.ajax({
	      url: "Bychiple/mapManager/parkingLocation",
	      type: "POST",
	      dataType: 'json',
	      success: function(parkingLoc) {
	    	  setAddressFromTheftLocation(parkingLoc.lat, parkingLoc.lng);
	      },
	      error: function() {
	          console.log("Failed to add a new theft place");
	      }
	});
}

function setAddressFromTheftLocation(lat, lng)
{
	var parkingLocation = new google.maps.LatLng(lat, lng);
	
	geocoder.geocode({'latLng': parkingLocation}, function(results, status) {
	    if (status === 'OK') 
	    {
	    	var address;
	    	
	    	if (results[0] !== undefined) {
	    		address = results[0].formatted_address;
	    	}
	    	else {
	    		address = "Unknown Address";
	    	}
	    	
	    	createNewTheftPlace(address);
	      
	    } else {
	    	console.log('Geocoder failed due to: ' + status);
	    }
	});
}

function createNewTheftPlace(address)
{
	$.ajax({
	      url: "Bychiple/theftPlacesManager/addTheftPlace",
	      type: "POST",
	      data: {address: address},
	      dataType: 'json',
	      success: function() {
	      },
	      error: function() {
	          console.log("Failed to create new theft place");
	      }
	  });
}






//--------------------------------
//-           Routes             -
//--------------------------------

function btnRouteClicked(btnElem)
{
	$("#btn-dl-menu").click(); //close menu
	
	if(isRoute)     // user clicked on finish
	{
		setTimeout(function(){
			$(btnElem).find('img').remove();
			$(btnElem).text('Start Route');
	        $(btnElem).append('<img id="startRouteImg" src="css/images/startRoute_icon.png">');
	        $("#btnClearRoute").removeClass("disabled");
		 }, 400);
		
        endRouteDate = (new Date()).getTime();
        if (routePoints.length >= 4) {
        	$("#btnSaveRoute").removeClass("disabled");
        	var routeLoc = createGoogleMapPointsArr();
            drawRoutePolyline(routeLoc);
            showRouteDetails();
    	}
    }
	else    // user clicked on start
	{
		setTimeout(function(){
			$(btnElem).find('img').remove();
			$(btnElem).text('Finish Route');
	    	$(btnElem).append('<img id="finishRouteImg" src="css/images/finishRoute_icon.png">');
	    	$("#btnClearRoute").addClass("disabled");
		 }, 400);
		
    	startRouteDate = (new Date()).getTime();
    	routePoints = [];
    	clearRoute();
    }
	
	isRoute = !isRoute;
}

function showRouteDetails()
{
	$.ajax({
		url : "Bychiple/settings/getElectricFlagAndWeight",
		type : "POST",
		dataType : 'json',
		success : function(data)
		{
			var isElectric = data[0];
			var weight = data[1];
			var msg = buildLabelsOfCurrRouteDetails(isElectric, weight);
			
			bootbox.dialog({
				  message: msg,
				  title: "Route Details",
				  buttons: {
				    main: {
				      label: "Ok",
				      className: "btn-primary",
				      callback: function() {
				      }
				    }
				  }
			});
			
		},
		error : function() {
		}
	});
}

function buildLabelsOfCurrRouteDetails(isElectric, weight)
{
	var routeTimeInHour = (((endRouteDate - startRouteDate) / 1000) / 3600) // route time in hours
	var distance = (google.maps.geometry.spherical.computeLength(bicyclingPath.getPath()) / 1000).toFixed(2);  // distance in km
	var speed = distance / routeTimeInHour;
	var routeTime = routeTimeToShow((endRouteDate - startRouteDate));
	
	var details = '<div id="routeDetails" class="ui-field-contain">' +
	'<label>Time: <span>' + routeTime + '</span></label>' +
	'<label>Speed: <span>' + speed.toFixed(2) + ' km/h</span></label>' +
	'<label>Distance: <span>' + distance + ' km</span></label>';

	if (!isElectric){
		var calories = getMetabolicEquivalent(speed) * weight * routeTimeInHour;
		details += '<label>Calories: <span>' + calories.toFixed(2) + ' Cal</span></label>';
	}

	details += '</div>';

	return details;
}

function btnSaveRouteClicked()
{
	$("#btn-dl-menu").click(); //close menu
	
	bootbox.prompt("Enter route name:", function(result) 
	{       
	    if (result !== null) {
	    	if((result.trim() === "") || (!isEnglishLetter(result))){
	    		result = "Untitled";
	    	}
	    	saveRoute(result);
	    	$("#btnSaveRoute").addClass("disabled");
		} 
	});
}

function createGoogleMapPointsArr()
{
	var routeLoc = [];
	
	for (var i = 0; i < routePoints.length; i+=2) {
		routeLoc.push(new google.maps.LatLng(routePoints[i], routePoints[i+1]));
	}
	
	return routeLoc;
}

function btnClearRouteClicked()
{
	$("#btn-dl-menu").click(); //close menu
	clearRoute();
	focusPositionOnMap();
	focusPositionMapInterval = setInterval(focusPositionOnMap, 30000); 
}

function focusPositionOnMap() 
{
	if (userMarker !== undefined)
		map.setCenter(userMarker.getPosition()); 
}

function clearRoute()
{
	if (!isRoute) {
		$("#btnSaveRoute").addClass('disabled');
	}
	
	if (startMarker !== undefined) {
		startMarker.setMap(null);
	}
	
	if (endMarker !== undefined) {
		endMarker.setMap(null);
	}
	
	if (bicyclingPath !== undefined)
		bicyclingPath.setVisible(false);
}

function saveRoute(routeName)
{	
	var distance = google.maps.geometry.spherical.computeLength(bicyclingPath.getPath()).toFixed(2);
	
	$.ajax({
	      url: "Bychiple/routesManager/saveRoute",
	      type: "POST",
	      data: {
	    	  	locList: routePoints.toString(),
	    	  	routeName: routeName,
	    	  	startDateStr: startRouteDate.toString(),
	    	  	endDateStr: endRouteDate.toString(),
	    	  	routeDistance: distance
			},
	      success: function() {
	      },
	      error: function() {
	          console.log("Failed to save route");
	      }
	});
}

function drawRoutePolyline(routeLoc)
{
	bicyclingPath = new google.maps.Polyline({
	    path: routeLoc,
	    geodesic: true,
	    strokeColor: '#22aadd',
	    strokeOpacity: 0.8,
	    strokeWeight: 8
	});

	bicyclingPath.setMap(map);
	bicyclingPath.setVisible(true);
	
	startMarker = createMarker(routeLoc[0], startRouteMarkerPath, ROUTE_MARKER_ZINDEX); 
	endMarker = createMarker(routeLoc[routeLoc.length - 1], finishRouteMarketPath, ROUTE_MARKER_ZINDEX); 
}

function getMetabolicEquivalent(speed)
{
	// the speed is kilometer per hour
	var MET;
	
	switch (true) {
    	case (speed < 8.85):
    		MET = 3.5;
    		break;
    	case (speed >= 8.85 && speed < 16.09):
    		MET = 5.8;
    		break;
    	case (speed >= 16.09 && speed < 19.31):
    		MET = 6.0;
    		break;
    	case (speed >= 19.31 && speed < 22.53):
    		MET = 8.0;
    		break;
    	case (speed >= 22.53 && speed < 25.74):
    		MET = 10.0;
    		break;
    	case (speed >= 25.74 && speed < 32.18):
    		MET = 12.0;
    		break;
    	case (speed >= 32.18):
    		MET = 16.0;
    		break;
	}
	
	return MET;
}







//--------------------------------
//-       Routes History         -
//--------------------------------

function routesHistoryClicked()
{
	$("#btn-dl-menu").click(); //close menu
	var msg;
	
	$.ajax({
		url: "Bychiple/routesManager/getRoutesHistoryList",
		type: "POST",
		dataType: 'json',
		success: function(routeDetails){
			var routesHistory = routeDetails[0];
			var isElectric = routeDetails[1];
			
			if (routesHistory.length === 0){
				msg = "No routes history available.";
			}
			else {
				msg = buildRouteHistory(routesHistory, isElectric);
			}
			
			showRouteHistory(msg);
		},
		error: function() {
        }
	});
}

function buildRouteHistory(routesHistory, isElectric)
{
	var msg;
	
	var startMsg = '<form><div class="ui-field-contain">' + 
	'<select name="select-native-2" id="select-native-2" data-mini="true" onchange="onSelectRoute()">';
	var endSelect = '</select></div>';
	var comboBoxOptions = '<option disabled selected value>-- select route --</option>';
	
	$.each(routesHistory, function(i, route) {
		var option = '<option value="' + route.id + '">'+ route.name + '</option>';
		comboBoxOptions += option;
	});
		
	var details = buildLabelsOfRouteHistoryDetails(isElectric) + '</form>';

	msg = startMsg + comboBoxOptions + endSelect + details;
	
	return msg;
}

function buildLabelsOfRouteHistoryDetails(isElectric)
{
	var details = '<div id="routeDetails" class="ui-field-contain">' +
		'<label>Time: <span id="routeTime"></span></label>' +
		'<label>Speed: <span id="routeSpeed"></span></label>' +
		'<label>Distance: <span id="routeDistance"></span></label>';

	if (!isElectric){
		details += '<label id="calLabel">Calories: <span id="routeCalories"></span></label>';
	}

	details += '</div>';
	
	return details;
}

function showRouteHistory(msg)
{
	bootbox.dialog({
		message: msg,
		title: "Routes History",
		buttons: {
			danger: {
				label: "Cancel",
				className: "btn-danger",
				callback: function() {
				}
			},
			success: {
				label: "Ok",
				className: "btn-success",
				callback: function() {
					routeOptionClicked();
				}
			}
		}
	});
}

function routeOptionClicked()
{
	clearRoute();
	var routeId = $("#select-native-2").val();
	getRoutePointsListAndDrawOnMap(routeId);
}

function getRoutePointsListAndDrawOnMap(routeID)
{
	$.ajax({
		url: "Bychiple/routesManager/getRoutePointsList",
		type: "POST",
		dataType: 'json',
		data: {routeId: routeID},
		success: function(routePointsList){
			var routeLoc = [];
			$.each(routePointsList, function(i, point) { 
				  routeLoc.push(new google.maps.LatLng(point.lat, point.lng));
			});
			
			drawRoutePolyline(routeLoc); 
			
			clearInterval(focusPositionMapInterval);
			map.setCenter(startMarker.getPosition()); 
		},
		error: function() {
        }
	});
}

function onSelectRoute()
{
	var routeID = $("#select-native-2").val();
	
	$.ajax({
		url: "Bychiple/routesManager/getRouteDetails",
		type: "POST",
		dataType: 'json',
		data: {routeId: routeID},
		success: function(routeDetails)
		{
			var startDate = new Date(routeDetails[0]);
			var endDate = new Date(routeDetails[1]);
			var routeTimeInHour = (((endDate - startDate) / 1000) / 3600) // route time in hours
			var distance = routeDetails[2] / 1000;  // distance in km
			var weight = routeDetails[3];
			var isElectric = routeDetails[4];
			var speed = distance / routeTimeInHour;
			var routeTime = routeTimeToShow(endDate - startDate);
			
			fillDetailsToLabels(routeTime, speed, distance, isElectric, weight, routeTimeInHour);
			
		},
		error: function() {
        }
	});
}

function fillDetailsToLabels(routeTime, speed, distance, isElectric, weight, routeTimeInHour)
{
	$("#routeTime").text(routeTime);
	$("#routeSpeed").text(speed.toFixed(2) + " km/h");
	$("#routeDistance").text(distance.toFixed(2) + " km");
	
	if (isElectric){   // we don't allow this feature for electric bicycle
		$("#calLabel").hide();
	}
	else {
		var calories = getMetabolicEquivalent(speed) * weight * routeTimeInHour;
		$("#routeCalories").text(calories.toFixed(2) + " Cal");
		$("#calLabel").show();
	}
}

function routeTimeToShow(routeTimeInMilli)
{
	var routeTime = routeTimeInMilli / 1000; // time in seconds
	var timeToShow;
	
	if (routeTime < 60){
		timeToShow = routeTime.toFixed(0) + " sec";
	}
	else {
		routeTime = routeTime / 60;  // time in minutes
		if (routeTime < 60){
			timeToShow = routeTime.toFixed(0) + " min";
		}
		else {
			timeToShow = (routeTime / 60).toFixed(2) + " hr";
		}	
	}
	
	return timeToShow;
}






//--------------------------------
//-         Validation           -
//--------------------------------

function isStrNotNullOrEmpty(str)
{
	return ((str !== null) && (str.trim() !== ""));
}

function isEnglishLetter(val)
{
	var result = /[^\x00-\x7F]+/.test(val);
	return !result;
}
