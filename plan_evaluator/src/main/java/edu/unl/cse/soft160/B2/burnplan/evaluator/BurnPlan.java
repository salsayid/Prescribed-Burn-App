package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class BurnPlan {
	private Double latitude;
	private Double longitude;
	private Boolean isBlackLineVolatile;
	private Date currentDay;
	private Integer acresToBeBurned;
	private Day dayOfFire;
	private List<Supply> supplies;
	private FirePattern firePattern;
	private FuelType fuelType;
	private Integer widthOfBlacklines;

	public BurnPlan(Day dayOf, Date currentDay, Double latitude, Double longitude, FuelType fuelType, FirePattern firePattern,
			Integer widthOfBlacklines, Boolean isBlackLineVolatile, Integer acresToBeBurned, List<Supply> supplies) {
		this.dayOfFire = dayOf;
		this.latitude = latitude;
		this.longitude = longitude;
		this.isBlackLineVolatile = isBlackLineVolatile;
		this.supplies = supplies;
		this.firePattern = firePattern;
		this.fuelType = fuelType;
		this.widthOfBlacklines = widthOfBlacklines;
		this.acresToBeBurned = acresToBeBurned;
		this.currentDay = currentDay;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Boolean isBlackLineVolatile() {
		return isBlackLineVolatile;
	}

	public void setBlackLineVolatile(boolean isBlackLineVolatile) {
		this.isBlackLineVolatile = isBlackLineVolatile;
	}

	public Day getDay() {
		return dayOfFire;
	}

	public void setDay(Day day) {
		this.dayOfFire = day;
	}

	public List<Supply> getSupplies() {
		return supplies;
	}

	public void setSupplies(Supply supply, List<Supply> supplies) {
		this.supplies = supplies;
	}

	public FirePattern getFirePattern() {
		return firePattern;
	}

	public void setFirePattern(FirePattern firePattern) {
		this.firePattern = firePattern;
	}

	public Date getCurrentDay() {
		return currentDay;
	}

	public void setCurrentDay(Date currentDay) {
		this.currentDay = currentDay;
	}
	public Integer getAcresToBeBurned() {
		return acresToBeBurned;
	}

	public void setAcresToBeBurned(int acresToBeBurned) {
		this.acresToBeBurned = acresToBeBurned;
	}

	public FuelType getFuelType() {
		return fuelType;
	}

	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}

	public Integer getWidthOfBlacklines() {
		return widthOfBlacklines;
	}

	public void setWidthOfBlacklines(int widthOfBlacklines) {
		this.widthOfBlacklines = widthOfBlacklines;
	}

}
