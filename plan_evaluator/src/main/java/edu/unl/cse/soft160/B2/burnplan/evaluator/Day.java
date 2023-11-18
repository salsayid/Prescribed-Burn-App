package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.sql.Time;
import java.time.LocalDate;

public class Day {
	private boolean isOutdoorBuringBanned;
	private Time timeOfDay;
	private Season season;
	private Weather weather;
	private LocalDate date;
	
	public Day(LocalDate date, Weather weather, boolean isOutdoorBurningBanned, Time timeOfDay, Season season) {
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

	public Time getTimeOfDay() {
		return timeOfDay;
	}

	public void setTimeOfDay(Time timeOfDay) {
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
