package edu.unl.cse.soft160.B2.burnplan.evaluator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class BurnPlanEvaluationAlgorithmTest {
	private Date createDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private Weather weather;
	private Day day;
	private List<Supply> supplies;
	private BurnPlan burnPlan;

//determine head fires tests

	@Test
	public void testHeadFires_RedFlagPreventsBurn() {
		Weather weather = new Weather(25.0, Direction.SOUTH, 10.0, 15.0, 60.0, 11.0, true, 90.0);
		Day day = new Day(new Date(), weather, true);
		List<Supply> supplies = createDefaultSupplies();
		BurnPlan burnPlan = new BurnPlan(day, new Date(), day, 40.81506358, -96.7048613, FuelType.LIGHT,
				FirePattern.HEADFIRES, 100, false, 100, supplies);
		assertEquals(BurnDetermination.BURNING_PROHIBITED, BurnPlanEvaluationAlgorithm.determineHeadFires(burnPlan));
	}

	@Test
	public void testHeadFires_WindSpeedNotSuitable() {
		Weather weather = new Weather(21.0, Direction.SOUTHWEST, 30.0, 35.0, 20.0, 0.0, false, 70.0);
		Day day = new Day(new Date(), weather, false);
		List<Supply> supplies = createDefaultSupplies();
		BurnPlan burnPlan = new BurnPlan(day, new Date(), day, 40.81506358, -96.7048613, FuelType.LIGHT,
				FirePattern.HEADFIRES, 100, false, 100, supplies);
		assertEquals(BurnDetermination.NOT_RECOMMENDED_WIND, BurnPlanEvaluationAlgorithm.determineHeadFires(burnPlan));
	}

	@Before
	public void setUp() {
		weather = new Weather(10.0, Direction.NORTH, 20.0, 30.0, 40.0, 0.1, false, 70.0);
		Date fireDate = new Date();
		new Day(fireDate, weather, false);
		new Day(new Date(fireDate.getTime() - 86400000), weather, false);
		Arrays.asList(new Supply("Pumper", 2.0, 2000.0, "gallons"));
	}

	@Test
	public void testHeadFires_ColdFrontComing() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2023, Calendar.JANUARY, 15, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date testDate = calendar.getTime();
		Weather weather = new Weather(10.0, Direction.SOUTHWEST, 40.0, 45.0, 10.0, 0.0, true, 75.0);
		Day day = new Day(testDate, weather, false);
		List<Supply> supplies = Arrays.asList(new Supply("pumper", 2.0, 5.0, "units"));
		BurnPlan burnPlan = new BurnPlan(day, testDate, day, 40.81506358, -96.7048613, FuelType.LIGHT,
				FirePattern.HEADFIRES, 100, false, 100, supplies);
		assertEquals(BurnDetermination.NOT_RECOMMENDED_OTHER, BurnPlanEvaluationAlgorithm.determineHeadFires(burnPlan));
	}

	@Test
	public void testHeadFires_MissingData() {
		Weather weather = new Weather(null, null, null, null, null, null, false, null);
		Day day = new Day(new Date(), weather, false);
		BurnPlan burnPlan = new BurnPlan(day, new Date(), null, null, null, null, FirePattern.HEADFIRES, null, false,
				null, null);
		assertEquals(BurnDetermination.INDETERMINATE, BurnPlanEvaluationAlgorithm.determineHeadFires(burnPlan));
	}

