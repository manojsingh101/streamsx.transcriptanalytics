package com.ibm.streamsx.transcriptanalytics;
import com.ibm.streams.operator.metrics.Metric.Kind;
import com.ibm.streams.operator.model.InputPortSet.WindowMode;
import com.ibm.streams.operator.model.InputPortSet.WindowPunctuationInputMode;
import com.ibm.streams.operator.model.OutputPortSet.WindowPunctuationOutputMode;

@com.ibm.streams.operator.model.SharedLoader()
@com.ibm.streams.operator.model.PrimitiveOperator(namespace="com.ibm.streamsx.transcriptanalytics", description="This operator is used to compute distance of a text from model consisting of set of text. Distance represents how close the text is to the cluster or topic")
@com.ibm.streams.operator.model.InputPortSet(cardinality=1, description="This input port is required. The operator expects an attribute of type rstring that will be used as input distance scoring model.")
@com.ibm.streams.operator.model.OutputPortSet(cardinality=1, description="This output port is required. The operator passes through all attributes on the input port as-is to the output port. In addition, it expects an attribute called 'score' of type float64 '")
@com.ibm.streams.operator.internal.model.ShadowClass("com.ibm.streamsx.transcriptanalytics.DistanceModelScore")
@javax.annotation.Generated("com.ibm.streams.operator.internal.model.processors.ShadowClassGenerator")
public class DistanceModelScore$StreamsModel extends com.ibm.streams.operator.AbstractOperator
 {

@com.ibm.streams.operator.model.Parameter(name="testDataAttr", optional=false, description="The attribute which contains the text data which needs to be scored")
@com.ibm.streams.operator.internal.model.MethodParameters({"testDataAttr"})
public void setTestDataAttr(com.ibm.streams.operator.TupleAttribute<com.ibm.streams.operator.Tuple,java.lang.String> testDataAttr) {}

@com.ibm.streams.operator.model.Parameter(name="modelPath", optional=false, description="The path containing the persisted analytic model")
@com.ibm.streams.operator.internal.model.MethodParameters({"path"})
public void setModelPath(java.lang.String path) {}

@com.ibm.streams.operator.model.Parameter(name="minSetSize", optional=true, description="The min number of words in the set for which scoring would be done. default =5")
@com.ibm.streams.operator.internal.model.MethodParameters({"minSetSize"})
public void setMinSetSize(int minSetSize) {}
}