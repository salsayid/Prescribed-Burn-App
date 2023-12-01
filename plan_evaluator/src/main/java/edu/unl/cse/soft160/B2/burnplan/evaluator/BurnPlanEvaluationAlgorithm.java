package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.unl.cse.soft160.json_connections.connection.RestConnection;
import edu.unl.cse.soft160.json_connections.connector.OpenWeatherConnector;

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
			if (supply.getName() == "pumper") {
				pumperIndex = indexNumber;
			} else if (supply.getName() == "fire-starting fuel") {
				fuelIndex = indexNumber;
			} else if (supply.getName() == "drip torches") {
				dripTorchIndex = indexNumber;
			} else if (supply.getName() == "rakes" || supply.getName() == "fire swatters") {
				fireSwatterIndex = indexNumber;
			} else if (supply.getName() == "backpack pump") {
				backpackPumpIndex = indexNumber;
			} else if (supply.getName() == "dozer") {
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
		} catch (Exception anInputWasNotInput) {
			return BurnDetermination.INDETERMINATE;
		}
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
		boolean withinDateRange = burnPlan.getDay().getDate().isAfter(burnPlan.getCurrentDay().plusDays(2))
				&& burnPlan.getDay().getDate().isBefore(burnPlan.getCurrentDay().plusDays(5));
		if (!hasRequiredSupplies || burnPlan.getDay().getWeather().isColdFrontApproaching()
				|| (burnPlan.getFuelType() == FuelType.HEAVY && weather.getRainChance() > 50) || !withinDateRange) {
			return BurnDetermination.NOT_RECOMMENDED_OTHER;
		}
		if (weather.getRainChance() > 50 && weather.getRainAmount() > 10) {
			return BurnDetermination.NOT_RECOMMENDED_OTHER;
		}
		boolean humidityIsAcceptable = weather.getRelativeHumidity() >= 20;
		if (humidityIsAcceptable) {
			return BurnDetermination.ACCEPTABLE;
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
		} catch (Exception anInputWasNotInput) {
			return BurnDetermination.INDETERMINATE;
		}

		if (weather.getTemperature() > 65 || weather.getTemperature() < 35) {
			return BurnDetermination.NOT_RECOMMENDED_TEMPERATURE;
		}
		if (weather.getWindSpeed() > 10) {
			return BurnDetermination.NOT_RECOMMENDED_WIND;
		}
		boolean hasRequiredSupplies = checkSupplies(burnPlan.getSupplies(), burnPlan.getAcresToBeBurned());
		boolean withinDateRange = burnPlan.getDay().getDate().isAfter(burnPlan.getCurrentDay().plusDays(2))
				&& burnPlan.getDay().getDate().isBefore(burnPlan.getCurrentDay().plusDays(5));
		if (!hasRequiredSupplies || burnPlan.getDay().getWeather().isColdFrontApproaching()
				|| (burnPlan.getFuelType() == FuelType.HEAVY && weather.getRainChance() > 50) || !withinDateRange) {
			return BurnDetermination.NOT_RECOMMENDED_OTHER;
		}
		if (weather.getRainChance() > 50 && weather.getRainAmount() > 10) {
			return BurnDetermination.NOT_RECOMMENDED_OTHER;
		}
		boolean temperatureIsDesired = weather.getTemperature() <= 60 && weather.getTemperature() >= 40;
		boolean humidityIsDesired = weather.getHumidity() >= 40 && weather.getHumidity() <= 60;
		boolean windSpeedIsDesired = weather.getWindSpeed() <= 8;
		LocalTime midMorningStart = LocalTime.of(10, 0, 0, 0);
		LocalTime lateAfternoonStart = LocalTime.of(16, 0, 0, 0);
		boolean timeIsDesired = burnPlan.getDay().getTimeOfDay().isAfter(midMorningStart)
				&& burnPlan.getDay().getTimeOfDay().isBefore(lateAfternoonStart);
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
	}

	static public BurnDetermination determineHeadFires(BurnPlan burnPlan) {
		Weather weather = burnPlan.getDay().getWeather();
		boolean notEnoughData = false;
		try {
			boolean redFlagConditionsPreventBurn = checkRedFlagConditions(burnPlan.getDay().getWeather(),
					burnPlan.getDay());
			if (redFlagConditionsPreventBurn || burnPlan.getDay().isOutdoorBuringBanned()) {
				return BurnDetermination.BURNING_PROHIBITED;
			}
		} catch (Exception anInputWasNotInput) {
			notEnoughData = true;
		}
		if (!notEnoughData) {
			if (weather.getTemperature() > 85 || weather.getTemperature() < 60) {
				return BurnDetermination.NOT_RECOMMENDED_TEMPERATURE;
			}
			if (weather.getWindSpeed() > 20) {
				return BurnDetermination.NOT_RECOMMENDED_WIND;
			}
			boolean hasRequiredSupplies = checkSupplies(burnPlan.getSupplies(), burnPlan.getAcresToBeBurned());
			boolean withinDateRange = burnPlan.getDay().getDate().isAfter(burnPlan.getCurrentDay().plusDays(2))
					&& burnPlan.getDay().getDate().isBefore(burnPlan.getCurrentDay().plusDays(5));
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
			LocalTime midDayStart = LocalTime.of(12, 0, 0, 0);
			LocalTime lateAfternoonStart = LocalTime.of(16, 0, 0, 0);
			boolean timeIsDesired = burnPlan.getDay().getTimeOfDay().isAfter(midDayStart)
					&& burnPlan.getDay().getTimeOfDay().isBefore(lateAfternoonStart);
			boolean windDirectionIsDesired = weather.getWindDirection() == Direction.SOUTHWEST;
			if (temperatureIsDesired && humidityIsDesired && windSpeedIsDesired && timeIsDesired
					&& windDirectionIsDesired) {
				return BurnDetermination.DESIRED;
			}
			boolean humidityIsAcceptable = weather.getHumidity() >= 20 && weather.getHumidity() <= 45;
			boolean windSpeedIsAcceptable = weather.getWindSpeed() >= 5 && weather.getWindSpeed() <= 20;
			boolean windDirectionIsAcceptable = weather.getWindDirection() == Direction.SOUTHWEST
					|| weather.getWindDirection() == Direction.SOUTH || weather.getWindDirection() == Direction.WEST;
			if (windDirectionIsAcceptable && humidityIsAcceptable && windSpeedIsAcceptable && timeIsDesired) {
				return BurnDetermination.ACCEPTABLE;
			}
			if (!windDirectionIsAcceptable || weather.getWindSpeed() < 5) {
				return BurnDetermination.NOT_RECOMMENDED_WIND;
			}
			return BurnDetermination.NOT_RECOMMENDED_OTHER;
		} else {
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

	// determines the type of openweather date to get from the user
	private static String getDataSet(Scanner scanner) {
		List<String> dataSets = new ArrayList<>(OpenWeatherConnector.allowableDataSets);
		for (int i = 0; i < dataSets.size(); ++i) {
			System.out.println((i + 1) + ". " + dataSets.get(i));
		}
		System.out.print(System.lineSeparator() + "Please enter the desired data set: ");
		int choice = scanner.nextInt();
		scanner.nextLine();

		return dataSets.get(choice - 1);
	}

	// calls the openweather api to get the weather data
	private static String getData(OpenWeatherConnector weather, String dataSet, Instant now, Scanner scanner) {
		String query;
		switch (dataSet) {
		case "weather":
		case "forecast":
			query = "zip=68588";
			break;
		case "onecall":
		case "air_pollution":
		case "air_pollution/forecast":
			query = "lat=40.81506358&lon=-96.7048613";
			break;
		case "air_pollution/history":
			Instant yesterday = now.minus(1, ChronoUnit.DAYS);
			Instant twoDaysAgo = now.minus(2, ChronoUnit.DAYS);
			query = "lat=40.81506358&lon=-96.7048613&start=" + twoDaysAgo.getEpochSecond() + "&end="
					+ yesterday.getEpochSecond();
			break;
		default:
			System.err.println("The " + dataSet + " dataset is not currently supported.");
			query = "";
		}

		if (!query.equals("")) {
			System.out.print("Enter query, or press the ENTER key to accept the example query (" + query + "): ");
			String userQuery = scanner.nextLine();
			if (!userQuery.equals("")) {
				System.out.println("   " + "*".repeat(76));
				System.out.println("   ***   The following example output strings assume default units.         ***");
				System.out.println("   ***   If you specified other units, the values will be correct, but      ***");
				System.out.println("   ***   the stated units will be the defaults, not your specified units.   ***");
				System.out.println("   " + "*".repeat(76));
				query = userQuery;
			}
		}

		String data = null;
		if (!query.equals("")) {
			System.out.println("Requesting data at " + now);
			try {
				data = weather.retrieveData(query);
			} catch (IOException ioException) {
				System.err.println("IO Exception: " + ioException.getClass());
				System.err.println("\t" + ioException.getMessage());
				System.err.println("Caused by: " + ioException.getCause());
				if (ioException.getCause() != null) {
					System.err.println("\t" + ioException.getCause().getMessage());
				}
			}
		} else {
			System.err.println("Not requesting data at " + now + " due to empty query.");
		}
		return data;
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Instant now = Instant.now();
		String apiKey = null;
		try {
			apiKey = RestConnection.getApiKey("openweathermap");
		} catch (IOException ioException) {
			System.err.println("IO Exception: " + ioException.getClass());
			System.err.println("\t" + ioException.getMessage());
			System.err.println("Caused by: " + ioException.getCause());
			System.err.println("\t" + ioException.getCause().getMessage());
			System.exit(1);
		}
		String dataSet = getDataSet(scanner);
		OpenWeatherConnector openWeather = new OpenWeatherConnector(dataSet, apiKey);
		String data = getData(openWeather, dataSet, now, scanner);
	}
}
