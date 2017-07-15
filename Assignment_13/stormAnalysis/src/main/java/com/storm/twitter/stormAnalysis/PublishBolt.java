package com.storm.twitter.stormAnalysis;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Date;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;
/*import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;*/
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;


/*import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;*/

public class PublishBolt extends BaseRichBolt{
	//private static final Logger logger = LogManager.getLogger(PublishBolt.class);
	 private OutputCollector collector;

	public void execute(Tuple tuple) {
		System.out.println("-------PUB BOLT------------ : ");
		String tweet = tuple.getStringByField(Constants.TWEET);
	    Double sentiment = tuple.getDoubleByField(Constants.SENTIMENT);
	    String finalSent = null;
	    if(sentiment>0 && sentiment <=1){
	    	finalSent = "VERY UNHAPPY";
	    }else if(sentiment >2 && sentiment <=3){
	    	finalSent = "HAPPY";
	    }else if(sentiment >1 && sentiment <=2){
	    	finalSent = "UNHAPPY";
	    }
		System.out.println("PUB BOLT TWEET : "+finalSent);
	    collector.emit(new Values(tweet, finalSent));
	  //  logger.info("Tweet : "+tweet+" ------------ Sentiment : "+finalSent);
	    System.out.println("Tweet : "+tweet+" ------------ Sentiment : "+finalSent);
	   Configuration hConf =  HBaseConfiguration.create();
	   hConf.addResource(new Path("/etc/hbase/conf/core-site.xml"));
	    	       hConf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
	    	      HTable hTable = null;
				try {
					hTable = new HTable(hConf, "SentimentTwitter");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	    	      String rowkey = "row_"+new Date().getTime();
	    	      Put put = new Put(Bytes.toBytes(rowkey));
	    	      put.add(Bytes.toBytes("data"),Bytes.toBytes("tweet"), Bytes.toBytes(tweet));
	    	      put.add(Bytes.toBytes("data"),Bytes.toBytes("sentiment"), Bytes.toBytes(finalSent));
	    	      try {
					hTable.put(put);
				} catch (RetriesExhaustedWithDetailsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedIOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	      try {
					hTable.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		collector = arg2;
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(Constants.TWEET,"SentimentValue"));
		
	}

	/*public void execute(Tuple arg0) {
		// TODO Auto-generated method stub
		
	}

	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		// TODO Auto-generated method stub
		
	}*/

	/*public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
		
	}*/

}
