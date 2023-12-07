package edu.unl.cse.soft160.B2.burnplan.evaluator;

public class Weather {
	private Double windSpeed; // mph
	private Double humidity;
	private Double relativeHumidity; // yes this is different from humidity
	private Direction windDirection;
	private Double rainChance;
	private Double rainAmount; // inches
	private boolean isColdFrontApproaching;
	private Double temperature; // Fahrenheit

	public Weather(Double windSpeed, Direction windDirection, Double humidity, Double relativeHumidity,
			Double rainChance, Double rainAmount, boolean isColdFrontApproaching, Double temperature) {
		this.windSpeed = windSpeed;
		this.humidity = humidity;
		this.relativeHumidity = relativeHumidity;
		this.windDirection = windDirection;
		this.rainChance = rainChance;
		this.rainAmount = rainAmount;
		this.isColdFrontApproaching = isColdFrontApproaching;
		this.temperature = temperature;
	}

	public Double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public Double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public Double getRelativeHumidity() {
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

	public Double getRainChance() {
		return rainChance;
	}

	public void setRainChance(double rainChance) {
		this.rainChance = rainChance;
	}

	public Double getRainAmount() {
		return rainAmount;
	}

	public void setRainAmount(double rainAmount) {
		this.rainAmount = rainAmount;
	}

	public Boolean isColdFrontApproaching() {
		return isColdFrontApproaching;
	}

	public void setColdFrontApproaching(boolean isColdFrontApproaching) {
		this.isColdFrontApproaching = isColdFrontApproaching;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
}
