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
	static void printPlanOutput(BurnPlan burnPlan, BurnDetermination planEvaluation) {
		System.out.print("Rachel will add this tommorrow!");
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
		
		if (data == null) {
			System.err.println("Unable to get data from OpenWeather");
			System.exit(1);
		}
		
		// get user input
		String[] inputPrompts = { "What is the planed date for the burn (YYYY, MM, DD): ",
				"Is Burning banned for the planed day (true/false): ", "What is the latitude for the burn: ",
				"What is the longitude for the burn: ", "What Fuel Type is going to be used (Light/Heavy): ",
				"What Fire Pattern is going to be used (Headfire/Black_lines): ",
				"If using Black_lines what is the width (0 if NOT using Black_lines): ",
				"If using Black_lines is the fuel volatile (true/false if NOT using Black_lines): ",
				"How many acres are to be burned: " };
		int prompt = 0;

		List<String> inputs = new ArrayList<>();
		String input = "";
		boolean haveAllInputs = false;

		System.out.println("If at any point you wish to close the program type exit in one of the prompts");
		do {
			input = "";
			input = getInput(inputPrompts[prompt], scanner);
			if (input != "") {
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
		scanner.close();

		try {
			String[] planedDateStrs = inputs.get(0).split(",");
			Date currentDay = new Date();
			Date dayBeforePlanedBurnDate = new Calendar.Builder().setDate(Integer.valueOf(planedDateStrs[0]),
					Integer.valueOf(planedDateStrs[1]), Integer.valueOf(planedDateStrs[2]) - 1).setTimeOfDay(0, 0, 0)
					.build().getTime();
			Date dayOfPlanedBurnDate = new Calendar.Builder().setDate(Integer.valueOf(planedDateStrs[0]),
					Integer.valueOf(planedDateStrs[1]), Integer.valueOf(planedDateStrs[2])).setTimeOfDay(0, 0, 0)
					.build().getTime();

			Weather dayOfPlanedBurnWeather = new Weather(null, null, null, null, null, null, false, null);
			Weather dayBeforePlanedBurnWeather = new Weather(null, null, null, null, null, null, false, null);

			Day dayBeforePlanedBurn = new Day(dayBeforePlanedBurnDate, dayBeforePlanedBurnWeather, false);
			Day dayOfPlanedBurn = new Day(dayOfPlanedBurnDate, dayOfPlanedBurnWeather,
					Boolean.parseBoolean(inputs.get(1)));


			List<Supply> supplies = new ArrayList<>(Arrays.asList(new Supply(null, null, null, null, null)));

			BurnPlan burnPlan = new BurnPlan(dayOfPlanedBurn, currentDay, Double.valueOf(inputs.get(2)),
					Double.valueOf(inputs.get(3)), FuelType.valueOf(inputs.get(4).toUpperCase()),
					FirePattern.valueOf(inputs.get(5).toUpperCase()), Integer.valueOf(inputs.get(6)),
					Boolean.parseBoolean(inputs.get(7)), Integer.valueOf(inputs.get(8)), supplies);
			BurnDetermination planEvaluation = BurnPlanEvaluationAlgorithm.evaluate(burnPlan);
			printPlanOutput(burnPlan,planEvaluation);
			// BurnPlanEvaluationAlgorithm.evaluate(burnPlan);
		} catch (NumberFormatException e) {
			System.err.println("At least one input that required a number was not a valid number \nIf you meant for one of the true/false prompts to be true make sure to enter true, any other response will be seen as false");
			System.err.println();
			System.err.println("Make sure to enter Fuel type and Fire pattern exactly as shown in the prompt");
		}

	}
}