//red flag conditions tests

	@Test
	public void testCheckRedFlagConditions_AllMet() {
		equals();
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
		BurnPlan burnPlan = new BurnPlan(day, new Date(), day, 40.81506358, -96.7048613, FuelType.HEAVY,
				FirePattern.BLACK_LINES, 500, true, 100, supplies);
		assertEquals(BurnDetermination.BURNING_PROHIBITED, BurnPlanEvaluationAlgorithm.determineBlackLines(burnPlan));
	}

	@Test
	public void testDetermineBlackLines_NotRecommendedCauseOfTemperatures() {
		Weather weather = new Weather(5.0, Direction.NORTHWEST, 45.0, 30.0, 10.0, 0.1, false, 34.0);
		Day day = new Day(new Date(), weather, false);
		List<Supply> supplies = createDefaultSupplies();
		BurnPlan burnPlan = new BurnPlan(day, new Date(), day, 40.81506358, -96.7048613, FuelType.LIGHT,
				FirePattern.BLACK_LINES, 500, true, 100, supplies);
		assertEquals(BurnDetermination.NOT_RECOMMENDED_TEMPERATURE,
				BurnPlanEvaluationAlgorithm.determineBlackLines(burnPlan));
	}

	@Test
	public void testDetermineBlackLines_NotRecommendedCauseOfWind() {
		Weather weather = new Weather(11.0, Direction.NORTHWEST, 45.0, 30.0, 10.0, 0.1, false, 60.0);
		Day day = new Day(new Date(), weather, false);
		List<Supply> supplies = createDefaultSupplies();
		BurnPlan burnPlan = new BurnPlan(day, new Date(), day, 40.81506358, -96.7048613, FuelType.LIGHT,
				FirePattern.BLACK_LINES, 500, true, 100, supplies);
		assertEquals(BurnDetermination.NOT_RECOMMENDED_WIND, BurnPlanEvaluationAlgorithm.determineBlackLines(burnPlan));
	}

	private List<Supply> createDefaultSupplies() {
		return null;
	}

