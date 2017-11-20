# streamsx.transcriptanalytics
Toolkit for real-time analytics of text2speech transcripts


    
+ Developing and running applications that use the TranscriptAnalytics Toolkit

Operators: DistanceModelScore - This operator uses a trained model (a set of collection words) which represents a topic or words being spoken on a channel. The incoming tuple would be scored against this model to generate a score which represents how close the incoming text is to the model. The output score range 0..1


The following code demonstrates how this would be done in the SPL program:

	
	stream<rstring ivrcallid, rstring channel, rstring utterance, float64 score> scoredUtterances = DistanceModelScore(csvLineData){
		param
			testDataAttr : utterance;
			modelPath : "/media/sf_vmshare/dev/streamsx.transcriptanalytics/machine.10.25.new.model.csv"; 
			minSetSize : 5;
	}
		


On initialization, the operator will load the model. Each incoming tuple will be used to generate a score using the model and the score would be passed as an attribute called ‘score’on the output schema.

Other optional parameters for operator
1. minSetSize: a score is generated only if the text contains atleast these many words else default 0;

To learn more about Streams:
* [IBM Streams on Github](http://ibmstreams.github.io)
* [Introduction to Streams Quick Start Edition](http://ibmstreams.github.io/streamsx.documentation/docs/4.1/qse-intro/)
* [Streams Getting Started Guide](http://ibmstreams.github.io/streamsx.documentation/docs/4.1/qse-getting-started/)
* [StreamsDev](https://developer.ibm.com/streamsdev/)
