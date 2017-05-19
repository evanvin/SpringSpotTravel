package com.spottravel.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;

public class Zip implements Comparator<Zip>, Serializable{

	private static final long serialVersionUID = 5049549434735916556L;
	
	private String zip;
	private String city;
	private String state;
	private double distance;
	private double latitude;
	private double longitude;
	private int rank;
	private List<PlacesSearchResult> places = new ArrayList<PlacesSearchResult>(0);
	private DirectionsRoute route;
	
	
	
	
	public DirectionsRoute getRoute() {
		return route;
	}
	public void setRoute(DirectionsRoute route) {
		this.route = route;
	}
	public List<PlacesSearchResult> getPlaces() {
		return places;
	}
	public void setPlaces(List<PlacesSearchResult> places) {
		this.places = places;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public int compare(Zip z1, Zip z2) {
		return z1.getRank() - z2.getRank();
	}
	
	@Override
	public String toString() {
		return "Zip [zip=" + zip + ", city=" + city + ", state=" + state
				+ ", distance=" + distance + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", rank=" + rank + "]";
	}

	
	
	
}