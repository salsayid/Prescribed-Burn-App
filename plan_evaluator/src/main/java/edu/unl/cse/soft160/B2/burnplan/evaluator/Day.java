package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.util.Date;


public class Day {
	private Boolean isOutdoorBuringBanned;
	private Season season;
	private Weather weather;
	private Date date;
		
	public Day(Date date, Weather weather, Boolean isOutdoorBurningBanned, Season season) {
		this.weather = weather;
		this.isOutdoorBuringBanned = isOutdoorBurningBanned;
		this.season = season;
		this.date = date;
	}

	public Boolean isOutdoorBuringBanned() {
		return isOutdoorBuringBanned;
	}

	public void setOutdoorBuringBanned(Boolean isOutdoorBuringBanned) {
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
