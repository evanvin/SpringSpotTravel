<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>

	<head>
		<title>Spring MVC Starter Application</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		
		<style>
			html, body {
		        height: 100%;
		        margin: 0;
		        padding: 0;
		      }
		      
		      .hidden{
		      	display:none;
		      }
		      
		      .placesAtLocation{
		      	min-height: 68px;
		      	max-height: 400px;
		      	overflow-y: scroll !important;
		      }
		      
		      .placesAtLocation::-webkit-scrollbar-track{
		      		-webkit-box-shadow: inset 0 0 6px rgba(53,53,49,0.3);
					background-color: #FF7043;
		      }
		      
		      .placesAtLocation::-webkit-scrollbar{
		      		width: 6px;
					background-color: #FF7043;
		      }
		      
		      .placesAtLocation::-webkit-scrollbar-thumb{
		      		background-color: #353531;
		      }
		      
		      
		      
			  #map,main{height:100%;width:100%;}
			  #nav-mobile{background-color:transparent !important; box-shadow: none; width: 400px;}
			 /* main{padding-left:300px;}*/
			 .side-nav::-webkit-scrollbar {
					width: 0px;  /* remove scrollbar space */
					background: transparent;  /* optional: just make scrollbar invisible */
				}
		</style>
		
		<link rel="stylesheet" href="http://fonts.googleapis.com/icon?family=Material+Icons"/>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.7/css/materialize.min.css"/>
		
		<script   src="https://code.jquery.com/jquery-3.1.0.min.js"   integrity="sha256-cCueBR6CsyA4/9szpPfrX3s49M9vUU5BgtiJj06wt/s="   crossorigin="anonymous"></script>
		<script src="<c:url value="/static/resources/js/materialize.js"/>"></script>
		
		
	</head>

	<body>

	<header>
		<ul id="nav-mobile" class="side-nav fixed"
			style="transform: translateX(0%);">
			<div class="row" id="sidebarItems"></div>
		</ul>
	</header>
	<main>
	
	<div id="map"></div>
	</main>

	<script>
		var map;
		var routes = [];
		var placeMarkers = [];
		var spot = JSON.parse('${spot}');
		var placeinfowindow;
	  	var indexOfPlaceMarkersOpen = 0;

		function initMap() {

			console.log(spot.mapZoom);
			map = new google.maps.Map(document.getElementById('map'), {
				center : {
					lat : spot.start.latitude,
					lng : spot.start.longitude
				},
				zoom : spot.mapZoom
			});

			for (var i = 0; i < spot.locations.length; i++) {
				setupMarker(spot.locations[i], i);
				setupLines(spot.locations[i], i);
				setupPlaces(spot.locations[i],i);
			}
			

			drawSearchZone(spot.searchZone);

		}

		function setupPlaces(bound, i) {

			var places = [];
			for (var j = 0; j < bound.places.length; j++) {
				placeinfowindow = new google.maps.InfoWindow();
				var marker = new google.maps.Marker(
						{
							map : map,
							position : bound.places[j].geometry.location,
							icon : "https://maps.gstatic.com/intl/en_ALL/mapfiles/markers2/measle.png"
						});

				google.maps.event.addListener(marker, 'click', function() {
					placeinfowindow.setContent(bound.places[j-1].name);
					placeinfowindow.open(map, this);
				});

				marker.setVisible(false);
				places.push(marker);
			}
			placeMarkers.push(places);
		}

		function setupLines(bound, i) {
			var line = new google.maps.Polyline({
				path : google.maps.geometry.encoding
						.decodePath(bound.route.overviewPolyline.encodedPath),
				strokeColor : '#009688',
				strokeWeight : 3
			});
			line.setMap(null);
			routes.push(line);
		}

		function setupMarker(bound, i) {
			var infowindow = new google.maps.InfoWindow();
			var marker;
			var contentString = '<div id="content">' + '<div id="siteNotice">'
					+ '</div>' + '<h5 id="firstHeading" class="firstHeading">'
					+ bound.city + ', ' + bound.state + '</h5>' + '</div>';

			var iLat = bound.latitude;
			var iLon = bound.longitude;
			var latlon = {
				lat : iLat,
				lng : iLon
			};
			var city = bound.city;
			marker = new google.maps.Marker({
				position : latlon,
				map : map,
				info : contentString
			});
			google.maps.event.addListener(marker, 'click',
					(function(marker, i) {
						return function() {
							infowindow.setContent(this.info);
							infowindow.open(map, marker);
						}
					})(marker, i));

			google.maps.event.addListener(marker, 'mouseover', (function(
					marker, i) {
				return function() {
					showRoute(i);
				}
			})(marker, i));

			google.maps.event.addListener(marker, 'mouseout', (function(marker,
					i) {
				return function() {
					hideAllRoutes();
				}
			})(marker, i));
			
			
			
			var sideBarItem = '<div id="spot_location_' + (i+1) + '" ondblclick="popOpenCard(this, ' + i + ');" onmouseout="hideAllRoutes();" onmouseover="showRoute(' + i + ');" class="location-card col s10"><div class="card deep-orange lighten-1"><div class="card-content white-text">' + 
			'<span class="card-title">' + bound.city + ', ' + bound.state + '</span><span class="badge teal white-text">#' + (i+1) + '</span><p>Distance: ' + parseInt(bound.distance) + '</p>'
			+ '';
			
			if(bound.places.length < 1)
				sideBarItem += '<div class="center">Nothing here interests you.</div>';
			else
				sideBarItem += collecAppend(bound.places);
			
			sideBarItem += '</div></div></div>'
			$('#sidebarItems').append(sideBarItem);

		}
		
		
		function collecAppend(places){
			var text = "";
			text += '<div class="collection placesAtLocation hidden teal white-text">';
			$.each(places, function(key, value){
// 				var pic = "";
// 				if(value.photos != null && value.photos[0].photoReference != 'no photo')
// 					pic = '<img src="'+ value.photos[0].photoReference + '" alt="" class="circle">';
// 				else
// 					pic = '<i class="material-icons circle">room</i>';

//uncomment above lines when ready for production
				pic = '<i class="material-icons circle">room</i>';
//remove above line when uncommenting the previous lines				
				
						 
				text += '<li class="collection-item avatar teal white-text" style="min-height:68px;">'
			     	+ pic
			      	+ '<span class="title"><b>' + value.name + '</b></span>'
			      	+ '<p>';
			      	for(var i = 0; i < Math.floor(value.rating); i++)
			      		text += '<i class="material-icons">grade</i>';
			      	text += '</p></li>';
				//sideBarItem += '<a href="#!" class="collection-item">' + value.name + '</a>';
			});
			text += '</div>';
			return text;
		}
		
		function popOpenCard(card, index){
			var isCardOpened = false;
			$('.location-card').each(function(){
				if($(this).hasClass('s12')){
					isCardOpened = true;
					return;
				}
			});
			
			if(isCardOpened){
				hidePlaceMarkersForPlace(indexOfPlaceMarkersOpen);
				//check if current clicked card is the one opened and close it if so
				if($(card).hasClass('s12')){
					$(card).toggleClass('s12');
					$(card).toggleClass('s10');
					$(card).find('.placesAtLocation').toggle('hidden');
				}
				else{
					zoomToObject(routes[index]);
					indexOfPlaceMarkersOpen = index;
					showPlaceMarkersForPlace(indexOfPlaceMarkersOpen);
					$('.location-card.s12').each(function(){
						$(this).toggleClass('s12');
						$(this).toggleClass('s10');
						$(this).find('.placesAtLocation').toggle('hidden');
					});
					$(card).toggleClass('s12');
					$(card).toggleClass('s10');
					$(card).find('.placesAtLocation').toggle('hidden');
				}
			}
			else{
				zoomToObject(routes[index]);
				hidePlaceMarkersForPlace(indexOfPlaceMarkersOpen);
				showPlaceMarkersForPlace(index);
				indexOfPlaceMarkersOpen = index;
				$('.location-card.s12').each(function(){
					$(this).toggleClass('s12');
					$(this).toggleClass('s10');
					$(this).find('.placesAtLocation').toggle('hidden');
				});
				$(card).toggleClass('s10');
				$(card).toggleClass('s12');
				$(card).find('.placesAtLocation').toggle('hidden');
			}
			//otherwise close all cards and then open this one
			
		}

		function showRoute(id) {
			hideAllRoutes();
			if (routes[id] != null && routes[id] != 'undefined')
				routes[id].setMap(map);
		}

		function hideAllRoutes() {
			for (var i = 0; i < routes.length; i++) {
				routes[i].setMap(null);
			}
		}

		function drawSearchZone(searchZone) {
			donut = new google.maps.Polygon({
				paths : searchZone,
				strokeColor : "#4285F4",
				strokeOpacity : 0.6,
				strokeWeight : 2,
				fillColor : "#4285F4",
				fillOpacity : 0.15
			});
			donut.setMap(map);
		}
		
		function zoomToObject(obj){
			var bounds = new google.maps.LatLngBounds();
			var points = obj.getPath().getArray();
			for (var n = 0; n < points.length ; n++){
				bounds.extend(points[n]);
			}
			map.fitBounds(bounds);
		}
		
		function hidePlaceMarkersForPlace(index){
			$.each(placeMarkers[index], function(key, value){
				value.setVisible(false);
			});
		}
		
		function showPlaceMarkersForPlace(index){
			$.each(placeMarkers[index], function(key, value){
				value.setVisible(true);
			});
		}
	</script>
		
		<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA3iaP3eQR9ax9NJexRoSoOBVGXcNIOzjM&callback=initMap&libraries=places,geometry"
    async defer></script>
	</body>
</html>
