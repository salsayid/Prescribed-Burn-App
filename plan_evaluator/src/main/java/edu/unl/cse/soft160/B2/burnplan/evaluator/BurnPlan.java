package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.sql.Time;

public class BurnPlan {
	private Double latitude;
	private Double longitude;
	private Integer windSpeed;
	private Double humidity;
	private Double relativeHumidity; //yes this is different from humidity
	private Boolean isOutdoorBuringBanned;
	private Direction windDirection;
	private Time timeOfDay;
	private Season season;
	private Boolean isBlackLineVolatile;
	private Double rainChance;
	private Double rainAmount;
	private Boolean isColdFrontApproaching;
	private Double airTemperature;
	private Integer widthOfBlacklines;
	
	public BurnPlan() {
		
	}

}
