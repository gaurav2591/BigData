package com.storm.twitter.stormAnalysis;
/*
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.Scheme;
import backtype.storm.spout.SchemeAsMultiScheme;
//import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;*/
/*import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;*/
/*import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;*/

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

public class TwittterTopology {
	
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException, AuthorizationException {
		/***STORM KAFKA***/
		Config config = new Config();
	      config.setDebug(true);
	      config.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
	      String zkConnString = "localhost:2181";
	      String topic = "flumekafka1";
	      BrokerHosts hosts = new ZkHosts(zkConnString);
	      
	      SpoutConfig kafkaSpoutConfig = new SpoutConfig (hosts, topic, "/" + topic,    
	        "test");
	     
	      kafkaSpoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme()); 
	    //  kafkaSpoutConfig.bufferSizeBytes = 1024 * 1024 * 4;
	      //kafkaSpoutConfig.fetchSizeBytes = 1024 * 1024 * 4;
	     // kafkaSpoutConfig.forceFromStart = true;
	      kafka.api.OffsetRequest.LatestTime();
	     // kafkaSpoutConfig.scheme = new org.ap
	     // org.apache.storm.kafka.StringMultiSchemeWithTopic();
		/******/
		
		//Used to build the topology
	    TopologyBuilder builder = new TopologyBuilder();
	    builder.setSpout("spout", new KafkaSpout(kafkaSpoutConfig));
	    //Add the spout, with a name of 'spout'
	    //and parallelism hint of 5 executors
	  //  builder.setSpout("spout", new TwitterSpout("", "", "", ""), 5);
	    //Add the SplitSentence bolt, with a name of 'split'
	    //and parallelism hint of 8 executors
	    //shufflegrouping subscribes to the spout, and equally distributes
	    //tuples (sentences) across instances of the SplitSentence bolt
	    builder.setBolt("sentimentBolt", new SentimentBolt(), 8).shuffleGrouping("spout");
	    //Add the counter, with a name of 'count'
	    //and parallelism hint of 12 executors
	    //fieldsgrouping subscribes to the split bolt, and
	    //ensures that the same word is sent to the same instance (group by field 'word')
	    builder.setBolt("publishBolt", new PublishBolt(), 12).shuffleGrouping("sentimentBolt");

	    //new configuration
	    Config conf = new Config();
	    //Set to false to disable debug information when
	    // running in production on a cluster
	    conf.setDebug(false);

	    //If there are arguments, we are running on a cluster
	    if (args != null && args.length > 0) {
	      //parallelism hint to set the number of workers
	      conf.setNumWorkers(3);
	      //submit the topology
	      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
	    }
	    //Otherwise, we are running locally
	    else {
	      //Cap the maximum number of executors that can be spawned
	      //for a component to 3
	      conf.setMaxTaskParallelism(3);
	      //LocalCluster is used to run locally
	      LocalCluster cluster = new LocalCluster();
	      //submit the topology
	      cluster.submitTopology("sentiment-analysis-topolgy", conf, builder.createTopology());
	      //sleep
	      Thread.sleep(10000);
	      //shut down the cluster
	     // cluster.killTopology("sentiment-analysis-topolgy");
	      cluster.shutdown();
	      System.out.println("-----------------FINISHED---------------");
	    }

	}

}
