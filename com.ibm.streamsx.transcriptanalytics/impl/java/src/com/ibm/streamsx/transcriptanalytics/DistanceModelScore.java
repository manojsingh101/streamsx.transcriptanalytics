/*******************************************************************************
 * Copyright (C) 2017 International Business Machines Corporation
 * All Rights Reserved
 *******************************************************************************/
package com.ibm.streamsx.transcriptanalytics;

import java.util.logging.Logger;

import com.ibm.streams.operator.Attribute;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamingInput;
import com.ibm.streams.operator.Tuple;
import com.ibm.streams.operator.TupleAttribute;
import com.ibm.streams.operator.logging.TraceLevel;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;

@PrimitiveOperator(description="This operator is used to compute distance of a text from model consisting of set of text. Distance represents how close the text is to the cluster or topic")
@InputPortSet(cardinality=1,description="This input port is required. The operator expects an attribute of type rstring that will be used as input distance scoring model.")
@OutputPortSet(cardinality=1,description="This output port is required. The operator passes through all attributes on the input port as-is to the output port. In addition, it expects an attribute called 'score' of type float64 '")
public class DistanceModelScore extends AbstractTranscriptAnalyticsOperator {

	
	private String modelPath;    
	TupleAttribute<Tuple,String> testDataAttr;
	private DistanceModel model ;
	private int minSetSize = 5;
	private static final String CLASS_NAME = DistanceModelScore.class.getName();
	
	/**
	 * Create a {@code Logger} specific to this class that will write to the SPL
	 * trace facility
	 */
	private static Logger tracer = Logger.getLogger(CLASS_NAME, "com.ibm.streamsx.transcriptanalytics.messages");

	@Parameter(name="testDataAttr",optional=false,description="The attribute which contains the text data which needs to be scored")
	public void setTestDataAttr(TupleAttribute<Tuple,String> testDataAttr) {
		this.testDataAttr = testDataAttr;
	}

	@Parameter(name="modelPath",optional=false,description="The path containing the persisted analytic model")
	public void setModelPath(String path) {
		this.modelPath = path;
	}
	
	@Parameter(name="minSetSize",optional=true, description="The min number of words in the set for which scoring would be done. default =5")
	public void setMinSetSize(int minSetSize) {
		this.minSetSize = minSetSize;
	}

	@Override
	public synchronized void initialize(OperatorContext context) throws Exception {
		super.initialize(context);
		
		init();
	}
	
	protected void init() throws Exception{
		try {
			tracer.log(TraceLevel.INFO,"TRACE_M_LOAD_MODEL_INIT", new Object[]{modelPath});
			model = new DistanceModel(modelPath, minSetSize);
		} catch (Exception e) {
			tracer.log(TraceLevel.ERROR, "TRACE_M_LOAD_MODEL_EXCEPTION", new Object[]{modelPath, e.getMessage()});
			throw e;
		}		
	}

	@Override
	public void processTuple(StreamingInput<Tuple> stream, Tuple tuple) throws Exception {
		//For each incoming tuple, extract the testDataAttr attribute value as a list of doubles

		try {
			String testDataStr = testDataAttr.getValue(tuple);

			//perform the specific operation using the specific model
			double result = model.computeDistance(testDataStr);
			
			//Generate an output tuple
			OutputTuple out= getOutput(0).newTuple();

			//Pass all incoming attributes as is to the output tuple
			out.assign(tuple);

			out.setDouble(RESULT_ATTRIBUTE, result);

			//Submit to the output port
			getOutput(0).submit(out);
		} catch (Exception e){
			tracer.log(TraceLevel.ERROR, "TRACE_M_PROCESS_TUPLE", new String[]{e.getClass().getName(), e.getMessage()});
		}
	}

	
}
