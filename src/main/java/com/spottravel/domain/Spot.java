package com.spottravel.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.maps.model.LatLng;

public class Spot implements Serializable {

	private static final long serialVersionUID = -3363410936403036707L;
	
	private List<Zip> locations = new ArrayList<Zip>(0);
	private Zip start;
	private int outerBound;
	private int innerBound;
	private int resultsWanted;
	private boolean isRandom;
	private String[] interests;
	private double maxLat;
	private double minLat;
	private double maxLon;
	private double minLon;
	private int mapZoom;
	private List<List<LatLng>> searchZone = new ArrayList<List<LatLng>>(0); 
	
	public Spot(){}
	
	
	
	
	public Spot(int outerBound, int innerBound, int resultsWanted, boolean isRandom, String[] interests, List<Zip> locations) {
		this.locations = locations;
		this.outerBound = outerBound;
		this.innerBound = innerBound;
		this.resultsWanted = resultsWanted;
		this.isRandom = isRandom;
		this.interests = interests;
	}




	public int getMapZoom() {
		return mapZoom;
	}
	public void setMapZoom(int mapZoom) {
		this.mapZoom = mapZoom;
	}
	public List<Zip> getLocations() {
		return locations;
	}
	public void setLocations(List<Zip> locations) {
		this.locations = locations;
	}
	public Zip getStart() {
		return start;
	}
	public void setStart(Zip start) {
		this.start = start;
	}
	public int getOuterBound() {
		return outerBound;
	}
	public void setOuterBound(int outerBound) {
		this.outerBound = outerBound;
	}
	public int getInnerBound() {
		return innerBound;
	}
	public void setInnerBound(int innerBound) {
		this.innerBound = innerBound;
	}
	public int getResultsWanted() {
		return resultsWanted;
	}
	public void setResultsWanted(int resultsWanted) {
		this.resultsWanted = resultsWanted;
	}
	public boolean isRandom() {
		return isRandom;
	}
	public void setRandom(boolean isRandom) {
		this.isRandom = isRandom;
	}
	public String[] getInterests() {
		return interests;
	}
	public void setInterests(String[] interests) {
		this.interests = interests;
	}
	public double getMaxLat() {
		return maxLat;
	}
	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}
	public double getMinLat() {
		return minLat;
	}
	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}
	public double getMaxLon() {
		return maxLon;
	}
	public void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
	}
	public double getMinLon() {
		return minLon;
	}
	public void setMinLon(double minLon) {
		this.minLon = minLon;
	}
	public List<List<LatLng>> getSearchZone() {
		return searchZone;
	}
	public void setSearchZone(List<List<LatLng>> searchZone) {
		this.searchZone = searchZone;
	}




	@Override
	public String toString() {
		return "Spot [locations=" + locations.size() + ", start=" + start.toString()
				+ ", outerBound=" + outerBound + ", innerBound=" + innerBound
				+ ", resultsWanted=" + resultsWanted + ", isRandom=" + isRandom
				+ ", interests=" + Arrays.toString(interests) + ", maxLat="
				+ maxLat + ", minLat=" + minLat + ", maxLon=" + maxLon
				+ ", minLon=" + minLon + ", mapZoom=" + mapZoom
				+ ", searchZone=" + searchZone + "]";
	}
	
	
	
	
	
}