	Here is a link for an example README.md <https://canvas.unl.edu/courses/162910/files/15695789>

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
The REST connector must be in the project's build path during development (and the project's classpath?) when the app is run.

For development, JUnit 4 is required to run the project's unit tests.

## Building

[Building Info Goes Here]

## Running

For development, 'BurnPlanEvaluationAlgorithm.java' can be run in Eclipse by right-clicking on it in the "Package Explorer" and selecting "Run As" → "Java Application".

## Software Architecture

[Software Architecture Info Goes Here]


## Testing & Test Results

[Testing & Test Results Info Goes Here]
