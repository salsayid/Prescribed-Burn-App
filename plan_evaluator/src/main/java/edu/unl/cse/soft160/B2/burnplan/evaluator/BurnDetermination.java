package edu.unl.cse.soft160.B2.burnplan.evaluator;

public enum BurnDetermination {
	ACCEPTABLE,  //conditions are somewhat favorable and supplies meet minimum requirements
	DESIRED,  //conditions are highly favorable and supplies meet minimum requirements
	BURNING_PROHIBITED, //red flag conditions prohibit burning
	NOT_RECOMMENDED_TEMPERATURE, //conditions are unfavorable due to temperature forecast
	NOT_RECOMMENDED_WIND, //conditions are unfavorable due to wind forecast
	NOT_RECOMMENDED_OTHER, //conditions or supplies do not meet minimum requirements
	INDETERMINATE, //found too few data to make a determination 
}
