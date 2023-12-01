package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.util.Date;


public class Day {
	private boolean isOutdoorBuringBanned;
	private Season season;
	private Weather weather;
	private Date date;
		
	public Day(Date date, Weather weather, boolean isOutdoorBurningBanned, Season season) {
		this.weather = weather;
		this.isOutdoorBuringBanned = isOutdoorBurningBanned;
		this.season = season;
		this.date = date;
	}

	public boolean isOutdoorBuringBanned() {
		return isOutdoorBuringBanned;
	}

	public void setOutdoorBuringBanned(boolean isOutdoorBuringBanned) {
		this.isOutdoorBuringBanned = isOutdoorBuringBanned;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
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
