package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.util.Date;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import edu.unl.cse.soft160.json_connections.connection.RestConnection;
import edu.unl.cse.soft160.json_connections.connector.OpenWeatherConnector;

public class BurnPlanEvaluationApp {
	private static String getInput(String msg, Scanner scanner) {
		System.out.print(msg);
		return scanner.nextLine();
	}

	// determines the type of openweather data set
	static String getDataSet(Scanner scanner) {
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

	static void printSuppliesOutput(List<Supply> supplies, BurnPlan burnPlan) {
		// for what the user actually has available for the burn
		String suppliesName = " ";
		for (int supplyNumber = 0; supplyNumber < supplies.size(); supplyNumber++) {
			suppliesName = suppliesName + " " + supplies.get(supplyNumber).getName();
		}
		System.out.print("Supplies available: " + suppliesName + "\n");
		System.out.println(
				"The following are the required supplies, if you have the right amount, they will be followed by the word true.");
		System.out.println("If you do not have enough of an item, it will be followed by the word false.");
		// for whether or not the user has the required supplies
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
		// catches any missing or lacking supplies
		try {
			System.out.print("Do the pumpers meet the required supply?: "
					+ !((supplies.get(pumperIndex).getQuantity() / burnPlan.getAcresToBeBurned()) < 0.0125) + "\n");
		} catch (Exception itemMissing) {
			System.out.print("Do the pumpers meet the required supply?: " + "false" + "\n");
		}
		System.out.println("You need at least " + (burnPlan.getAcresToBeBurned() * 0.0125) + " pumper(s).");
		try {
			System.out.print("Does the fire starting fuel meet the required supply?: "
					+ !((supplies.get(fuelIndex).getQuantity() / burnPlan.getAcresToBeBurned()) < 0.1) + "\n");
		} catch (Exception itemMissing) {
			System.out.print("Does the fire starting fuel meet the required supply?: " + "false" + "\n");
		}
		System.out.println(
				"You need at least " + (burnPlan.getAcresToBeBurned() * 0.1) + " gallons of fire starting fuel.");
		try {
			System.out.print("Do the drip torches meet the required supply?: "
					+ !((supplies.get(dripTorchIndex).getQuantity() / supplies.get(fuelIndex).getQuantity()) < 0.1)
					+ "\n");
		} catch (Exception itemMissing) {
			System.out.print("Do the drip torches meet the required supply?: " + "false" + "\n");
		}
		System.out.println("You need at least " + (supplies.get(fuelIndex).getQuantity() * 0.1) + " drip torch(es).");
		try {
			System.out.print("Do the rakes or fire swatters meet the required supply?: "
					+ !((supplies.get(fireSwatterIndex).getQuantity() / burnPlan.getAcresToBeBurned()) < 0.1) + "\n");
		} catch (Exception itemMissing) {
			System.out.print("Do the rakes or fire swatters meet the required supply?: " + "false" + "\n");
		}
		System.out.println("You need at least " + (burnPlan.getAcresToBeBurned() * 0.1) + " rakes or fireSwatters(s).");

		try {
			System.out.print("Is there at least one backpack pump?: "
					+ (supplies.get(backpackPumpIndex).getQuantity() > 0) + "\n");
		} catch (Exception itemMissing) {
			System.out.print("Is there at least one backpack pump?: " + "false" + "\n");
		}
		try {
			System.out.print("Is there at least one dozer?: " + (supplies.get(dozerIndex).getQuantity() > 0) + "\n");
		} catch (Exception itemMissing) {
			System.out.print("Is there at least one dozer?: " + "false" + "\n");
		}
	}

	static void printWeatherForecastInformation(FirePattern fireType, Weather weather) {
		switch (fireType) {
		case BLACK_LINES:
			if (weather.getTemperature() == null) {
				System.out.println("Temperature: unknown");
			} else {
				System.out.println("Temperature: " + weather.getTemperature());
			}
			System.out.println("Acceptable temperature range: 35-65 degrees");
			if (weather.getHumidity() == null) {
				System.out.println("Humidity: unknown");
			} else {
				System.out.println("Humidity: " + weather.getHumidity());
			}
			System.out.println("Acceptable humidity range: 30-65 precent");
			if (weather.getWindSpeed() == null) {
				System.out.println("Wind speed: unknown");
			} else {
				System.out.println("WindSpeed: " + weather.getWindSpeed());
			}
			System.out.println("Acceptable wind speed range: 0-10 mph");
			if (weather.getWindDirection() == null) {
				System.out.println("Wind direction: unknown");
			} else {
				System.out.println("Wind direction: " + weather.getWindDirection());
			}
			System.out.println("Wind direction does not affect Black Line fires");
			if (weather.getRelativeHumidity() == null) {
				System.out.println("Relative Humidity: unknown");
			} else {
				System.out.println("Relative Humidity: " + weather.getRelativeHumidity());
			}
			System.out.println("Acceptable relative humidity: above 20 percent");
			if (weather.isColdFrontApproaching() == null) {
				System.out.println("Cold front to pass within twelve hours: unknown");
			} else {
				System.out.println("Cold front to pass within twelve hours: " + weather.isColdFrontApproaching());
			}
			System.out.println("A cold front passing withing twelve hours is not acceptable");
			if (weather.getRainChance() == null) {
				System.out.println("Rain chance: unknown");
			} else {
				System.out.println("Rain chance: " + weather.getRainChance());
			}
			if (weather.getRainAmount() == null) {
				System.out.println("Rain Amount: unknown");
			} else {
				System.out.println("Rain amount: " + weather.getRainAmount());
			}
			System.out.println(
					"If the rain chance exceeds 50 percent and the anticipated rain amount meets at least 10 inches,\n then there is considered Bad Weather and burning is not reccommended.");

		case HEADFIRES:
			if (weather.getTemperature() == null) {
				System.out.println("Temperature: unknown");
			} else {
				System.out.println("Temperature: " + weather.getTemperature());
			}
			System.out.println("Acceptable temperature range: 60-85 degrees");
			if (weather.getHumidity() == null) {
				System.out.println("Humidity: unknown");
			} else {
				System.out.println("Humidity: " + weather.getHumidity());
			}
			System.out.println("Acceptable humidity range: 20-45 precent");
			if (weather.getWindSpeed() == null) {
				System.out.println("Wind speed: unknown");
			} else {
				System.out.println("WindSpeed: " + weather.getWindSpeed());
			}
			System.out.println("Acceptable wind speed range: 5-20 mph");
			if (weather.getWindDirection() == null) {
				System.out.println("Wind direction: unknown");
			} else {
				System.out.println("Wind direction: " + weather.getWindDirection());
			}
			System.out.println("Acceptable wind directions: any direction between and including South and West");
			if (weather.getRelativeHumidity() == null) {
				System.out.println("Relative Humidity: unknown");
			} else {
				System.out.println("Relative Humidity: " + weather.getRelativeHumidity());
			}
			System.out.println("Acceptable relative humidity: above 20 percent");
			if (weather.isColdFrontApproaching() == null) {
				System.out.println("Cold front to pass within twelve hours: unknown");
			} else {
				System.out.println("Cold front to pass within twelve hours: " + weather.isColdFrontApproaching());
			}
			System.out.println("A cold front passing withing twelve hours is not acceptable");
			if (weather.getRainChance() == null) {
				System.out.println("Rain chance: unknown");
			} else {
				System.out.println("Rain chance: " + weather.getRainChance());
			}
			if (weather.getRainAmount() == null) {
				System.out.println("Rain Amount: unknown");
			} else {
				System.out.println("Rain amount: " + weather.getRainAmount());
			}
			System.out.println(
					"If the rain chance exceeds 50 percent and the anticipated rain amount meets at least 10 inches,\n then there is considered Bad Weather and burning is not reccommended.");

		default:
			if (weather.getTemperature() == null) {
				System.out.println("Temperature: unknown");
			} else {
				System.out.println("Temperature: " + weather.getTemperature());
			}
			System.out.println("Acceptable temperature range: below 80 degrees");
			if (weather.getHumidity() == null) {
				System.out.println("Humidity: unknown");
			} else {
				System.out.println("Humidity: " + weather.getHumidity());
			}
			if (weather.getWindSpeed() == null) {
				System.out.println("Wind speed: unknown");
			} else {
				System.out.println("WindSpeed: " + weather.getWindSpeed());
			}
			System.out.println("Acceptable wind speed range: 0-20 mph");
			if (weather.getWindDirection() == null) {
				System.out.println("Wind direction: unknown");
			} else {
				System.out.println("Wind direction: " + weather.getWindDirection());
			}
			if (weather.getRelativeHumidity() == null) {
				System.out.println("Relative Humidity: unknown");
			} else {
				System.out.println("Relative Humidity: " + weather.getRelativeHumidity());
			}
			System.out.println("Acceptable relative humidity: above 20 percent");
			if (weather.isColdFrontApproaching() == null) {
				System.out.println("Cold front to pass within twelve hours: unknown");
			} else {
				System.out.println("Cold front to pass within twelve hours: " + weather.isColdFrontApproaching());
			}
			System.out.println("A cold front passing withing twelve hours is not acceptable");
			if (weather.getRainChance() == null) {
				System.out.println("Rain chance: unknown");
			} else {
				System.out.println("Rain chance: " + weather.getRainChance());
			}
			if (weather.getRainAmount() == null) {
				System.out.println("Rain Amount: unknown");
			} else {
				System.out.println("Rain amount: " + weather.getRainAmount());
			}
			System.out.println(
					"If the rain chance exceeds 50 percent and the anticipated rain amount meets at least 10 inches,\n then there is considered Bad Weather and burning is not reccommended.");

		}
	}

	static void printMissingInformation(BurnPlan burnPlan) {
		System.out.println("Missing Information:");
		if (burnPlan.getDay().getWeather().getTemperature() == null) {
			System.out.println("Temperature");
		}
		if (burnPlan.getDay().getWeather().getWindSpeed() == null) {
			System.out.println("Wind Speed");
		}
		if (burnPlan.getDay().getWeather().getRelativeHumidity() == null) {
			System.out.println("Relative Humidity");
		}
		if (burnPlan.getDay().getWeather().getHumidity() == null) {
			System.out.println("Humidity");
		}
		if (burnPlan.getDay().getWeather().getWindDirection() == null) {
			System.out.println("Wind Direction");
		}
		if (burnPlan.getDay().getWeather().isColdFrontApproaching() == null) {
			System.out.println("Whether or not a cold front will pass within 12 hours");
		}
		if (burnPlan.getDay().getWeather().getRainChance() == null) {
			System.out.println("Rain Chance");
		}
		if (burnPlan.getDay().getWeather().getRainAmount() == null) {
			System.out.println("Rain Amount");
		}
		if (burnPlan.getDay().isOutdoorBuringBanned() == null) {
			System.out.println("Whether or not outdoor burning is banned");
		}
		if (burnPlan.getAcresToBeBurned() == null) {
			System.out.println("Acres to be burned");
		}
		if (burnPlan.getFirePattern() == null) {
			System.out.println("Type of fire");
		}
		if (burnPlan.getFuelType() == null) {
			System.out.println("Fuel type");
		}
		if (burnPlan.getLongitude() == null) {
			System.out.println("Longitude");
		}
		if (burnPlan.getLatitude() == null) {
			System.out.println("Latitude");
		}
	}

	static void printPlanOutput(BurnPlan burnPlan, BurnDetermination planEvaluation) {
		System.out.print("Report generated on: " + burnPlan.getCurrentDay().toString() + "\n");
		System.out.println("Intended burn date: " + burnPlan.getDay().getDate());
		System.out.print(
				"Location: " + burnPlan.getLatitude() + " Latitude, and " + burnPlan.getLongitude() + " Longitude\n");
		System.out.print("Size of land to be burned: " + burnPlan.getAcresToBeBurned() + "\n");
		System.out.print("Land fuel type: " + burnPlan.getFuelType() + "\n");
		System.out.print("Type of fire: " + burnPlan.getFirePattern() + "\n");
		printSuppliesOutput(burnPlan.getSupplies(), burnPlan);
		System.out.print("Weather Forecast\n");
		printWeatherForecastInformation(burnPlan.getFirePattern(), burnPlan.getDay().getWeather());
		System.out.println("Burn determination: " + planEvaluation);
		// lists missing information if INDETERMINATE is returned
		if (planEvaluation == BurnDetermination.INDETERMINATE) {
			printMissingInformation(burnPlan);
		}
		if (planEvaluation == BurnDetermination.BURNING_PROHIBITED) {
			System.out.println("Outdoor burning is currently prohibited by the governor of Tennesse.");
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// get user input
		String[] inputPrompts = { "What is the planned date for the burn (YYYY, MM, DD): ",
				"What time is the burn for the planned day (hour in military time): ",
				"Is Burning banned for the planned day (true/false): ", 
				"What is the latitude for the burn: ",
				"What is the longitude for the burn: ", 
				"What Fuel Type is going to be used (Light/Heavy): ",
				"What Fire Pattern is going to be used (Headfires/Black_lines): ",
				"If using Black_lines what is the width (0 if NOT using Black_lines): ",
				"If using Black_lines is the fuel volatile (true/false if NOT using Black_lines): ",
				"Is there a cold front approaching (true/false): ",
				"How many acres are to be burned: " };
		int prompt = 0;

		List<String> inputs = new ArrayList<>();
		String input = "";
		boolean haveAllInputs = false;

		System.out.println("If at any point you wish to close the program type exit for one of the prompts");
		do {
			input = "";
			input = getInput(inputPrompts[prompt], scanner);
			if (!input.equals("")) {
				inputs.add(input);
				prompt++;
				if (prompt == inputPrompts.length) {
					haveAllInputs = true;
				}
			}
			if (input.equals("exit")) {
				System.exit(1);
			}
		} while (!haveAllInputs);
		// inputs start at index 10 for supplies
		String[] supplyPrompts = {"What is the capacity for each pumper: ",
				"How many pumpers do you have: ",
				"What unit do the pumpers belong to: ",
				"What is the capacity for a barrel of fire starting fluid: ",
				"How many barrels of fire starting fluid do you have: ",
				"What unit do the fire starting fluid belongs to: ", "How many drip torches do you have: ",
				"What unit do the drip torches belong to: ", "how many rakes or fire swatters do you have: ",
				"What unit do the rakes or fire swatters belong to: ", "How many backback pumps do you have: ",
				"What unit do the backpack pumps belong to: ", "How many dozers do you have: ",
				"What unit do the dozers belong to: " };

		prompt = 0;
		haveAllInputs = false;
		input = "";

		System.out.println("Please enter the supplies needed");
		do {
			input = "";
			input = getInput(supplyPrompts[prompt], scanner);
			if (!input.equals("")) {
				inputs.add(input);
				prompt++;
				if (prompt == supplyPrompts.length) {
					haveAllInputs = true;
				}
			}
			if (input.equals("exit")) {
				System.exit(1);
			}
		} while (!haveAllInputs);

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
		if (dataSet.equals("exit")) {
			System.exit(1);
		}

		OpenWeatherConnector openWeather;
		String data;
		
		System.out.print("Do you want to use a JSON file to read weather date? (yes or no): ");
		String useJson = scanner.next();		
		String jsonFileName;
		boolean isJsonTrue;
		
		if (useJson.toLowerCase().equals("yes")) {
			System.out.print("What is the name of the json file? (ex. my-file.json): ");
			jsonFileName = scanner.next();
			isJsonTrue = true;
		} else {
			jsonFileName = null;
			isJsonTrue = false;
		}

		if (isJsonTrue) {
			openWeather = new OpenWeatherConnector(dataSet);
			try {
				data = openWeather.retrieveData(jsonFileName);
			} catch (IOException error) {
				data = null;
				System.out.println(error);
			}
		} else {
			openWeather = new OpenWeatherConnector(dataSet, apiKey);
			data = getData(openWeather, dataSet, now, scanner);
		}

		if (data == null) {
			System.err.println("Unable to get data from OpenWeather");
			System.exit(1);
		}
		scanner.close();

		try {
			String[] planedDateStrs = inputs.get(0).split(",");
			Date currentDay = new Date();
			Date dayBeforePlanedBurnDate = new Calendar.Builder().setDate(Integer.valueOf(planedDateStrs[0].strip()),
					Integer.valueOf(planedDateStrs[1].strip()) - 1, Integer.valueOf(planedDateStrs[2].strip()))
					.setTimeOfDay(Integer.parseInt(inputs.get(1)), 0, 0).build().getTime();
			
			Date dayOfPlanedBurnDate = new Calendar.Builder().setDate(Integer.valueOf(planedDateStrs[0].strip()),
					Integer.valueOf(planedDateStrs[1].strip()), Integer.valueOf(planedDateStrs[2].strip()))
					.setTimeOfDay(Integer.parseInt(inputs.get(1)), 0, 0).build().getTime();
			
			Direction windDirection = Direction.NORTH;
			long openWeatherWindDirection = 0;
			if ((openWeatherWindDirection >= 0 && openWeatherWindDirection < 23) ||(openWeatherWindDirection <= 360 && openWeatherWindDirection > 337)) {
				windDirection = Direction.NORTH;
			} else if ((openWeatherWindDirection >= 23 && openWeatherWindDirection < 68)) {
				windDirection = Direction.NORTHEAST;
			} else if ((openWeatherWindDirection >= 68 && openWeatherWindDirection < 113)) {
				windDirection = Direction.EAST;
			} else if ((openWeatherWindDirection >= 113 && openWeatherWindDirection < 159)) {
				windDirection = Direction.SOUTHEAST;
			} else if ((openWeatherWindDirection >= 159 && openWeatherWindDirection < 203)) {
				windDirection = Direction.SOUTH;
			} else if ((openWeatherWindDirection >= 203 && openWeatherWindDirection < 249)) {
				windDirection = Direction.SOUTHWEST;
			} else if ((openWeatherWindDirection >= 249 && openWeatherWindDirection < 293)) {
				windDirection = Direction.WEST;
			} else if ((openWeatherWindDirection >= 293 && openWeatherWindDirection <= 337)) {
				windDirection = Direction.NORTHWEST;
			}

			Weather dayOfPlanedBurnWeather = new Weather(openWeather.getWindSpeed(dayOfPlanedBurnDate), windDirection, Double.valueOf(openWeather.getHumidity(dayOfPlanedBurnDate)), Double.valueOf(openWeather.getHumidity(dayOfPlanedBurnDate)), openWeather.getProbabilityOfPrecipitation(dayOfPlanedBurnDate), openWeather.getDailyRainfall(dayOfPlanedBurnDate), Boolean.valueOf(inputs.get(9)), openWeather.getTemperature(dayOfPlanedBurnDate));
			Weather dayBeforePlanedBurnWeather = new Weather(openWeather.getWindSpeed(dayBeforePlanedBurnDate), windDirection, Double.valueOf(openWeather.getHumidity(dayBeforePlanedBurnDate)), Double.valueOf(openWeather.getHumidity(dayBeforePlanedBurnDate)), openWeather.getProbabilityOfPrecipitation(dayBeforePlanedBurnDate), openWeather.getDailyRainfall(dayBeforePlanedBurnDate), Boolean.valueOf(inputs.get(9)), openWeather.getTemperature(dayBeforePlanedBurnDate));

			Day dayBeforePlanedBurn = new Day(dayBeforePlanedBurnDate, dayBeforePlanedBurnWeather, Boolean.valueOf(inputs.get(2)));
			Day dayOfPlanedBurn = new Day(dayOfPlanedBurnDate, dayOfPlanedBurnWeather,
					Boolean.valueOf(inputs.get(2)));

			List<Supply> supplies = new ArrayList<>(Arrays.asList(
					new Supply("pumper", Double.valueOf(inputs.get(12)), Double.valueOf(inputs.get(11)), inputs.get(13)),
					new Supply("fire-starting fuel", Double.valueOf(inputs.get(15)), Double.valueOf(inputs.get(14)), inputs.get(16)),
					new Supply("drip torches", Double.valueOf(inputs.get(17)), 0.0, inputs.get(18)),
					new Supply("rakes", Double.valueOf(inputs.get(19)), 0.0, inputs.get(20)),
					new Supply("backpack pump", Double.valueOf(inputs.get(21)), 0.0, inputs.get(22)),
					new Supply("dozer", Double.valueOf(inputs.get(23)), 0.0, inputs.get(24))));

			BurnPlan burnPlan = new BurnPlan(dayOfPlanedBurn, currentDay, dayBeforePlanedBurn, Double.valueOf(inputs.get(3)),
					Double.valueOf(inputs.get(4)), FuelType.valueOf(inputs.get(5).toUpperCase()),
					FirePattern.valueOf(inputs.get(6).toUpperCase()), Integer.valueOf(inputs.get(7)),
					Boolean.parseBoolean(inputs.get(8)), Integer.valueOf(inputs.get(10)), supplies);
			
			BurnDetermination planEvaluation = BurnPlanEvaluationAlgorithm.evaluate(burnPlan);
			printPlanOutput(burnPlan, planEvaluation);
		} catch (NumberFormatException e) {
			System.err.println(
					"At least one input that required a number was not a valid number \nIf you meant for one of the true/false prompts to be true make sure to enter true, any other response will be seen as false");
			System.err.println();
			System.err.println("Make sure to enter Fuel type and Fire pattern exactly as shown in the prompt");
		}

	}
}
