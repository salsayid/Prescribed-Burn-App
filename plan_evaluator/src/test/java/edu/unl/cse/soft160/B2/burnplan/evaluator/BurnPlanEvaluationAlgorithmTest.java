package edu.unl.cse.soft160.B2.burnplan.evaluator;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Date;
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


}
