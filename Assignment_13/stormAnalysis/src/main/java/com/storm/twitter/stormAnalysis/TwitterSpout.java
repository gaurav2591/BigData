package com.storm.twitter.stormAnalysis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/*import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;*/

public class TwitterSpout /*extends BaseRichSpout */{
	/*
	private static final Logger logger = LogManager.getLogger(TwitterSpout.class);
	 SpoutOutputCollector _collector;
	   LinkedBlockingQueue<Status> queue = null;
	   TwitterStream _twitterStream;

	// Twitter API authentication credentials
	String custkey, custsecret;
	String accesstoken, accesssecret;
	 String[] keyWords = {"modi","india"};

	public TwitterSpout(String key, String secret, String token, String tokenSecret) {
		this.custkey = key;
		this.custsecret = secret;
		this.accesstoken = token;
		this.accesssecret = tokenSecret;
	}
	
	public TwitterSpout() {
		
	}

	public void open(Map map, TopologyContext context, SpoutOutputCollector collector) {
		  queue = new LinkedBlockingQueue<Status>(1000);
	         _collector = collector;
	         StatusListener listener = new StatusListener() {
	            public void onStatus(Status status) {
	               queue.offer(status);
	            }
						
	            public void onDeletionNotice(StatusDeletionNotice sdn) {}
						
	            public void onTrackLimitationNotice(int i) {}
						
	            public void onScrubGeo(long l, long l1) {}
						
	            public void onException(Exception ex) {}
						
	            public void onStallWarning(StallWarning arg0) {
	               // TODO Auto-generated method stub
	            }
	         };
					
	         ConfigurationBuilder cb = new ConfigurationBuilder();
					
	         cb.setDebugEnabled(true)
	            .setOAuthConsumerKey(custkey)
	            .setOAuthConsumerSecret(custsecret)
	            .setOAuthAccessToken(accesstoken)
	            .setOAuthAccessTokenSecret(accesssecret);
						
	         _twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	         _twitterStream.addListener(listener);
					
	         if (keyWords.length == 0) {
	            _twitterStream.sample();
	         }else {
	            FilterQuery query = new FilterQuery().track(keyWords).language("en");
	            _twitterStream.filter(query);
	         }

	}

	public void nextTuple() {
		Status ret = queue.poll();
		System.out.println("TWEET SPOUT : "+ret);
	      if (ret == null) {
	         try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      } else {
	         _collector.emit(new Values(ret));
	      }
		//collector.emit(new Values(tweetText/*, latitude, longitude*//*));

	/*}

	
	@Override
	   public void close() {
	      _twitterStream.shutdown();
	   }
				
	   @Override
	   public Map<String, Object> getComponentConfiguration() {
	      Config ret = new Config();
	      ret.setMaxTaskParallelism(1);
	      return ret;
	   }
				
	   @Override
	   public void ack(Object id) {}
				
	   @Override
	   public void fail(Object id) {}
		
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(Constants.TWEET, Constants.LATITUDE, Constants.LONGITUDE));

	}

*/}
