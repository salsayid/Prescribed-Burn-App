package edu.unl.cse.soft160.B2.burnplan.evaluator;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class BurnPlanEvaluationApp {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("What is the planed date for the burn (YYYY, DD, MM): ");
		String planedDateOfBurn = scanner.nextLine();
		System.out.print("");
		
		
		
		
		Date currentDay = new Date();
		Date dayOfPlanedBurnDate = new Date();
		Date dayBeforePlanedBurnDate = dayOfPlanedBurnDate;
		dayBeforePlanedBurnDate.setTime(dayOfPlanedBurnDate.getTime() - 86400);
		
		Weather dayOfPlanedBurnWeather = new Weather(null, null, null, null, null, null, false, null);
		Weather dayBeforePlanedBurnWeather = new Weather(null, null, null, null, null, null, false, null);
		
		Day dayBeforePlanedBurn = new Day(dayBeforePlanedBurnDate, dayBeforePlanedBurnWeather, false, null, null);
		Day dayOfPlanedBurn = new Day(dayOfPlanedBurnDate, dayOfPlanedBurnWeather, false, null, null);
		
		List<Supply> supplies = new ArrayList<>(Arrays.asList(
				new Supply(null, null, null, null, null)
				));
		
		BurnPlan burnPlan = new BurnPlan(dayOfPlanedBurn, null, null, null, null, supplies, null, null, null, currentDay);
	}
}
