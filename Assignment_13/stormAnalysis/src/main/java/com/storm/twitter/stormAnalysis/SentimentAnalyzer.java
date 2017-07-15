package com.storm.twitter.stormAnalysis;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalyzer {

	 static StanfordCoreNLP pipeline;

	    public static void init() {
	    	Properties props = new Properties();
	        props.put("annotators", "tokenize,ssplit,parse,sentiment");
	        pipeline = new StanfordCoreNLP(props);
	    }

	    public static double findSentiment(String tweet) {
	    	Properties props = new Properties();
	        props.setProperty("annotators", "tokenize,ssplit,parse,sentiment");
	        pipeline = new StanfordCoreNLP(props);
	        int sumSentiment = 0;
	        double n = 0;
	        if (tweet != null && tweet.length() > 0) {
	            int longest = 0;
	            Annotation annotation = pipeline.process(tweet);
	            for (CoreMap sentence : annotation
	                    .get(CoreAnnotations.SentencesAnnotation.class)) {
	                Tree tree = sentence
	                        .get(SentimentCoreAnnotations.AnnotatedTree.class);
	                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
	                sumSentiment += sentiment;
	                n++;

	            }
	        }
	        return sumSentiment/n;
	    }
	    
	    public static void main(String[] args) {
			double test = findSentiment("RoseTintedVisor : Quantum Big Data Cloud AI Book Chapter 89- https://t.co/dTjFV3EREB Hadoop Action Chuck Lam : https://t.co/tEnK1gc2KH");
			System.out.println(test);
		}
}
