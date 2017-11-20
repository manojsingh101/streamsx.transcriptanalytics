package com.ibm.streamsx.transcriptanalytics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DistanceModel {

	private String modelPath ;
	private  int minSetSize;
	private List<Set<String>> machineUtteranceSetList = new ArrayList<>();
	
	/**
	 * Constructor with path of the model file as argument
	 * 
	 * @param modelPath
	 */
	public DistanceModel(String modelPath, int minSetSize) throws Exception{
		this.modelPath = modelPath;
		this.minSetSize = minSetSize;
		loadModel();
	}
	
	/**
	 * Load the file and create a list of sets of words for each line in the file.
	 */
	private void loadModel() throws Exception{
		try(BufferedReader br = new BufferedReader(new FileReader(modelPath))){
			String line;
			while( (line = br.readLine()) !=null){
				machineUtteranceSetList.add(getCleanedSet(line));
			}
		}catch(Exception e){
			throw e;
		}
	}	
	
	/**
	 * Compute the distance of the passed text from the set of text in the model
	 * @param text
	 * @return
	 */
	public Double computeDistance(String text){		
		 Set<String> cleanedUtteranceSet = getCleanedSet(text);
		 return computeMaxSetDistance(cleanedUtteranceSet);
	}
 
	/**
	 * 
	 * @param utteranceSet
	 * @return
	 */
	private double computeMaxSetDistance(Set<String> utteranceSet) {
	    if (utteranceSet.size() <= minSetSize) {
	        return -1.0;
	    }

	    double maxSetDistance = Double.MIN_VALUE;
	    for (Set<String> machineUtterance : machineUtteranceSetList) {
	        double currSetDistance = computeSetDistance(utteranceSet, machineUtterance);
	        if (currSetDistance > maxSetDistance) {
	            maxSetDistance = currSetDistance;
	        }
	    }

	    return maxSetDistance;
	}

	/**
	 * 
	 * @param utteranceSet
	 * @param machineUtterance
	 * @return
	 */
	private double computeSetDistance(Set<String> utteranceSet, Set<String> machineUtterance) {
	    int intersectionCount = 0;
	    for (String utterance : utteranceSet) {
	        if (machineUtterance.contains(utterance)) {
	            intersectionCount++;
	        }
	    }
	    
	    return (intersectionCount * 1.0 / utteranceSet.size());
	}

	/**
	 * 
	 * @param utterance
	 * @return
	 */
	private Set<String> getCleanedSet(String utterance) {
	    String cleanedUtterance = Utils.removeSpecialCharacters(utterance.toLowerCase());
	    cleanedUtterance = cleanedUtterance.replaceAll("\\.", " ");
	    HashSet<String> result = new HashSet<>();
	    if (cleanedUtterance.trim().length() > 0) {
	        result.addAll(Arrays.asList(cleanedUtterance.split("\\s+")));
	    }
	    
	    return result;
	}

}
