package edu.unl.cse.soft160.B2.burnplan.evaluator;

public class Weather {
	private double windSpeed; //mph
	private double humidity;
	private double relativeHumidity; //yes this is different from humidity
	private Direction windDirection;
	private double rainChance;
	private double rainAmount; //inches
	private boolean isColdFrontApproaching;
	
	public Weather(double windSpeed, double humidity, double relativeHumidity, Direction windDirection,
			double rainChance, double rainAmount, boolean isColdFrontApproaching) {
		this.windSpeed = windSpeed;
		this.humidity = humidity;
		this.relativeHumidity = relativeHumidity;
		this.windDirection = windDirection;
		this.rainChance = rainChance;
		this.rainAmount = rainAmount;
		this.isColdFrontApproaching = isColdFrontApproaching;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public double getRelativeHumidity() {
		return relativeHumidity;
	}

	public void setRelativeHumidity(double relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}

	public Direction getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(Direction windDirection) {
		this.windDirection = windDirection;
	}

	public double getRainChance() {
		return rainChance;
	}

	public void setRainChance(double rainChance) {
		this.rainChance = rainChance;
	}

	public double getRainAmount() {
		return rainAmount;
	}

	public void setRainAmount(double rainAmount) {
		this.rainAmount = rainAmount;
	}

	public boolean isColdFrontApproaching() {
		return isColdFrontApproaching;
	}

	public void setColdFrontApproaching(boolean isColdFrontApproaching) {
		this.isColdFrontApproaching = isColdFrontApproaching;
	}
}