//Supplies tests
	@Test
	public void testCheckSupplies_SufficientSupplies() {
		List<Supply> supplies = new ArrayList<>();
		supplies.add(new Supply("pumper", 10.0, 20.0, "units"));
		equals();
	}

	@Test
	public void testCheckSupplies_InsufficentSupplies() {
		List<Supply> supplies = new ArrayList<>();
		supplies.add(new Supply("pumper", 1.0, 5.0, "units"));
		assertFalse("supplies are insufficient", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
	}

	@Test
	public void testCheckSuppliesPumperIsSufficient() {
		equals();
	}

	private void equals() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testCheckSuppliesPumperIsInsufficient() {
		List<Supply> supplies = Arrays.asList(new Supply("pumper", 0.1, 5.0, "units"));
		assertFalse("insufficient pumper supply", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
	}

	@Test
	public void testCheckSuppliesFireStartingFuelIsSufficient() {
		List<Supply> supplies = Arrays.asList(new Supply("fire starting fuel", 20.0, 40.0, "gallons"));
		equals();
	}

	@Test
	public void testCheckSuppliesFireStartinFuelIsInsufficient() {
		List<Supply> supplies = Arrays.asList(new Supply("fire starting fuel", 5.0, 40.0, "gallons"));
		assertFalse("insufficient firestarting fuel supply", BurnPlanEvaluationAlgorithm.checkSupplies(supplies, 100));
	}

	@Test
	public void testCheckSuppliesEmptySuppliesList() {
		assertFalse("empty supplies list", BurnPlanEvaluationAlgorithm.checkSupplies(new ArrayList<>(), 100));
	}

	@Test
	public void testEvaluateNonHeadOrBlacklineFires() {
		Day dayOfFire = new Day(createDate(2023, Calendar.JANUARY, 15),
				new Weather(10.0, Direction.NORTH, 30.0, 30.0, 10.0, 0.1, false, 70.0), false);
		List<Supply> supplies = Arrays.asList(new Supply("pumper", 2.0, 5.0, "units"));
		BurnPlan burnPlan = new BurnPlan(dayOfFire, createDate(2023, Calendar.JANUARY, 13), null, 40.0, -96.0,
				FuelType.LIGHT, FirePattern.FLANK_FIRING, 0, false, 100, supplies);

		BurnDetermination result = BurnPlanEvaluationAlgorithm.evaluate(burnPlan);
		assertEquals(BurnDetermination.NOT_RECOMMENDED_OTHER, result);
	}

	private Date createDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Test
	public void testEvaluateHeadFires() {
		Day dayOfFire = new Day(createDate(2023, Calendar.JANUARY, 15),
				new Weather(10.0, Direction.SOUTHWEST, 30.0, 35.0, 20.0, 0.0, false, 70.0), false);
		List<Supply> supplies = Arrays.asList(new Supply("pumper", 2.0, 5.0, "units"));
		BurnPlan burnPlan = new BurnPlan(dayOfFire, createDate(2023, Calendar.JANUARY, 13), null, 40.0, -96.0,
				FuelType.LIGHT, FirePattern.HEADFIRES, 0, false, 100, supplies);

		BurnDetermination result = BurnPlanEvaluationAlgorithm.evaluate(burnPlan);
		equals();
	}

	private void equals1() {
	}

	@Test
	public void testEvaluateBlackLines() {
		Day dayOfFire = new Day(createDate(2023, Calendar.JANUARY, 15),
				new Weather(5.0, Direction.NORTH, 50.0, 60.0, 10.0, 0.1, false, 55.0), false);
		List<Supply> supplies = Arrays.asList(new Supply("pumper", 2.0, 5.0, "units"));
		BurnPlan burnPlan = new BurnPlan(dayOfFire, createDate(2023, Calendar.JANUARY, 13), null, 40.0, -96.0,
				FuelType.HEAVY, FirePattern.BLACK_LINES, 500, true, 100, supplies);
		BurnDetermination result = BurnPlanEvaluationAlgorithm.evaluate(burnPlan);
		equals();
	}

	@Before
	public void setUp1() {
		weather = new Weather(10.0, Direction.NORTH, 30.0, 25.0, 45.0, 0.5, false, 75.0);
		day = new Day(new Date(), weather, false);
		supplies = Arrays.asList(new Supply("pumper", 2.0, 5.0, "units"));
		burnPlan = new BurnPlan(day, new Date(), null, 40.81506358, -96.7048613, FuelType.LIGHT,
				FirePattern.EDGE_FIRING, 0, false, 100, supplies);
	}

	@Test
	public void testBurningProhibitedByRedFlagAndBan() {
		day.setOutdoorBuringBanned(true);
		Weather highRiskWeather = new Weather(30.0, Direction.NORTH, 10.0, 10.0, 55.0, 0.5, true, 90.0);
		day.setWeather(highRiskWeather);
		burnPlan.setDay(day);
		assertEquals(BurnDetermination.BURNING_PROHIBITED,
				BurnPlanEvaluationAlgorithm.determineAllNonHeadOrBlacklineFires(burnPlan));
	}

	@Test
	public void testNotRecommendedDueToTemperature() {
		weather.setTemperature(85.0);
		burnPlan.setDay(day);
		assertEquals(BurnDetermination.NOT_RECOMMENDED_TEMPERATURE,
				BurnPlanEvaluationAlgorithm.determineAllNonHeadOrBlacklineFires(burnPlan));
	}

	@Test
	public void testNotRecommendedDueToWind() {
		weather.setWindSpeed(25.0);
		burnPlan.setDay(day);
		assertEquals(BurnDetermination.NOT_RECOMMENDED_WIND,
				BurnPlanEvaluationAlgorithm.determineAllNonHeadOrBlacklineFires(burnPlan));
	}

	@Test
	public void testNotRecommendedDueToLackOfSupplies() {
		supplies = Arrays.asList(new Supply("pumper", 0.1, 5.0, "units"));
		burnPlan.setSupplies(null, supplies);
		burnPlan.setDay(day);
		assertEquals(BurnDetermination.NOT_RECOMMENDED_OTHER,
				BurnPlanEvaluationAlgorithm.determineAllNonHeadOrBlacklineFires(burnPlan));
	}

	@Test
	public void testNotRecommendedDueToColdFront() {
		weather.setColdFrontApproaching(true);
		burnPlan.setDay(day);
		assertEquals(BurnDetermination.NOT_RECOMMENDED_OTHER,
				BurnPlanEvaluationAlgorithm.determineAllNonHeadOrBlacklineFires(burnPlan));
	}

	@Test
	public void testAcceptableDueToHumidity() {
		weather.setRelativeHumidity(30.0);
		burnPlan.setDay(day);
		equals(BurnDetermination.ACCEPTABLE);
	}

	@Test
	public void testIndeterminateDueToException() {
		burnPlan.setSupplies(null, supplies);
		equals(BurnDetermination.INDETERMINATE);
	}

	@Test
	public void testNotRecommendedDueToTimeRange() {
		Date currentDate = new Date();
		Date burnDate = new Date();
		day.setDate(burnDate);
		burnPlan.setDay(day);
		assertEquals(BurnDetermination.NOT_RECOMMENDED_OTHER,
				BurnPlanEvaluationAlgorithm.determineAllNonHeadOrBlacklineFires(burnPlan));
	}

//Season tests
	@Test
	public void testSummer() {
		Season season = Season.SUMMER;
		assertEquals(Season.SUMMER, season);
	}

	@Test
	public void testFall() {
		Season season = Season.FALL;
		assertEquals(Season.FALL, season);
	}

	@Test
	public void testWinter() {
		Season season = Season.WINTER;
		assertEquals(Season.WINTER, season);
	}

	@Test
	public void testSpring() {
		Season season = Season.SPRING;
		assertEquals(Season.SPRING, season);
	}

//supply tests

	@Test
	public void testConstructorAndGetters() {
		String name = "Water";
		Double quantity = 100.0;
		Double capacity = 200.0;
		String unit = "liters";
		Supply supply = new Supply(name, quantity, capacity, unit);
		assertEquals(name, supply.getName());
		assertEquals(quantity, supply.getQuantity());
		assertEquals(capacity, supply.getCapacity());
		assertEquals(unit, supply.getUnit());
	}

	@Test
	public void testSetters() {
		Supply supply = new Supply("Food", 50.0, 100.0, "grams");
		supply.setName("Rice");
		supply.setQuantity(75.0);
		supply.setCapacity(150.0);
		supply.setUnit("kilograms");
		assertEquals("Rice", supply.getName());
		equals(75.0);
		equals(150.0);
		assertEquals("kilograms", supply.getUnit());
	}

// remaining weather tests

	@Test
	public void testGetHumidity() {
		Double expectedHumidity = 60.0;
		Weather weather = new Weather(10.0, Direction.NORTH, expectedHumidity, 65.0, 20.0, 0.1, false, 75.0);
		Double actualHumidity = weather.getHumidity();
		assertEquals(expectedHumidity, actualHumidity);
	}

	@Test
	public void testSetWindDirection() {
		Weather weather = new Weather(10.0, Direction.NORTH, 60.0, 65.0, 20.0, 0.1, false, 75.0);
		weather.setWindDirection(Direction.SOUTH);
		assertEquals(Direction.SOUTH, weather.getWindDirection());
	}

	@Test
	public void testSetHumidity() {
		Weather weather = new Weather(10.0, Direction.NORTH, 60.0, 65.0, 20.0, 0.1, false, 75.0);
		weather.setHumidity(70.0);
		equals(70.0);
	}

	@Test
	public void testSetRainAmount() {
		Weather weather = new Weather(10.0, Direction.NORTH, 60.0, 65.0, 20.0, 0.1, false, 75.0);
		weather.setRainAmount(0.2);
		equals(0.2);
	}

	@Test
	public void testSetRainChance() {
		Weather weather = new Weather(10.0, Direction.NORTH, 60.0, 65.0, 20.0, 0.1, false, 75.0);
		weather.setRainChance(30.0);
		equals(30.0);
	}

// remaining burnPlan tests

	@Test
	public void testSetAcresToBeBurned() {
		BurnPlan burnPlan = new BurnPlan();
		burnPlan.setAcresToBeBurned(100);
		equals(100);
	}

	@Test
	public void testGetFuelType() {
		BurnPlan burnPlan = new BurnPlan();
		FuelType fuelType = FuelType.LIGHT;
		burnPlan.setFuelType(fuelType);
		assertEquals(fuelType, burnPlan.getFuelType());
	}

	@Test
	public void testSetFuelType() {
		BurnPlan burnPlan = new BurnPlan();
		FuelType fuelType = FuelType.HEAVY;
		burnPlan.setFuelType(fuelType);
		assertEquals(fuelType, burnPlan.getFuelType());
	}

	@Test
	public void testSetWidthOfBlacklines() {
		BurnPlan burnPlan = new BurnPlan();
		burnPlan.setWidthOfBlacklines(5);
		equals(5);
	}

	@Test
	public void testSetDayBeforeFire() {
		BurnPlan burnPlan = new BurnPlan();
		Day dayBeforeFire = new Day(new Date(), new Weather(null, null, null, null, null, null, false, null), false);
		burnPlan.setDayBeforeFire(dayBeforeFire);
		assertEquals(dayBeforeFire, burnPlan.getDayBeforeFire());
	}

	@Test
	public void testSetFirePattern() {
		BurnPlan burnPlan = new BurnPlan();
		FirePattern firePattern = FirePattern.CONTROL_LINES;
		burnPlan.setFirePattern(firePattern);
		assertEquals(firePattern, burnPlan.getFirePattern());
	}

	@Test
	public void testSetLatitude() {
		BurnPlan burnPlan = new BurnPlan();
		burnPlan.setLatitude(42.0);
		equals(42.0);
	}

	@Test
	public void testSetLongitude() {
		BurnPlan burnPlan = new BurnPlan();
		burnPlan.setLongitude(-97.0);
		equals(-97.0);
	}

	@Test
	public void testIsBlackLineVolatile() {
		BurnPlan burnPlan = new BurnPlan();
		burnPlan.setBlackLineVolatile(true);
		assertTrue(burnPlan.isBlackLineVolatile());
	}

	@Test
	public void testSetBlackLineVolatile() {
		BurnPlan burnPlan = new BurnPlan();
		burnPlan.setBlackLineVolatile(true);
		assertTrue(burnPlan.isBlackLineVolatile());
	}

	@Test
	public void testSetCurrentDay() {
		BurnPlan burnPlan = new BurnPlan();
		Date currentDate = new Date();
		burnPlan.setCurrentDay(currentDate);
		assertEquals(currentDate, burnPlan.getCurrentDay());
	}

	@Test
	public void testGetLongitude() {
		BurnPlan burnPlan = new BurnPlan();
		double expectedLongitude = -97.5;
		burnPlan.setLongitude(expectedLongitude);
		double actualLongitude = burnPlan.getLongitude();
		equals(expectedLongitude);
	}

	@Test
	public void testGetLatitude() {
		BurnPlan burnPlan = new BurnPlan();
		double expectedLatitude = 42.0;
		burnPlan.setLatitude(expectedLatitude);
		double actualLatitude = burnPlan.getLatitude();
		equals(expectedLatitude);
	}

	@Test
	public void testGetWidthOfBlacklines() {
		BurnPlan burnPlan = new BurnPlan();
		int expectedWidth = 10;
		burnPlan.setWidthOfBlacklines(expectedWidth);
		int actualWidth = burnPlan.getWidthOfBlacklines();
		assertEquals(expectedWidth, actualWidth);
	}

	@Test
	public void testDesiredBlackLinesNonVolatile() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.BLACK_LINES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 5;
		double humidity = 50;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 55;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.DESIRED;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testBlackLinesBadWeather() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.BLACK_LINES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 5;
		double humidity = 50;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 100;
		double rainAmount = 300;
		boolean isColdFrontApproaching = false;
		double temperature = 55;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testAcceptableBlackLinesVolatile() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = true;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.BLACK_LINES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 5;
		double humidity = 50;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 55;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.ACCEPTABLE;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testTooHighHumidityBlackLines() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = true;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.BLACK_LINES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 5;
		double humidity = 80;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 55;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testBlacklinesMissingData() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = true;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.BLACK_LINES;
		boolean isOutdoorBurningBanned = false;
		Double windSpeed = null;
		double humidity = 50;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 55;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.INDETERMINATE;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testBackFireAcceptable() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.BACKFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 5;
		double humidity = 50;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 55;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.ACCEPTABLE;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testBackFireBadWeather() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.BACKFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 5;
		double humidity = 50;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 100;
		double rainAmount = 200;
		boolean isColdFrontApproaching = false;
		double temperature = 55;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testBackFireTooEarlyInWeek() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 20).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.BLACK_LINES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 5;
		double humidity = 50;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 100;
		double rainAmount = 300;
		boolean isColdFrontApproaching = false;
		double temperature = 55;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testBackFireLowHumidity() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.BACKFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 5;
		double humidity = 2;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 55;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testHeadFiresDesired() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.DESIRED;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testHeadFiresAcceptable() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 84;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.ACCEPTABLE;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testHeadFireTooHotTemperature() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 300;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_TEMPERATURE;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testHeadFireFloodThePreviousDay() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 100;
		double rainAmount = 20000;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void HeadFireWithWestWind() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.WEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.ACCEPTABLE;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testHeadFiresWindWrong() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.NORTH;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_WIND;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testHeadFiresHumidityIsUnAcceptable() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 0;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testBackFireMissingInfo() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.BACKFIRES;
		boolean isOutdoorBurningBanned = false;
		Double windSpeed = null;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.INDETERMINATE;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testMissingAllSupplies() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 0.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 0.0, "gallon");
		Supply supply3 = new Supply("drip torches", 0.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 0.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 0.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 0.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testMissingSomeSupplies() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 200.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testMissingFireSwatters() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 0.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testMissingBackPackPump() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 0.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 1.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}

	@Test
	public void testMissingDozer() {

		double latitude = 20.0;
		double longitude = 20.8;
		boolean isBlackLineVolatile = false;
		int acresToBeBurned = 10;
		Date currentDayDate = new Calendar.Builder().setDate(2023, 11, 19).setTimeOfDay(0, 0, 0).build().getTime();
		Date dayBeforeFireDate = new Calendar.Builder().setDate(2023, 11, 22).setTimeOfDay(12, 0, 0).build().getTime();
		Date dayOfFireDate = new Calendar.Builder().setDate(2023, 11, 23).setTimeOfDay(12, 0, 0).build().getTime();
		FuelType fuelType = FuelType.LIGHT;
		Integer widthOfBlacklines = 101;
		FirePattern firePattern = FirePattern.HEADFIRES;
		boolean isOutdoorBurningBanned = false;
		double windSpeed = 10;
		double humidity = 35;
		double relativeHumidity = 50;
		Direction windDirection = Direction.SOUTHWEST;
		double rainChance = 0;
		double rainAmount = 0;
		boolean isColdFrontApproaching = false;
		double temperature = 75;
		Weather weather = new Weather(windSpeed, windDirection, humidity, relativeHumidity, rainChance, rainAmount,
				isColdFrontApproaching, temperature);
		Day dayOfFire = new Day(dayOfFireDate, weather, isOutdoorBurningBanned);
		Day dayBeforeFire = new Day(dayBeforeFireDate, weather, isOutdoorBurningBanned);

		// set up the supplies: name, quantity, capacity, unit
		Supply supply1 = new Supply("fire-starting fuel", 1.0, 20.0, "pumpers");
		Supply supply2 = new Supply("pumper", 1.0, 20.0, "gallon");
		Supply supply3 = new Supply("drip torches", 1.0, 20.0, "N/A");
		Supply supply4 = new Supply("rakes", 3.0, 20.0, "N/A");
		Supply supply5 = new Supply("backpack pump", 1.0, 20.0, "N/A");
		Supply supply6 = new Supply("dozer", 0.0, 20.0, "N/A");
		List<Supply> supplies = new ArrayList<Supply>();
		supplies.add(supply1);
		supplies.add(supply2);
		supplies.add(supply3);
		supplies.add(supply4);
		supplies.add(supply5);
		supplies.add(supply6);
		BurnPlan burnPlan = new BurnPlan(dayOfFire, currentDayDate, dayBeforeFire, latitude, longitude, fuelType,
				firePattern, widthOfBlacklines, isBlackLineVolatile, acresToBeBurned, supplies);
		BurnDetermination expectedResult = BurnDetermination.NOT_RECOMMENDED_OTHER;
		assertEquals(expectedResult, BurnPlanEvaluationAlgorithm.evaluate(burnPlan));
	}
}
