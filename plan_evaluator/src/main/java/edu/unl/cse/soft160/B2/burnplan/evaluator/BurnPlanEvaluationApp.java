package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class BurnPlanEvaluationApp {
	
	private static String getInput(String msg, Scanner scanner) {
		System.out.print(msg);
		return scanner.nextLine();
	}
	
	
	public static void main(String[] args) {
		String[] inputPrompts = {
				"What is the planed date for the burn (YYYY, MM, DD): ",
				"Is Burning banned for the planed day (true/false): ",
				"What is the latitude for the burn: ",
				"What is the longitude for the burn: ",
				"What Fuel Type is going to be used (Light/Heavy): ",
				"What Fire Pattern is going to be used (Headfire/Black_lines): ",
				"If using Black_lines what is the width (0 if NOT using Black_lines): ",
				"If using Black_lines is the fuel volatile (true/false if NOT using Black_lines): ",
				"How many acres are to be burned: "
				};
		int prompt = 0;
		
		List<String> inputs = new ArrayList<>();
		String input = "";
		boolean haveAllInputs = false;
		
		Scanner scanner = new Scanner(System.in);
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
		} while (!haveAllInputs);
		scanner.close();
		
		try {
			String[] planedDateStrs = inputs.get(0).split(",");
			Date currentDay = new Date();
			Date dayBeforePlanedBurnDate = new Calendar.Builder().setDate(Integer.valueOf(planedDateStrs[0]), Integer.valueOf(planedDateStrs[1]), Integer.valueOf(planedDateStrs[2]) -1).setTimeOfDay(0, 0, 0).build().getTime();
			Date dayOfPlanedBurnDate = new Calendar.Builder().setDate(Integer.valueOf(planedDateStrs[0]), Integer.valueOf(planedDateStrs[1]), Integer.valueOf(planedDateStrs[2])).setTimeOfDay(0, 0, 0).build().getTime();
			
			Weather dayOfPlanedBurnWeather = new Weather(null, null, null, null, null, null, false, null);
			Weather dayBeforePlanedBurnWeather = new Weather(null, null, null, null, null, null, false, null);
			
			Day dayBeforePlanedBurn = new Day(dayBeforePlanedBurnDate, dayBeforePlanedBurnWeather, false, null);
			Day dayOfPlanedBurn = new Day(dayOfPlanedBurnDate, dayOfPlanedBurnWeather, Boolean.parseBoolean(inputs.get(1)), null);

			List<Supply> supplies = new ArrayList<>(Arrays.asList(new Supply(null, null, null, null, null)));

			BurnPlan burnPlan = new BurnPlan(dayOfPlanedBurn, currentDay, Double.valueOf(inputs.get(2)), Double.valueOf(inputs.get(3)),
					FuelType.valueOf(inputs.get(4).toUpperCase()), FirePattern.valueOf(inputs.get(5).toUpperCase()),
					Integer.valueOf(inputs.get(6)), Boolean.parseBoolean(inputs.get(7)), Integer.valueOf(inputs.get(8)), supplies);
			//BurnPlanEvaluationAlgorithm.evaluate(burnPlan);
		} catch (NumberFormatException e) {
			System.out.println("At least one input that required a number was not a valid number \nIf you meant for one of the true/false prompts to be true make sure to enter true, \nany other response will be seen as false");
		}
	}
}
