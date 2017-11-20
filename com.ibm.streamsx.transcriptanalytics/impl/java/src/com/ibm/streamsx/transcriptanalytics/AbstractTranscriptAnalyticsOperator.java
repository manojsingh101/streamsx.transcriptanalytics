/*******************************************************************************
 * Copyright (C) 2017 International Business Machines Corporation
 * All Rights Reserved
 *******************************************************************************/
package com.ibm.streamsx.transcriptanalytics;

import java.util.logging.Logger;

import com.ibm.json.java.JSONObject;
import com.ibm.streams.operator.AbstractOperator;
import com.ibm.streams.operator.Attribute;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.ProcessingElement;
import com.ibm.streams.operator.StreamSchema;
import com.ibm.streams.operator.StreamingInput;
import com.ibm.streams.operator.Tuple;
import com.ibm.streams.operator.OperatorContext.ContextCheck;
import com.ibm.streams.operator.Type.MetaType;
import com.ibm.streams.operator.compile.OperatorContextChecker;
import com.ibm.streams.operator.logging.TraceLevel;
import com.ibm.streams.operator.model.SharedLoader;

/**
 * Provides the base class for all transcript analytics operators. This base class is responsible
 * for the common parameters, common compiler checks, common utility methods as well
 * as setting up the spark context from the passed parameters so that the analytics models 
 * can be loaded.
 * 
 *  
 */
@SharedLoader
public abstract class AbstractTranscriptAnalyticsOperator extends AbstractOperator {

	public static final String RESULT_ATTRIBUTE = "score";

	private static final String CLASS_NAME =  AbstractTranscriptAnalyticsOperator.class.getName();
	/**
	 * Create a {@code Logger} specific to this class that will write to the SPL
	 * trace facility
	 */
	private static Logger tracer = Logger.getLogger(CLASS_NAME, "com.ibm.streamsx.transcriptanalytics.messages");

	public AbstractTranscriptAnalyticsOperator() {
	}
		
	@Override
	public void process(StreamingInput<Tuple> stream, Tuple tuple)
			throws Exception {
		if(stream.isControl()) {
			processControlPort(stream, tuple);
		}
		else {			
			processTuple(stream, tuple);			
		}
	}
	/**
	 * Compile time to check to ensure that the output schema contains an attribute called
	 * 'sentiment' and 'sentimentProbabilities'. 
	 */
	@ContextCheck
	public static void checkOutputAttribute(OperatorContextChecker checker) {
		OperatorContext context = checker.getOperatorContext();
		
		if(context.getNumberOfStreamingOutputs() == 1) {
			StreamSchema schema = context.getStreamingOutputs().get(0).getStreamSchema();
			Attribute resultAttribute = schema.getAttribute(RESULT_ATTRIBUTE);
			
			//check if the output attribute is present where the result will be stored
			if(resultAttribute == null) {
				tracer.log(TraceLevel.ERROR, "COMPILE_M_MISSING_ATTRIBUTE", new Object[]{ RESULT_ATTRIBUTE});
				checker.setInvalidContext();
			}else if( resultAttribute.getType().getMetaType() != MetaType.FLOAT64) {
				tracer.log(TraceLevel.ERROR, "COMPILE_M_WRONG_TYPE_FULL", new Object[]{RESULT_ATTRIBUTE, "float64", resultAttribute.getType()});
				checker.setInvalidContext();
			}

		}
	}

	@ContextCheck
	public static void checkControlPortInputAttribute(OperatorContextChecker checker) {
		OperatorContext context = checker.getOperatorContext();
		
		if(context.getNumberOfStreamingInputs() == 2) {
			StreamSchema schema = context.getStreamingInputs().get(1).getStreamSchema();
			
			//the first attribute must be of type rstring
			Attribute jsonAttr = schema.getAttribute(0);
			
			//check if the input attribute is of right type
			if(jsonAttr != null && jsonAttr.getType().getMetaType() != MetaType.RSTRING) {
				tracer.log(TraceLevel.ERROR, "COMPILE_M_WRONG_TYPE", jsonAttr.getType());
				checker.setInvalidContext();
			}
		}
	}

	protected void processControlPort(StreamingInput<Tuple> stream, Tuple tuple) {
		String jsonString = tuple.getString(0);
		try {
			JSONObject config = JSONObject.parse(jsonString);
			Boolean reinitParams = (Boolean)config.get("reload");
			if(reinitParams) {				
					init();				
			}
		} catch (Exception e) {
			e.printStackTrace();
			tracer.log(TraceLevel.ERROR, "TRACE_M_CONTROL_PORT_ERROR", new Object[]{e.getMessage()});
		}
	}
	
	protected abstract void init() throws Exception;
	protected abstract void processTuple(StreamingInput<Tuple> stream, Tuple tuple) throws Exception;

	private String getUniqueAppName(OperatorContext context) {
		ProcessingElement pe = context.getPE();
		return pe.getDomainId()+"_"+pe.getInstanceId()+"_"+pe.getPEId()+"_"+context.getName();
	}
	
	//Reads an attribute parameter "manually" if the @Parameter could not be used
	//to automatically initialize the field.
	protected Attribute getAttributeParameter(OperatorContext context, String paramName) {
		String attrNameString = context.getParameterValues(paramName).get(0);
		//it will be in the iport$0.get_attrName() format
		String[] parts = attrNameString.split("\\.");
		int port = Integer.parseInt(parts[0].substring("iport$".length()));
		
		int endIndex = parts[1].indexOf("()");
		String attrName = parts[1].substring("get_".length(), endIndex);
		
		return getInput(port).getStreamSchema().getAttribute(attrName);
		
	}

	//Close the spark context on shutdown
	@Override
	public void shutdown() throws Exception {		
		super.shutdown();
	}
	

}
