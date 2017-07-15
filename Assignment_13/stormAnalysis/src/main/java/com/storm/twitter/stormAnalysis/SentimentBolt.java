package com.storm.twitter.stormAnalysis;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
/*
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;*/
/*import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;*/
/*import twitter4j.Status;*/

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentBolt extends BaseRichBolt{

	OutputCollector outputCollector;
	
	public void execute(Tuple tuple) {
		System.out.println("-------SENT BOLT------------ : ");
		String tweetText = tuple.getString(0);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		System.out.println("TWEET : "+tweetText);
		try {
			 root = mapper.readTree(tweetText);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ROOT NODE : "+root);
		JsonNode textNode = root.path("text");
		tweetText = textNode.textValue();
		//Status tweet = (Status) tuple.getValueByField(Constants.TWEET);
		//String tweetText = tweet.getText();
		//String tweetText = tuple.getStringByField(Constants.TWEET);
		//String latitude = tuple.getStringByField(Constants.LATITUDE);
		//String longitude = tuple.getStringByField(Constants.LONGITUDE);
		if(tweetText != null){
		Double tweetSentiment = new SentimentAnalyzer().findSentiment(tweetText);
	    float a=0,n=0;
	    String text = tweetText;
			Properties props = new Properties();
			props.setProperty("annotators",
	                "tokenize, ssplit, pos, lemma, parse, sentiment");
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			Annotation annotation = pipeline.process(text);

			List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
			for(CoreMap sentence: sentences){
				String sentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
				Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
				int s = RNNCoreAnnotations.getPredictedClass(tree);
				a+=s;
				n++;
			}
			tweetSentiment= (double) (a/n);
			System.out.println("SENT BOLT TWEET TEXT: "+tweetText);
			System.out.println("SENT BOLT TWEET SENTIMENT : "+tweetSentiment);
	//	System.out.println("SENT BOLT SENTI : "+tweetSentiment);
			outputCollector.emit(new Values(tweetText,tweetSentiment/*, latitude,longitude*/));
		}
		
	}

	public void prepare(Map arg0, TopologyContext arg1, OutputCollector paramOutputCollector) {
		outputCollector = paramOutputCollector;
		
	}

	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields(Constants.TWEET,Constants.SENTIMENT/*, Constants.LATITUDE, Constants.LONGITUDE*/));
		
	}

	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	/*public void execute(Tuple arg0) {
		// TODO Auto-generated method stub
		
	}

	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		// TODO Auto-generated method stub
		
	}

	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
		
	}*/

}
