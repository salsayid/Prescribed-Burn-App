package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BurnPlanEvaluationAlgorithm {

	static public boolean checkRedFlagConditions(Weather weather, Day day) {
		int redFlags = 0;
		if (weather.getWindSpeed() > 20) {
			redFlags++;
		}
		if (weather.getRelativeHumidity() < 20) {
			redFlags++;
		}
		if (weather.getTemperature() > 80) {
			redFlags++;
		}
		if (weather.isColdFrontApproaching()) {
			redFlags++;
		}
		if (weather.getRainChance() > 50 && weather.getRainAmount() > 10) {
			redFlags++;
		}
		if (day.isOutdoorBuringBanned()) {
			redFlags++;
		}
		if (redFlags < 6) {
			return false;
		} else {
			return true;
		}
	}

	static public boolean checkSupplies(List<Supply> supplies, int acres) {
		// makes all of the indexes return an error by default
		int pumperIndex = -1;
		int fuelIndex = -1;
		int dripTorchIndex = -1;
		int fireSwatterIndex = -1;
		int backpackPumpIndex = -1;
		int dozerIndex = -1;
		// gets indexes for all required items
		int indexNumber = 0;
		for (Supply supply : supplies) {
			if (supply.getName().equals("pumper")) {
				pumperIndex = indexNumber;
			} else if (supply.getName().equals("fire-starting fuel")) {
				fuelIndex = indexNumber;
			} else if (supply.getName().equals("drip torches")) {
				dripTorchIndex = indexNumber;
			} else if (supply.getName().equals("rakes")) {
				fireSwatterIndex = indexNumber;
			} else if (supply.getName().equals("backpack pump")) {
				backpackPumpIndex = indexNumber;
			} else if (supply.getName().equals("dozer")) {
				dozerIndex = indexNumber;
			}
			indexNumber++;
		}
		// tests whether all supplies are accounted for
		boolean allSuppliesAreAccountedFor = true;
		double oneOverEighty = 0.0125;
		double oneOverTen = 0.1;
		try {

			if ((supplies.get(pumperIndex).getQuantity() / acres) < oneOverEighty) {
				allSuppliesAreAccountedFor = false;
			} else if ((supplies.get(fuelIndex).getQuantity() / acres) < oneOverTen) {
				allSuppliesAreAccountedFor = false;
			} else if ((supplies.get(dripTorchIndex).getQuantity()
					/ supplies.get(fuelIndex).getQuantity()) < oneOverTen) {
				allSuppliesAreAccountedFor = false;
			} else if ((supplies.get(fireSwatterIndex).getQuantity() / acres) < oneOverTen) {
				allSuppliesAreAccountedFor = false;
			} else if (supplies.get(backpackPumpIndex).getQuantity() < 1.0) {
				allSuppliesAreAccountedFor = false;
			} else if (supplies.get(dozerIndex).getQuantity() < 1.0) {
				allSuppliesAreAccountedFor = false;
			}
		} catch (Exception notAllSuppliesListed) {
			allSuppliesAreAccountedFor = false;
		}
		if (allSuppliesAreAccountedFor) {
			return true;
		} else {
			return false;
		}
	}

	static public BurnDetermination determineAllNonHeadOrBlacklineFires(BurnPlan burnPlan) {
		Weather weather = burnPlan.getDay().getWeather();
		boolean redFlagConditionsPreventBurn;

		try {
			redFlagConditionsPreventBurn = checkRedFlagConditions(burnPlan.getDay().getWeather(), burnPlan.getDay());

			if (redFlagConditionsPreventBurn || burnPlan.getDay().isOutdoorBuringBanned()) {
				return BurnDetermination.BURNING_PROHIBITED;
			}
			if (weather.getTemperature() > 80) {
				return BurnDetermination.NOT_RECOMMENDED_TEMPERATURE;
			}
			if (weather.getWindSpeed() > 20) {
				return BurnDetermination.NOT_RECOMMENDED_WIND;
			}

			boolean hasRequiredSupplies = checkSupplies(burnPlan.getSupplies(), burnPlan.getAcresToBeBurned());
			Date twoDaysLater = burnPlan.getCurrentDay();
			Date fiveDaysLater = burnPlan.getCurrentDay();
			LocalDate localDateTwoDaysLater = twoDaysLater.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					.plusDays(2);
			LocalDate localDateFiveDaysLater = fiveDaysLater.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					.plusDays(5);
			LocalDate dayOfBurn = burnPlan.getDay().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			boolean withinDateRange = dayOfBurn.isAfter(localDateTwoDaysLater)
					&& dayOfBurn.isBefore(localDateFiveDaysLater);
			if (!hasRequiredSupplies || burnPlan.getDay().getWeather().isColdFrontApproaching()
					|| (burnPlan.getFuelType() == FuelType.HEAVY && weather.getRainChance() > 50) || !withinDateRange) {
				return BurnDetermination.NOT_RECOMMENDED_OTHER;
			}
			if (weather.getRainChance() > 50 && weather.getRainAmount() > 10) {
				return BurnDetermination.NOT_RECOMMENDED_OTHER;
			}
			boolean humidityIsAcceptable = weather.getHumidity() >= 20;
			if (humidityIsAcceptable) {
				return BurnDetermination.ACCEPTABLE;
			}
		} catch (Exception anInputWasNotInput) {
			return BurnDetermination.INDETERMINATE;
		}
		return BurnDetermination.NOT_RECOMMENDED_OTHER;
	}

	static public BurnDetermination determineBlackLines(BurnPlan burnPlan) {
		Weather weather = burnPlan.getDay().getWeather();

		try {
			boolean redFlagConditionsPreventBurn = checkRedFlagConditions(burnPlan.getDay().getWeather(),
					burnPlan.getDay());
			if (redFlagConditionsPreventBurn || burnPlan.getDay().isOutdoorBuringBanned()) {
				return BurnDetermination.BURNING_PROHIBITED;
			}

			if (weather.getTemperature() > 65 || weather.getTemperature() < 35) {
				return BurnDetermination.NOT_RECOMMENDED_TEMPERATURE;
			}
			if (weather.getWindSpeed() > 10) {
				return BurnDetermination.NOT_RECOMMENDED_WIND;
			}
			boolean hasRequiredSupplies = checkSupplies(burnPlan.getSupplies(), burnPlan.getAcresToBeBurned());
			Date twoDaysLater = burnPlan.getCurrentDay();
			Date fiveDaysLater = burnPlan.getCurrentDay();
			LocalDate localDateTwoDaysLater = twoDaysLater.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					.plusDays(2);
			LocalDate localDateFiveDaysLater = fiveDaysLater.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					.plusDays(5);
			LocalDate dayOfBurn = burnPlan.getDay().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			boolean withinDateRange = dayOfBurn.isAfter(localDateTwoDaysLater)
					&& dayOfBurn.isBefore(localDateFiveDaysLater);
			if (!hasRequiredSupplies || burnPlan.getDay().getWeather().isColdFrontApproaching()
					|| (burnPlan.getFuelType() == FuelType.HEAVY && weather.getRainChance() > 50) || !withinDateRange) {
				return BurnDetermination.NOT_RECOMMENDED_OTHER;
			}
			if (burnPlan.getDayBeforeFire().getWeather().getRainChance() > 50 && burnPlan.getDayBeforeFire().getWeather().getRainAmount() > 10) {
				return BurnDetermination.NOT_RECOMMENDED_OTHER;
			}
			boolean temperatureIsDesired = weather.getTemperature() <= 60 && weather.getTemperature() >= 40;
			boolean humidityIsDesired = weather.getHumidity() >= 40 && weather.getHumidity() <= 60;
			boolean windSpeedIsDesired = weather.getWindSpeed() <= 8;
			LocalTime midMorningStart = LocalTime.of(10, 0, 0, 0);
			LocalTime lateAfternoonStart = LocalTime.of(16, 0, 0, 0);
			Instant instant = Instant.ofEpochMilli(burnPlan.getDay().getDate().getTime());
			LocalTime timeOfBurnDate = LocalDateTime.ofInstant(instant, ZoneId.of("America/Chicago")).toLocalTime();
			boolean timeIsDesired = timeOfBurnDate.isAfter(midMorningStart)
					&& timeOfBurnDate.isBefore(lateAfternoonStart);
			boolean widthOfBlackLinesDesired = true;
			if (burnPlan.isBlackLineVolatile() == null || burnPlan.isBlackLineVolatile()) {
				widthOfBlackLinesDesired = burnPlan.getWidthOfBlacklines() >= 500;
			} else {
				widthOfBlackLinesDesired = burnPlan.getWidthOfBlacklines() >= 100;
			}
			if (temperatureIsDesired && humidityIsDesired && windSpeedIsDesired && timeIsDesired
					&& widthOfBlackLinesDesired) {
				return BurnDetermination.DESIRED;
			}
			boolean humidityIsAcceptable = weather.getHumidity() >= 30 && weather.getHumidity() <= 65;

			if (humidityIsAcceptable && timeIsDesired) {
				return BurnDetermination.ACCEPTABLE;
			}
			return BurnDetermination.NOT_RECOMMENDED_OTHER;
		} catch (Exception anInputWasNotInput) {
			return BurnDetermination.INDETERMINATE;
		}
	}

	static public BurnDetermination determineHeadFires(BurnPlan burnPlan) {
		Weather weather = burnPlan.getDay().getWeather();
		try {
			boolean redFlagConditionsPreventBurn = checkRedFlagConditions(burnPlan.getDay().getWeather(),
					burnPlan.getDay());
			if (redFlagConditionsPreventBurn || burnPlan.getDay().isOutdoorBuringBanned()) {
				return BurnDetermination.BURNING_PROHIBITED;
			}
			if (weather.getTemperature() > 85 || weather.getTemperature() < 60) {
				return BurnDetermination.NOT_RECOMMENDED_TEMPERATURE;
			}
			if (weather.getWindSpeed() > 20) {
				return BurnDetermination.NOT_RECOMMENDED_WIND;
			}
			boolean hasRequiredSupplies = checkSupplies(burnPlan.getSupplies(), burnPlan.getAcresToBeBurned());
			Date twoDaysLater = burnPlan.getCurrentDay();
			Date fiveDaysLater = burnPlan.getCurrentDay();
			LocalDate localDateTwoDaysLater = twoDaysLater.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					.plusDays(2);
			LocalDate localDateFiveDaysLater = fiveDaysLater.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					.plusDays(5);
			LocalDate dayOfBurn = burnPlan.getDay().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			boolean withinDateRange = dayOfBurn.isAfter(localDateTwoDaysLater)
					&& dayOfBurn.isBefore(localDateFiveDaysLater);
			if (!hasRequiredSupplies || burnPlan.getDay().getWeather().isColdFrontApproaching()
					|| (burnPlan.getFuelType() == FuelType.HEAVY && weather.getRainChance() > 50) || !withinDateRange) {
				return BurnDetermination.NOT_RECOMMENDED_OTHER;
			}
			if (weather.getRainChance() > 50 && weather.getRainAmount() > 10) {
				return BurnDetermination.NOT_RECOMMENDED_OTHER;
			}
			boolean temperatureIsDesired = weather.getTemperature() <= 80 && weather.getTemperature() >= 70;
			boolean humidityIsDesired = weather.getHumidity() >= 25 && weather.getHumidity() <= 40;
			boolean windSpeedIsDesired = weather.getWindSpeed() >= 8 && weather.getWindSpeed() <= 15;
			LocalTime midMorningStart = LocalTime.of(10, 0, 0, 0);
			LocalTime lateAfternoonStart = LocalTime.of(16, 0, 0, 0);
			Instant instant = Instant.ofEpochMilli(burnPlan.getDay().getDate().getTime());
			LocalTime timeOfBurnDate = LocalDateTime.ofInstant(instant, ZoneId.of("America/Chicago")).toLocalTime();
			boolean timeIsDesired = timeOfBurnDate.isAfter(midMorningStart)
					&& timeOfBurnDate.isBefore(lateAfternoonStart);
			boolean windDirectionIsDesired = weather.getWindDirection() == Direction.SOUTHWEST;
			if (temperatureIsDesired && humidityIsDesired && windSpeedIsDesired && timeIsDesired
					&& windDirectionIsDesired) {
				return BurnDetermination.DESIRED;
			}
			boolean humidityIsAcceptable = weather.getHumidity() >= 20 && weather.getHumidity() <= 45;
			boolean windSpeedIsAcceptable = weather.getWindSpeed() >= 5 && weather.getWindSpeed() <= 20;
			boolean windDirectionIsAcceptable = (weather.getWindDirection() == Direction.SOUTHWEST
					|| weather.getWindDirection() == Direction.SOUTH || weather.getWindDirection() == Direction.WEST);
			if (windDirectionIsAcceptable && humidityIsAcceptable && windSpeedIsAcceptable && timeIsDesired) {
				return BurnDetermination.ACCEPTABLE;
			}
			if (!windDirectionIsAcceptable || weather.getWindSpeed() < 5) {
				return BurnDetermination.NOT_RECOMMENDED_WIND;
			}
			return BurnDetermination.NOT_RECOMMENDED_OTHER;
		} catch (Exception anInputWasNotInput) {
			return BurnDetermination.INDETERMINATE;
		}
	}

	public static BurnDetermination evaluate(BurnPlan burnPlan) {

		switch (burnPlan.getFirePattern()) {
		case BLACK_LINES:
			return determineBlackLines(burnPlan);
		case HEADFIRES:
			return determineHeadFires(burnPlan);
		default:
			return determineAllNonHeadOrBlacklineFires(burnPlan);
		}
	}
}
