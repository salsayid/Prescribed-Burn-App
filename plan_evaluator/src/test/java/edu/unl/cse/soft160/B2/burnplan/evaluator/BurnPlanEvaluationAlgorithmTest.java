package edu.unl.cse.soft160.B2.burnplan.evaluator;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class BurnPlanEvaluationAlgorithmTest {

	
//determine head fires tests
@Test
public void testHeadFires_RedFlagPreventsBurn() {
	Weather weather = new Weather(25.0, Direction.SOUTH, 10.0, 15.0, 60.0, 11.0, true, 90.0);
	Day day = new Day(new Date(), weather, true);
	List<Supply> supplies = createDefaultSupplies();
	BurnPlan burnPlan = new BurnPlan(day, new Date(), 40.81506358, -96.7048613, FuelType.LIGHT, FirePattern.HEADFIRES, 100, false, 100, supplies);
	assertEquals(BurnDetermination.BURNING_PROHIBITED, BurnPlanEvaluationAlgorithm.determineHeadFires(burnPlan));
}

@Test
public void testHeadFires_WindSpeedNotSuitable() {
	Weather weather = new Weather(21.0, Direction.SOUTHWEST, 30.0, 35.0, 20.0, 0.0, false, 70.0);
	Day day = new Day(new Date(), weather, false);
	List<Supply> supplies = createDefaultSupplies();
	BurnPlan burnPlan = new BurnPlan(day, new Date(), 40.81506358, -96.7048613, FuelType.LIGHT, FirePattern.HEADFIRES, 100, false, 100, supplies);
	assertEquals(BurnDetermination.NOT_RECOMMENDED_WIND, BurnPlanEvaluationAlgorithm.determineHeadFires(burnPlan));
}

@Test
public void  testHeadFires_ColdFrontComing() {
	Calendar calendar = Calendar.getInstance();
    calendar.set(2023, Calendar.JANUARY, 15, 0, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    Date testDate = calendar.getTime();
    Weather weather = new Weather(10.0, Direction.SOUTHWEST, 40.0, 45.0, 10.0, 0.0, true, 75.0);
    Day day = new Day(testDate, weather, false);
    List<Supply> supplies = Arrays.asList(new Supply("pumper", 2.0, 5.0, "units", FuelType.LIGHT));
    BurnPlan burnPlan = new BurnPlan(day, testDate, 40.81506358, -96.7048613, FuelType.LIGHT, FirePattern.HEADFIRES, 100, false, 100, supplies);
    assertEquals(BurnDetermination.NOT_RECOMMENDED_OTHER, BurnPlanEvaluationAlgorithm.determineHeadFires(burnPlan));
}
	
@Test
public void testHeadFires_MissingData() {
	Weather weather = new Weather(null, null, null, null, null, null, false, null);
	Day day = new Day(new Date(), weather, false);
	BurnPlan burnPlan = new BurnPlan(day, new Date(), null, null, null, FirePattern.HEADFIRES, null, false, null ,null);
	assertEquals(BurnDetermination.INDETERMINATE, BurnPlanEvaluationAlgorithm.determineHeadFires(burnPlan));
}

//red flag conditions tests
@Test
public void testCheckRedFlagConditions_AllMet() {
	Weather weather = new Weather(21.0, Direction.NORTH, 19.0, 45.0, 51.0, 11.0, true, 81.0);
	Day day = new Day(new Date(), weather, true);
	equals("all red flag conditions met", BurnPlanEvaluationAlgorithm.checkRedFlagConditions(weather, day));
}
@Test
public void testCheckRedFlagConditions_NoneMet() {
	Weather weather = new Weather(19.0, Direction.NORTH, 21.0, 45.0, 49.0, 9.0, false, 79.0);
	Day day = new Day(new Date(), weather, false);
	assertFalse("no red flag conditions met", BurnPlanEvaluationAlgorithm.checkRedFlagConditions(weather, day));
}

@Test
public void testRedFlagConditionsWindSpeedsExceed() {
    Weather weather = new Weather(21.0, Direction.NORTH, 30.0, 30.0, 10.0, 0.1, false, 70.0);
    Day day = new Day(new Date(), weather, false);
    equals(BurnPlanEvaluationAlgorithm.checkRedFlagConditions(weather, day));
}

@Test
public void testRedFlagConditionsLowHumidity() {
    Weather weather = new Weather(10.0, Direction.NORTH, 19.0, 19.0, 10.0, 0.1, false, 70.0);
    Day day = new Day(new Date(), weather, false);
    equals(BurnPlanEvaluationAlgorithm.checkRedFlagConditions(weather, day));
}

@Test
public void testRedFlagConditionsHighTemperature() {
    Weather weather = new Weather(10.0, Direction.NORTH, 30.0, 30.0, 10.0, 0.1, false, 81.0);
    Day day = new Day(new Date(), weather, false);
    equals(BurnPlanEvaluationAlgorithm.checkRedFlagConditions(weather, day));
}

@Test
public void testRedFlagConditionsHighRainChanceAndHighRainAmount() {
    Weather weather = new Weather(10.0, Direction.NORTH, 30.0, 30.0, 51.0, 11.0, false, 70.0);
    Day day = new Day(new Date(), weather, false);
    equals(BurnPlanEvaluationAlgorithm.checkRedFlagConditions(weather, day));
}

@Test
public void testRedFlagConditionsColdFrontComing() {
    Weather weather = new Weather(10.0, Direction.NORTH, 30.0, 30.0, 10.0, 0.1, true, 70.0);
    Day day = new Day(new Date(), weather, false);
    equals(BurnPlanEvaluationAlgorithm.checkRedFlagConditions(weather, day));
}

//Black Line Tests


@Test
public void testDetermineBlackLines_BurningNotAllowedCauseOfRedFlag() {
	Weather weather = new Weather(25.0, Direction.SOUTHWEST, 15.0, 10.0, 60.0, 11.0, true, 85.0);
	Day day = new Day(new Date(), weather, true);
	List<Supply> supplies = createDefaultSupplies();
	BurnPlan burnPlan = new BurnPlan(day, new Date(), 40.81506358, -96.7048613, FuelType.HEAVY, FirePattern.BLACK_LINES, 500, true, 100, supplies);
	assertEquals(BurnDetermination.BURNING_PROHIBITED, BurnPlanEvaluationAlgorithm.determineBlackLines(burnPlan));
}
@Test
public void testDetermineBlackLines_NotRecommendedCauseOfTemperatures() {
	Weather weather = new Weather(5.0, Direction.NORTHWEST, 45.0, 30.0, 10.0, 0.1, false, 34.0);
	Day day = new Day(new Date(), weather, false);
	List<Supply> supplies = createDefaultSupplies();
	BurnPlan burnPlan = new BurnPlan(day, new Date(), 40.81506358, -96.7048613, FuelType.LIGHT, FirePattern.BLACK_LINES, 500, true, 100, supplies);
	assertEquals(BurnDetermination.NOT_RECOMMENDED_TEMPERATURE, BurnPlanEvaluationAlgorithm.determineBlackLines(burnPlan));
}
@Test
public void testDetermineBlackLines_NotRecommendedCauseOfWind() {
	Weather weather = new Weather(11.0, Direction.NORTHWEST, 45.0, 30.0, 10.0, 0.1, false, 60.0);
	Day day = new Day(new Date(), weather, false);
	List<Supply> supplies = createDefaultSupplies();
	BurnPlan burnPlan = new BurnPlan(day, new Date(), 40.81506358, -96.7048613, FuelType.LIGHT, FirePattern.BLACK_LINES, 500, true, 100, supplies); 
	assertEquals(BurnDetermination.NOT_RECOMMENDED_WIND, BurnPlanEvaluationAlgorithm.determineBlackLines(burnPlan));
}
private List<Supply> createDefaultSupplies() {
	return null;
}


//Supplies tests
@Test
public void testCheckSupplies_SufficientSupplies() {
	List<Supply> supplies = new ArrayList<>();
	supplies.add(new Supply("pumper", 10.0, 20.0, "units", FuelType.LIGHT));
	equals("all supplies are sufficient", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
}
@Test
public void testCheckSupplies_InsufficentSupplies() {
	List<Supply> supplies = new ArrayList<>();
	supplies.add(new Supply("pumper", 1.0, 5.0, "units", FuelType.LIGHT));
	assertFalse("supplies are insufficient", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
}
@Test
public void testCheckSuppliesPumperIsSufficient() {
	List<Supply> supplies = Arrays.asList(new Supply("pumper", 2.0, 5.0, "units", FuelType.LIGHT));
	equals("sufficient pumper supply", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
}
private void equals(String string, boolean checkSupplies) {
	// TODO Auto-generated method stub	
}

@Test
public void testCheckSuppliesPumperIsInsufficient() {
	List<Supply> supplies = Arrays.asList(new Supply("pumper", 0.1, 5.0, "units", FuelType.LIGHT));
	assertFalse("insufficient pumper supply", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
}
@Test
public void testCheckSuppliesFireStartingFuelIsSufficient() {
	List<Supply> supplies = Arrays.asList(new Supply("fire starting fuel", 20.0, 40.0, "gallons", FuelType.LIGHT));
	equals("sufficient firestarting fuel supply", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
}
@Test
public void testCheckSuppliesFireStartinFuelIsInsufficient() {
	List<Supply> supplies = Arrays.asList(new Supply("fire starting fuel", 5.0, 40.0, "gallons", FuelType.LIGHT));
	assertFalse("insufficient firestarting fuel supply", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
}

@Test
public void testCheckSuppliesEmptySuppliesList() {
	assertFalse("empty supplies list", BurnPlanEvaluationAlgorithm.checkSupplies(new ArrayList<>(), 100));
}



}
