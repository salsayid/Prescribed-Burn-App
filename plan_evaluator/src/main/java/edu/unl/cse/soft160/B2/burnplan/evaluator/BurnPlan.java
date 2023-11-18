package edu.unl.cse.soft160.B2.burnplan.evaluator;

public class BurnPlan {
	private double latitude;
	private double longitude;
	private boolean isBlackLineVolatile;
	private Day day;
	private Supply supply;
	private FirePattern firePattern;
	public BurnPlan(Day day, double latitude, double longitude, boolean isBlackLineVolatile, FirePattern firePattern, Supply supply) {
		this.day = day;
		this.latitude = latitude;
		this.longitude = longitude;
		this.isBlackLineVolatile = isBlackLineVolatile;
		this.supply = supply;
		this.firePattern = firePattern;
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

	public Supply getSupply() {
		return supply;
	}

	public void setSupply(Supply supply) {
		this.supply = supply;
	}

	public FirePattern getFirePattern() {
		return firePattern;
	}

	public void setFirePattern(FirePattern firePattern) {
		this.firePattern = firePattern;
	}

}
