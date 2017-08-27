package com.spark.project301_3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kafka.serializer.StringDecoder;
import scala.Tuple2;


public class TweetsToHDFS {

	  @SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException {

		    //String brokers = "sandbox.hortonworks.com:6667";
		   String brokers = "localhost:9092";
		    String topics = "flumekafka1";

		    SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaWordCount");
		    
		    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));
		    SQLContext sqlContext = new SQLContext(jssc.sparkContext().sc());
		    HashSet<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
		    HashMap<String, String> kafkaParams = new HashMap<String, String>();
		    kafkaParams.put("metadata.broker.list", brokers);
		    kafkaParams.put("auto.offset.reset", "smallest");
		    JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(
		        jssc,
		        String.class,
		        String.class,
		        StringDecoder.class,
		        StringDecoder.class,
		        kafkaParams,
		        topicsSet
		    );
		    
		    
		    System.out.println("-------------COUNT : "+messages.count());
		    
		    JavaDStream<String> json = messages.map(new Function<Tuple2<String,String>, String>() {
		        public String call(Tuple2<String,String> message) throws Exception {
		            System.out.println(message._2());
		            return message._2();
		        };
		    });

		    json.foreachRDD(rdd -> {
		        /*rdd.foreach(
		                record -> System.out.println("-------RECORD----------"+record)
		                );*/
		    	
		    	List<String> tweets = rdd.collect();
		    	System.out.println("---------------------------------Tweets------------------"+tweets);
		    	List<Row> tweetsRow = new ArrayList<>();
		    	for(String tweet : tweets){
		    		tweetsRow.add(RowFactory.create(tweet));
		    	}
		    	
		    	JavaRDD<Row> javaRdd = jssc.sparkContext().parallelize(tweetsRow);
		    	StructType schema = DataTypes.createStructType(new StructField[] {DataTypes.createStructField("Tweets", DataTypes.StringType, true)});
		    	SQLContext spark = SQLContext.getOrCreate((rdd.context()));
		        DataFrame msgDataFrame = spark.createDataFrame(javaRdd, schema);
		        msgDataFrame.show();
		        if(msgDataFrame != null && msgDataFrame.take(1).length > 0){
		        	msgDataFrame.write().json("tweets");
		        }
		    });
		    
		    
		    
		    jssc.start();
		    jssc.awaitTermination();
}
}
