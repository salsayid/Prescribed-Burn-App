	Link for an example README.md <https://canvas.unl.edu/courses/162910/files/15695789>

# Burn Plan Evaluation App

The Burn Plan Evaluation app connects to OpenWeather. Upon taking; the date, if burning is banned, the coordinates in Latitude and Longitude, fuel type, fire pattern, width, if the fuel is volatile, acres, and a list of supplies. The app reports if the user, upon matching specifications, has a desired or acceptable burn. If the burn is outside of specifications, the app shall result in not recommending the burn along with the reason for the recommendation. If there are multiple reasons for the burn plan resulting in not recommended, the program will prioritize a specific reason. Should the burn be banned, the app will result in telling the user that the ban is prohibited. If the inputs are missing any information, the app should result in informing the user that the result is indeterminate.

Project Status: **incomplete, not tested, no test failures, and no known bugs**

Authors:
*	Brett Johnson <bjohnson261@unl.edu>
*	Sayid Alsayid <salsayid2@unl.edu>
*	Rachael Solberg <rsolberg3@unl.edu>
*	Ethan Friedman <efriedman4@unl.edu>
*	Delanie Miller <dmiller77@unl.edu>
*	Connor Fausett <cfausett2@unl.edu>

## Dependencies

The burn plan evaluation app depends on [the OpenWeather REST connector](https://git.unl.edu/soft-core/soft-160/openweather-rest-and-file-connector).

The REST connector must be in the project's build path during development and the project's classpath when the app is run.

To run, the REST connector requires a running instance of OpenWeather either
on the same computer or accessible over a network.

For development, JUnit 4 is required to run the project's unit tests.

## Building

[Building Info Goes Here]

## Running

For development, 'BurnPlanEvaluationAlgorithm.java' can be run in Eclipse by right-clicking on it in the "Package Explorer" and selecting "Run As" → "Java Application".

## Software Architecture

[Software Architecture Info Goes Here]

## Testing & Test Results

[Description of tests Goes Here]

Unit tests are available in one class in the 'test' folder:
* 'BurnPlanEvaluationAlgorithmTest.java'

The unit tests require JUnit 4.

No UI testing, load/stress testing, security testing was performed for this milestone.

The app is [not] passing all unit-level and system-level tests. The unit-level tests
achieve 0% statement coverage of the code developed by our team.

# This Info Might be Changed or Gotten Rid of from Testing
The code [will] be tested through category-partition testing. These are the categories and partitions:
Ex. Category- partition 1, partition 2, ...
* latitude - an integer, null
* longitude- an integer, null
* isBlackLineVolatile- true, false, null
* acresToBeBurned- 0, more than 0, less than 0, null
* dayOfFire- within date range (based on currentDay), before date range (based on currentDay), after date range (based on currentDay), null
* supplies- no supplies, all the necessary supplies are present and the right amount, all the supplies are present but not the right amount (each individual type of supplies), one type of supplies is not present
* firePattern- HeadFire,BlackLines, BackFire/any other fire type
* fuelType- Light, Heavy, null
* widthOfBlacklines = 0, 100, 500, null
* windSpeed- Desireable wind speed for headfires, desireable wind speed for blacklines, acceptable wind speed for headfires, acceptable wind speed for blacklines, acceptable windspeed for all other fires, wind speed of 100 mph, null
* humidity- desired for headfires, desired for blacklines, acceptable for headfires, acceptable for blacklines, acceptable for all other fires, humidity in the range of a red flag condition, null
* relativeHumidity- <20, >=20, null
* windDirection- North, North East, East, South East, South, SouthWest, West, NorthWest, null (tested on head fires specifically)
* rainChance- <50, >=50, null
* rainAmount- <10, >=10, null
* isColdFrontApproaching- true, false, null/unknown
* temperature- desired for headfires, desired for blacklines, acceptable for headfires, acceptable for blacklines, acceptable for all other fires, temperature above 80 when the fire is not a black line or a head fire, null

