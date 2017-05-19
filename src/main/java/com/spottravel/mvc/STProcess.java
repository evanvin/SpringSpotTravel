package com.spottravel.mvc;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import org.h2.util.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PhotoRequest;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.Photo;
import com.google.maps.model.PhotoResult;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.spottravel.domain.AdventureForm;
import com.spottravel.domain.Spot;
import com.spottravel.domain.Zip;



public class STProcess {
	
	
	final private String API_KEY = "AIzaSyA3iaP3eQR9ax9NJexRoSoOBVGXcNIOzjM";

	
	public STProcess(){
		
	}
	
	public Spot processDataFromForm(AdventureForm adventureForm){
		System.out.println(adventureForm.toString());
		return processData(adventureForm.getOuterBound(), adventureForm.getInnerBound(), adventureForm.getResultsWanted(), false, adventureForm.getInterests());
	}
	
	public Spot processDataTest(){
		return processData(150, 50, 10, false, new String[]{"bar", "atm"});
	}
	
	
	
	public Spot processData(int outerBound, int innerBound, int resultsWanted, boolean isRandom, String[] interests){
		final DefaultResourceLoader loader = new DefaultResourceLoader(); 
		Resource resource = loader.getResource("classpath:META-INF/spring/rank.json"); 
		
		ObjectMapper mapper = new ObjectMapper();
		List<Zip> zips = new ArrayList<Zip>();
		try {
			zips = mapper.readValue(resource.getFile(), new TypeReference<List<Zip>>(){});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//selected properties
		
		final String startZip = "20910";
		//get zip info of start location
		Spot spot = new Spot(outerBound, innerBound, resultsWanted, isRandom, interests, zips);
		
		spot.setStart(getInfoForHome(spot.getLocations(), startZip));
		
		
		//load button is clicked
		spot = findLocations(spot);
		
		return spot;
	}
	
	
	
	private Spot findLocations(Spot spot){
		//get bounding box values for search radius
		final double R = 3959.0;	//earth radius in miles
		spot.setMaxLat(spot.getStart().getLatitude() + Math.toDegrees(spot.getOuterBound()/R));
		spot.setMinLat(spot.getStart().getLatitude() - Math.toDegrees(spot.getOuterBound()/R));
		spot.setMaxLon(spot.getStart().getLongitude() + Math.toDegrees(spot.getOuterBound()/R/Math.cos(Math.toRadians(spot.getStart().getLatitude()))));
		spot.setMinLon(spot.getStart().getLongitude() - Math.toDegrees(spot.getOuterBound()/R/Math.cos(Math.toRadians(spot.getStart().getLatitude()))));
		
		//remove start from locations list
		removeStartZip(spot.getLocations(), spot.getStart().getZip());
		
		//remove any locations outside the search radius zone
		spot.setLocations(removeLocationsOutsideSearchZone(spot));
		
		//remove duplicates of city state
		spot.setLocations(removeDuplicatesOfCityState(spot.getLocations()));
		
		System.out.println(spot.toString());
		//remove close bound locations (removing locations that are close to one another)
		spot.setLocations(removeLocationsInCloseProximity(spot.getLocations(), spot.getResultsWanted(), 10));
		
		//shuffle results if random is selected
		if(spot.isRandom()){
			Collections.shuffle(spot.getLocations());
			spot.setResultsWanted(1);
		}
		
		//minimize locations to results wanted or if locations list is below 10
		spot.setLocations(minimizeLocationsSoSizeOfResultsWanted(spot));
		
		//remove odd characters from city names
		//spot.setLocations(removeOddCharacters(spot));		
		
		//get map details
		spot = loadMapDetails(spot);
		
		return spot;		
	}
	

	private Spot loadMapDetails(Spot spot) {
		//get the map zoom
		spot.setMapZoom(determineMapZoom(spot.getOuterBound()));
		
		//get places for each location
		GeoApiContext context = new GeoApiContext().setApiKey(API_KEY);
		LatLng startLL = new LatLng(spot.getStart().getLatitude(), spot.getStart().getLongitude());
		String[] interests = spot.getInterests();

		for(Zip z : spot.getLocations()){
			
			try {
				//get places for that location
				//5 miles radius (8046 meters)
				System.out.println(interests[0]);
				PlacesSearchResponse psr = PlacesApi.nearbySearchQuery(context, new LatLng(z.getLatitude(), z.getLongitude())).radius(8046).type(PlaceType.valueOf(interests[0].toUpperCase())).await();
				
				/* TODO: until we figure out how to search for places with multiple type criteria,
				 * we will take out the places not containing the criteria types */
				List<PlacesSearchResult> results = new ArrayList<PlacesSearchResult>(Arrays.asList(psr.results));
				List<PlacesSearchResult> resultsNew = new ArrayList<PlacesSearchResult>();
				for(int i = 0; i < results.size(); i++){
					if(hasPlaceType(results.get(i).types, interests)){
						//results.remove(i);
					}
					else{
						resultsNew.add(results.get(i));
						//get picture for that place and update it's places photoReference with it.
						/* TODO: comment this out once you know it works until production so server pings wont be too much */
//						Photo[] photos = results.get(i).photos;
//						if(photos != null && photos.length > 0){
//							PhotoResult pr = PlacesApi.photo(context, photos[0].photoReference).maxHeight(70).await();
//							StringBuilder sb = new StringBuilder();
//							sb.append("data:image/png;base64,");
//							sb.append(DatatypeConverter.printBase64Binary(pr.imageData));
//							photos[0].photoReference = sb.toString();
//							results.get(i).photos = photos;
//						}
//						else{
//							Photo[] pTemp = new Photo[1];
//							pTemp[0] = new Photo();
//							pTemp[0].photoReference = "no photo";
//							results.get(i).photos = pTemp;
//						}
					}
				}
				
				
				z.setPlaces(resultsNew);
				/* TODO: end */
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}

		
		
		//get routes from start to each location
		for(int i = 0; i < spot.getLocations().size(); i++){
			LatLng endLL = new LatLng(spot.getLocations().get(i).getLatitude(), spot.getLocations().get(i).getLongitude());
			try {
				DirectionsResult result = DirectionsApi.newRequest(context).origin(startLL).destination(endLL).await();
				spot.getLocations().get(i).setRoute(result.routes[0]);
			} catch (Exception e) {
				spot.getLocations().get(i).setRoute(new DirectionsRoute());
			}
		}
		

		//get donut search zone polygon
		spot = getSearchZone(spot);
	
		return spot;

	}
	
	

	public void printArrays(String[] x, String[] y){
		System.out.println(Joiner.on(",").join(x));
		System.out.println(Joiner.on(",").join(y));
	}	
	
	private boolean hasPlaceType(String[] placeTypes, String[] interests){
		return Collections.disjoint(Arrays.asList(placeTypes), Arrays.asList(interests));
	}
	
	
	private List<Zip> minimizeLocationsSoSizeOfResultsWanted(Spot spot) {
		int count = spot.getLocations().size() < spot.getResultsWanted() ? spot.getLocations().size() : spot.getResultsWanted();
		return spot.getLocations().subList(0, count);
	}
	
	private List<Zip> removeOddCharacters(Spot spot) {
		
		for(int i = 0; i < spot.getLocations().size(); i++){
			spot.getLocations().get(i).setCity(spot.getLocations().get(i).getCity().replace("'", " "));
		}
		return spot.getLocations();
	}

	private int determineMapZoom(int outerBound) {
		if(outerBound <= 75)
			return 9;
		else if(outerBound >=76 && outerBound <=150)
			return 8;
		else if(outerBound >=151 && outerBound <=375)
			return 7;
		else if(outerBound >=376 && outerBound <=575)
			return 6;
		else if(outerBound >=576 && outerBound <=1100)
			return 5;
		else
			return 4;
	}

	private List<Zip> removeLocationsInCloseProximity(List<Zip> locations, int resultsWanted, int minDistance) {
		List<Zip> temp2 = new ArrayList<Zip>();
		temp2.add(locations.get(0));
		int count = 1;
		
		for(int k = 0; k < 10; k++){
			for(int i = 0; i < locations.size(); i++){
				boolean flag = false;
				for(int j = 0; j < temp2.size(); j++){
					double distance = getDistance(locations.get(i).getLatitude(), locations.get(i).getLongitude(), temp2.get(j).getLatitude(), temp2.get(j).getLongitude());
					if(distance < minDistance){			//cities minDtsiance in miles apart from one another
						flag = true;
						break;
					}
				}				
				if(!flag)
					temp2.add(locations.get(i));
			}
		}
		return temp2;
	}

	private List<Zip> removeDuplicatesOfCityState(List<Zip> locations) {
		List<Zip> temp = new ArrayList<Zip>();
		for(Zip z : locations){
			boolean flag = false;
			for(Zip t : temp){
				if(z.getState().equals(t.getState()) && z.getCity().equals(t.getCity())){
					flag = true;
					break;
				}
			}
			if(!flag)
				temp.add(z);
		}
		return temp;
	}

	private List<Zip> removeLocationsOutsideSearchZone(Spot s) {
		List<Zip> inSearchZone = new ArrayList<Zip>();
		double minLat = s.getMinLat();
		double maxLat = s.getMaxLat();
		double minLon = s.getMinLon();
		double maxLon = s.getMaxLon();
		List<Zip> locations = s.getLocations();
		Zip start = s.getStart();
		int innerBound = s.getInnerBound();
		int outerBound = s.getOuterBound();
		
		for(int i = 0; i < locations.size(); i++){
			Zip z = locations.get(i);
			if(z.getLatitude() >= minLat && z.getLatitude() <= maxLat && z.getLongitude() >= minLon && z.getLongitude() <= maxLon){
				double distance = getDistance(start.getLatitude(), start.getLongitude(), z.getLatitude(), z.getLongitude());
				if(distance >= innerBound && distance <= outerBound){
					z.setDistance(distance);
					inSearchZone.add(z);
				}
			}
		}
		return inSearchZone;
	}

	private double getDistance(double lat1, double lon1, double lat2, double lon2) {
		final double R = 3959.0;	//earth radius in miles
		double rLat1 = Math.toRadians(lat1);
		double rLat2 = Math.toRadians(lat2);
		double tri1 = Math.toRadians(lat2-lat1);
		double tri2 = Math.toRadians(lon2-lon1);
		
		double a = Math.sin(tri1/2.0) * Math.sin(tri1/2.0) + Math.cos(rLat1) * Math.cos(rLat2) * Math.sin(tri2/2.0) * Math.sin(tri2/2.0);
		double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c;
		return d;
	}

	public Zip getInfoForHome(List<Zip> zips, String startZip){
		for(int i = 0; i < zips.size(); i++){
			if(zips.get(i).getZip().equals(startZip)){
				return zips.get(i);
			}
		}
		return null;
	}
	
	public static void removeStartZip(List<Zip> zips, String startZip){
		for(int i = 0; i < zips.size(); i++){
			if(zips.get(i).getZip().equals(startZip)){
				zips.remove(i);
				break;
			}
		}
	}
	
	
	
	public Spot getSearchZone(Spot spot){
		List<List<LatLng>> zones = new ArrayList<List<LatLng>>();
		LatLng start = new LatLng(spot.getStart().getLatitude(), spot.getStart().getLongitude());
		
		zones.add(drawCircle(start, spot.getOuterBound(), 1));
		zones.add(drawCircle(start, spot.getInnerBound(), -1));
		
		spot.setSearchZone(zones);
		return spot;
	}
	
	
	
	public List<LatLng> drawCircle(LatLng point, int radius, int dir) {
		double d2r = Math.PI / 180; // degrees to radians
		double r2d = 180 / Math.PI; // radians to degrees
		double earthsradius = 3963.0; // 3963 is the radius of the earth in
										// miles

		int points = 32;

		// find the raidus in lat/lon
		double rlat = (radius / earthsradius) * r2d;
		double rlng = rlat / Math.cos(point.lat * d2r);

		List<LatLng> extp = new ArrayList<LatLng>();

		int start = 0;
		int end = 0;
		if (dir == 1) {
			start = 0;
			end = points + 1;
		} else {
			start = points + 1;
			end = 0;
		}

		for (int i = start; (dir == 1 ? i < end : i > end); i = i + dir) {
			double theta = Math.PI * (double) (i / (double) (points / 2));
			double ey = point.lng + (rlng * Math.cos(theta)); // center a +
																// radius x *
																// cos(theta)
			double ex = point.lat + (rlat * Math.sin(theta)); // center b +
																// radius y *
																// sin(theta)
			extp.add(new LatLng(ex, ey));
		}
		return extp;
	}
}