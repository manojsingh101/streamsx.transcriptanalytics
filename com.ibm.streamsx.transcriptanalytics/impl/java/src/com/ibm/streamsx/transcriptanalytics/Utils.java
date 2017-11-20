package com.ibm.streamsx.transcriptanalytics;

import java.util.List;

public class Utils {


	/**
	 * average computed only when the scores are >= 0, negative scores are not considered
	 * @param setDistances
	 * @return
	 */
	public static Double computeAverageSetDistance(List<Double> setDistances) {
	    double average = 0.0;
	    int numNonNegative = 0;
	    for (Double setDistance : setDistances) {
	        if (setDistance >= 0.0) {
	            average += setDistance;
	            numNonNegative++;
	        }
	    }
	
	    return average / numNonNegative;
	}

 
	/**
	 * Removes white spaces that are extraneous and returns a single white space separated string
	 *
	 * @param whiteSpaceStr
	 * @return
	 */
	public static String removeWhiteSpaces(String whiteSpaceStr) {
	    String[] splitString = whiteSpaceStr.split("\\s+");
	    if (splitString.length == 0) {
	        return "";
	    }
	    String retval = splitString[0];
	    for (int i = 1; i < splitString.length; i++) {
	        retval += " " + splitString[i];
	    }
	
	    return retval;
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String removeSpecialCharacters(String str) {
	    String retval = str;
	    retval = retval.replaceAll("\\.\\.\\.", "");
	    retval = retval.replaceAll("\\.\\.", "");
	    retval = retval.replaceAll("xxx", "");
	
	    retval = removeWhiteSpaces(retval);
	
	    return retval;
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
