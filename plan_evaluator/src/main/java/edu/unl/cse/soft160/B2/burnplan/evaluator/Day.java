package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.time.LocalDate;
import java.time.LocalTime;

public class Day {
	private boolean isOutdoorBuringBanned;
	private LocalTime timeOfDay; //hour:minute
	private Season season;
	private Weather weather;
	private LocalDate date;
		
	public Day(LocalDate date, Weather weather, boolean isOutdoorBurningBanned, LocalTime timeOfDay, Season season) {
		this.weather = weather;
		this.isOutdoorBuringBanned = isOutdoorBurningBanned;
		this.timeOfDay = timeOfDay;
		this.season = season;
		this.date = date;
	}

	public boolean isOutdoorBuringBanned() {
		return isOutdoorBuringBanned;
	}

	public void setOutdoorBuringBanned(boolean isOutdoorBuringBanned) {
		this.isOutdoorBuringBanned = isOutdoorBuringBanned;
	}

	public LocalTime getTimeOfDay() {
		return timeOfDay;
	}

	public void setTimeOfDay(LocalTime timeOfDay) {
		this.timeOfDay = timeOfDay;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	

}
