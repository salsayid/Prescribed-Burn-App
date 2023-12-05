	Link for an example README.md <https://canvas.unl.edu/courses/162910/files/15695789>

# Burn Plan Evaluation App

The Burn Plan Evaluation app connects to OpenWeather. Upon taking; the date, the coordinates in Latitude and Longitude, fuel type, fire pattern, width, if the fuel is volatile, acres, and a list of supplies. The app reports if the user, upon matching specifications, has a desired or acceptable burn. If the burn is outside of specifications, the app shall result in not recommending the burn along with the reason for the recommendation. If there are multiple reasons for the burn plan resulting in not recommended, the program will prioritize a specific reason. Should the burn be banned, the app will result in telling the user that the ban is prohibited. If the inputs are missing any information, the app should result in informing the user that the result is indeterminate.

Project Status: **feature-complete, fully tested, no test failures, and no known bugs**

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

The app can be built and packaged as a JAR by running `mvn package` from the `plan_evaluator` folder. The project can also be imported into Eclipse, in which case Eclipse will build the `.class` files, but not a JAR.

## Running

For development, 'BurnPlanEvaluationAlgorithm.java' can be run in Eclipse by right-clicking on it in the "Package Explorer" and selecting "Run As" → "Java Application".

The burn plan evaluation app will prompt the user for the date, latitude and longitude, acres to be burned, fuel type, and fire pattern. Depending on the type of fire pattern the app will also will also ask for the width, and if the fuel is volatile. Enter this information as requested. 

The app will then print to the user if the requested burn is possible. Under different outputs of being desired, acceptable, not recommended other, not recommended wind, not recommended temperature, or prohibited. If any information is left blank, the app will report indeterminate.

## Software Architecture

The Burn Plan Evaluation app is organized into three categories: The OpenWeather backend, an implementation of the burn plan evaluation algorithm, a command-line interface, and a test suite.

There is one package for the app: the "burnplan evaluator" package

1. The OpenWeather category of the package relates to one class: "BurnPlanEvaluationApp.java"

The class contains code to connect to OpenWeather, as well as code to make API calls for weather data
	
2. The implementation of the burn plan evaluation algorithm: 

The class "BurnPlanEvaluationAlgorithm.java" contains the code that determines whether to recommend burning or not based 
on user input and OpenWeather data. It contains the algorithm itself.
	
- The evaluate() method calls one of three methods based on the type of fire recieved from the user
- The determineBlackLines(), the determineHeadFires(), and the determineAllNonHeadOrBlacklineFires() methods contain 
conditional statements that determine the burn determination based on data recieved from the user and OpenWeather
- The checkSupplies() method determines whether the necessary supplies are present in order to burn
- The checkRedFlagConditions() method determines whether certain weather conditions are present that would
prohibit a safe burn
	
The class "BurnPlanEvaluationApp.java" is the main class. The previously mentioned classes have objects created 
for them and their methods called in this class.
	
- Objects of the Day, Weather, and Supply classes are created and data from OpeanWeather and the user passed into them
- An object of the "BurnPlanEvaluationAlgorithm.java" class is created and its evaluate() method is called
- Inputs from the user are determined
- Outputs are determined, formatted, and printed
		
The following classes contain fields that are related to one another that are initialized with data
from the user and OpenWeather through their object creation
	
- "Day.java"
- "Weather.java"
- "Supply.java"
		
The following are enums that contain information that are used by the Day, Weather, Supply, BurnPlanEvaluationApp, and 
BurnPlanEvaluationAlgorithm classes
	
- "BurnDetermination.java"
- "Direction.java"
- "FirePattern.java"
- "FuelType.java"
- "Season.java"
	
3. The command-line interface contains the class "BurnPlanEvaluationApp.java"

The class is shared by the algorithm implementation category
	
The class determines the inputs from the user, as well as the outputs that are printed
	
4. The test suite contains the classes "AppTest.java" and "BurnPlanEvaluationAlgorithmTest.java"

The code tests the algorithm in "BurnPlanEvaluationAlgorithm.java"

While not code, the Documentation folder contains a class diagram

## Testing & Test Results

The burn plan evaluation app has been verified with manual system tests of the app. The tests were designed using the specifications required to create category-partition testing. The test suites were made to achieve 100% code coverage. The categories and partitions chosen are documented in a seperate file we created called 'evaluationTestFrames.ods', which is found in the 'Documentation' folder.

Unit tests are available in one class in the 'test' folder:
* 'BurnPlanEvaluationAlgorithmTest.java'

The unit tests require JUnit 4.

No UI testing, load/stress testing, security testing was performed for this milestone.

The app is [not] passing all unit-level and system-level tests. The unit-level tests
achieve 0% statement coverage of the code developed by our team.