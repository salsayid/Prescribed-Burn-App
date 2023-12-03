package edu.unl.cse.soft160.B2.burnplan.evaluator;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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



}
