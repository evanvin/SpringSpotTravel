package com.spottravel.domain;

import java.util.Arrays;


public class AdventureForm {

	
	private String zip;
	private String[] interests;
	private int innerBound;
	private int outerBound;
	private int resultsWanted;
	
	
	
	
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String[] getInterests() {
		return interests;
	}
	public void setInterests(String[] interests) {
		this.interests = interests;
	}
	public int getInnerBound() {
		return innerBound;
	}
	public void setInnerBound(int innerBound) {
		this.innerBound = innerBound;
	}
	public int getOuterBound() {
		return outerBound;
	}
	public void setOuterBound(int outerBound) {
		this.outerBound = outerBound;
	}
	public int getResultsWanted() {
		return resultsWanted;
	}
	public void setResultsWanted(int resultsWanted) {
		this.resultsWanted = resultsWanted;
	}
	@Override
	public String toString() {
		return "AdventureForm [zip=" + zip + ", interests="
				+ Arrays.toString(interests) + ", innerBound=" + innerBound
				+ ", outerBound=" + outerBound + ", resultsWanted="
				+ resultsWanted + "]";
	}
	
	
	
	
	
}
