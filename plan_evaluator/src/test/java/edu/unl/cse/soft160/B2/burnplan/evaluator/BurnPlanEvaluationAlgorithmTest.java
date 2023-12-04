package edu.unl.cse.soft160.B2.burnplan.evaluator;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class BurnPlanEvaluationAlgorithmTest {

	
	

@Test
public void testCheckRedFlagConditions_AllMet() {
	Weather weather = new Weather(21.0, Direction.NORTH, 19.0, 45.0, 51.0, 11.0, true, 81.0);
	Day day = new Day(new Date(), weather, true);
	assertTrue("all red flag conditions met", BurnPlanEvaluationAlgorithm.checkRedFlagConditions(weather, day));
}
@Test
public void testCheckRedFlagConditions_NoneMet() {
	Weather weather = new Weather(19.0, Direction.NORTH, 21.0, 45.0, 49.0, 9.0, false, 79.0);
	Day day = new Day(new Date(), weather, false);
	assertFalse("no red flag conditions met", BurnPlanEvaluationAlgorithm.checkRedFlagConditions(weather, day));
}




//Supplies tests
@Test
public void testCheckSupplies_SufficientSupplies() {
	List<Supply> supplies = new ArrayList<>();
	supplies.add(new Supply("pumper", 10.0, 20.0, "units", FuelType.LIGHT));
	assertTrue("all supplies are sufficient", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
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
	assertTrue("sufficient pumper supply", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
}
@Test
public void testCheckSuppliesPumperIsInsufficient() {
	List<Supply> supplies = Arrays.asList(new Supply("pumper", 0.1, 5.0, "units", FuelType.LIGHT));
	assertFalse("insufficient pumper supply", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
}
@Test
public void testCheckSuppliesFireStartingFuelIsSufficient() {
	List<Supply> supplies = Arrays.asList(new Supply("fire starting fuel", 20.0, 40.0, "gallons", FuelType.LIGHT));
	assertTrue("sufficient firestarting fuel supply", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
}
@Test
public void testCheckSuppliesFireStartinFuelIsInsufficient() {
	List<Supply> supplies = Arrays.asList(new Supply("fire starting fuel", 5.0, 40.0, "gallons", FuelType.LIGHT));
	assertFalse("insufficient firestarting fuel supply", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
}
@Test
public void testCheckSuppliesNullSuppliesList() {
	assertFalse("null supplies list", BurnPlanEvaluationAlgorithm.checkSupplies(null, 100));
}

@Test
public void testCheckSuppliesEmptySuppliesList() {
	assertFalse("empty supplies list", BurnPlanEvaluationAlgorithm.checkSupplies(new ArrayList<>(), 100));
}



@Test
public void testDetermineAllNonHeadOrBlacklineFires_Acceptable() {
	BurnPlan burnPlan = createBurnPlan(FirePattern.CONTROL_LINES, 70.0, 15.0, false);
	assertEquals("the conditions are acceptable", BurnDetermination.ACCEPTABLE, BurnPlanEvaluationAlgorithm.determineAllNonHeadOrBlacklineFires(burnPlan));
}
private BurnPlan createBurnPlan(FirePattern controlLines, double d, double e, boolean b) {
	// TODO Auto-generated method stub
	return null;
}



}
