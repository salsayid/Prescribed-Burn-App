package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.sql.Time;

public class BurnPlan {
	private double latitude;
	private double longitude;
	private boolean isBlackLineVolatile;
	private Day day;
	
	public BurnPlan(Day day, double latitude, double longitude, boolean isBlackLineVolatile) {
		this.day = day;
		this.latitude = latitude;
		this.longitude = longitude;
		this.isBlackLineVolatile = isBlackLineVolatile;
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

	public boolean isBlackLineVolatile() {
		return isBlackLineVolatile;
	}

	public void setBlackLineVolatile(boolean isBlackLineVolatile) {
		this.isBlackLineVolatile = isBlackLineVolatile;
	}

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

}
