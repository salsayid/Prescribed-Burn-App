package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.util.Date;


public class Day {
	private Boolean isOutdoorBuringBanned;
	private Weather weather;
	private Date date;
		
	public Day(Date date, Weather weather, Boolean isOutdoorBurningBanned) {
		this.weather = weather;
		this.isOutdoorBuringBanned = isOutdoorBurningBanned;
		this.date = date;
	}

	public Boolean isOutdoorBuringBanned() {
		return isOutdoorBuringBanned;
	}

	public void setOutdoorBuringBanned(Boolean isOutdoorBuringBanned) {
		this.isOutdoorBuringBanned = isOutdoorBuringBanned;
	}

	public Weather getWeather() {
		return weather;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	

}
