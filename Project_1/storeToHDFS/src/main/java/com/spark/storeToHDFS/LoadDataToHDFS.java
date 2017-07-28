package com.spark.storeToHDFS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.avro.generic.GenericData.Record;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
//import org.spark_project.guava.collect.Lists;

import kafka.serializer.StringDecoder;
import scala.Tuple2;
import scala.tools.nsc.interpreter.StructuredTypeStrings;

/*import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import scala.Tuple2;*/

public class LoadDataToHDFS {

	  @SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException {

		    String brokers = "sandbox.hortonworks.com:6667";
		    String topics = "customersTopic";

		    // Create context with a 2 seconds batch interval
		    SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaWordCount");
		    
		    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));
		    SQLContext sqlContext = new SQLContext(jssc.sparkContext().sc());
		    HashSet<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
		    HashMap<String, String> kafkaParams = new HashMap<String, String>();
		    kafkaParams.put("metadata.broker.list", brokers);
		  //  if (args.length == 3) {
		     // kafkaParams.put(KafkaUtils.securityProtocolConfig(), args[2]);
		    //}

		    // Create direct kafka stream with brokers and topics
		    JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(
		        jssc,
		        String.class,
		        String.class,
		        StringDecoder.class,
		        StringDecoder.class,
		        kafkaParams,
		        topicsSet
		    );

		    JavaDStream<String> text = messages.map(Tuple2::_2);
		    System.out.println("===========LINES============"+text.count());
		    JavaDStream<String> lines = text.flatMap(new FlatMapFunction <String, String>(){

				@Override
				public Iterable<String> call(String txt) throws Exception {
					return Arrays.asList(txt.split("\n"));
				}

		        
		    });
		    Boolean flag = false;
		    lines.foreachRDD((rdd -> {
	            rdd.foreachPartition(p -> {
	                //System.out.println("==================KAFKA START==================");
	            	while (p.hasNext()) {
	            		String line = p.next();
	                    System.out.println("===========Value of Kafka queue !!!!!!!!!!" + line);
	                    //String[] array = line.split("\n");
	                    //System.out.println("===========SIZE : "+array.length);
	                    
	                }
	            });
			    }));
		    if(args[0].equalsIgnoreCase("customers")){
		    JavaDStream<String> filteredData = lines.filter(new Function<String, Boolean>() {
				
				@Override
				public Boolean call(String line) throws Exception {
					System.out.println("===================EACH LINE : "+line);
					String[] array = line.split("\\|");
					System.out.println("===========Array : "+array.length+" ele : "+array[1]);
					Boolean flag = false;
					if(!array[0].equalsIgnoreCase("Age Numeric")){
					if(Integer.parseInt(array[1]) < 65){
						flag = true;
					}
					}
					return flag;
				}
			});
		    
		    JavaDStream<String> filteredDataSpaces = lines.filter(new Function<String, Boolean>() {
				
				@Override
				public Boolean call(String line) throws Exception {
					//System.out.println("===================EACH LINE : "+line);
					String[] array = line.split("\\|");
					//System.out.println("===========Array : "+array.length+" ele : "+array[1]);
					Boolean flag = false;
					if(!array[2].equalsIgnoreCase("Age Numeric")){
					if(array[2].trim().isEmpty()){
						flag = true;
					}
					}
					return flag;
				}
			});
		    
		   
		    filteredData.foreachRDD(new VoidFunction<JavaRDD<String>>() {
	              @Override
	              public void call(JavaRDD<String> rdd) { 
	                  JavaRDD<Row> rowRDD = rdd.map(new Function<String, Row>() {
	                      @Override
	                      public Row call(String msg) {
	                    	  System.out.println("====================MSG : "+msg);
	                        Row row = RowFactory.create(msg);
	                        return row;
	                      }
	                    });
	               
	        StructType schema = DataTypes.createStructType(new StructField[] {DataTypes.createStructField("Message", DataTypes.StringType, true)});
	        SQLContext spark = SQLContext.getOrCreate((rdd.context()));
	        DataFrame msgDataFrame = spark.createDataFrame(rowRDD, schema);
	        msgDataFrame.show();
	        if(msgDataFrame != null && msgDataFrame.take(1).length > 0){
	        	msgDataFrame.write().parquet("filteredCustomers");
	        	/*msgDataFrame.write().parquet("/usr/hdp/current/parFile");*/
	        }
	        }
	        });
		    
		    filteredDataSpaces.foreachRDD(new VoidFunction<JavaRDD<String>>() {
	              @Override
	              public void call(JavaRDD<String> rdd) { 
	            	  if(rdd.count() > 0){
	         Row ro =     RowFactory.create(String.valueOf(rdd.count()));
	            	  
	          List<Row> li = new ArrayList<>();
	          li.add(ro);
	         JavaRDD<Row> rRdd = jssc.sparkContext().parallelize(li);
	        StructType schema = DataTypes.createStructType(new StructField[] {DataTypes.createStructField("MessageSpaces", DataTypes.StringType, true)});
	        SQLContext spark = SQLContext.getOrCreate((rdd.context()));
	        DataFrame msgDataFrame = spark.createDataFrame(rRdd, schema);
	        msgDataFrame.show();
	        if(msgDataFrame != null && msgDataFrame.take(1).length > 0){
	        	msgDataFrame.write().parquet("customersWithBlankCustID");
	        	/*msgDataFrame.write().parquet("/usr/hdp/current/parFile");*/
	        }
	            	  }
	        }
	        });
		    
		    lines.foreachRDD(new VoidFunction<JavaRDD<String>>() {
	              @Override
	              public void call(JavaRDD<String> rdd) { 
	            	  if(rdd.count() > 0){
	          Row ro =     RowFactory.create("TOTAL RECORDS PROCESSED  "+String.valueOf(rdd.count()));
	            	 
	          List<Row> li = new ArrayList<>();
	          li.add(ro);
	         JavaRDD<Row> rRdd = jssc.sparkContext().parallelize(li);
	        StructType schema = DataTypes.createStructType(new StructField[] {DataTypes.createStructField("MessageTotal", DataTypes.StringType, true)});
	        SQLContext spark = SQLContext.getOrCreate((rdd.context()));
	        DataFrame msgDataFrame = spark.createDataFrame(rRdd, schema);
	        msgDataFrame.show();
	        if(msgDataFrame != null && msgDataFrame.take(1).length > 0){
	        	msgDataFrame.write().parquet("totalCustomersRec");
	        	/*msgDataFrame.write().parquet("/usr/hdp/current/parFile");*/
	        }
	            	  }
	        }
	        });
		    }else{

			    
			    JavaDStream<String> filteredDataSpaces = lines.filter(new Function<String, Boolean>() {
					
					@Override
					public Boolean call(String line) throws Exception {
						//System.out.println("===================EACH LINE : "+line);
						String[] array = line.split("\\|");
						//System.out.println("===========Array : "+array.length+" ele : "+array[1]);
						Boolean flag = false;
						if(!array[0].trim().equalsIgnoreCase("Address Line 1")){
						if(array[7].trim().isEmpty()){
							flag = true;
						}
						}
						return flag;
					}
				});
			    
			    lines.foreachRDD(new VoidFunction<JavaRDD<String>>() {
		              @Override
		              public void call(JavaRDD<String> rdd) { 
		                  JavaRDD<Row> rowRDD = rdd.map(new Function<String, Row>() {
		                      @Override
		                      public Row call(String msg) {
		                    	  System.out.println("====================MSG : "+msg);
		                        Row row = RowFactory.create(msg);
		                        return row;
		                      }
		                    });
		               
		        StructType schema = DataTypes.createStructType(new StructField[] {DataTypes.createStructField("Message", DataTypes.StringType, true)});
		        SQLContext spark = SQLContext.getOrCreate((rdd.context()));
		        DataFrame msgDataFrame = spark.createDataFrame(rowRDD, schema);
		        msgDataFrame.show();
		        if(msgDataFrame != null && msgDataFrame.take(1).length > 0){
		        	msgDataFrame.write().parquet("houseHold");
		        	/*msgDataFrame.write().parquet("/usr/hdp/current/parFile");*/
		        }
		        }
		        });
			    
			    filteredDataSpaces.foreachRDD(new VoidFunction<JavaRDD<String>>() {
		              @Override
		              public void call(JavaRDD<String> rdd) {
		            	  if(rdd.count() > 0){
		         Row  ro =     RowFactory.create(String.valueOf(rdd.count()));
		            	 
		          List<Row> li = new ArrayList<>();
		          li.add(ro);
		         JavaRDD<Row> rRdd = jssc.sparkContext().parallelize(li);
		        StructType schema = DataTypes.createStructType(new StructField[] {DataTypes.createStructField("MessageSpaces", DataTypes.StringType, true)});
		        SQLContext spark = SQLContext.getOrCreate((rdd.context()));
		        DataFrame msgDataFrame = spark.createDataFrame(rRdd, schema);
		        msgDataFrame.show();
		        if(msgDataFrame != null && msgDataFrame.take(1).length > 0){
		        	msgDataFrame.write().parquet("houseHoldWithBlankCustID");
		        	//msgDataFrame.write().parquet("/usr/hdp/current/parFile");
		        }
		            	  }
		        }
		        });
			    
			    lines.foreachRDD(new VoidFunction<JavaRDD<String>>() {
		              @Override
		              public void call(JavaRDD<String> rdd) { 
		            	  if(rdd.count() > 0){
		         Row  ro =     RowFactory.create("TOTAL RECORDS PROCESSED "+String.valueOf(rdd.count()));
		            	 
		          List<Row> li = new ArrayList<>();
		          li.add(ro);
		         JavaRDD<Row> rRdd = jssc.sparkContext().parallelize(li);
		        StructType schema = DataTypes.createStructType(new StructField[] {DataTypes.createStructField("MessageTotal", DataTypes.StringType, true)});
		        SQLContext spark = SQLContext.getOrCreate((rdd.context()));
		        DataFrame msgDataFrame = spark.createDataFrame(rRdd, schema);
		        msgDataFrame.show();
		        if(msgDataFrame != null && msgDataFrame.take(1).length > 0){
		        	msgDataFrame.write().parquet("totalHouseHoldRec");
		        	//msgDataFrame.write().parquet("/usr/hdp/current/parFile");
		        }
		            	  }
		        }
		        });
			    
		    }
		    jssc.start();
		    jssc.awaitTermination();
		   
}
}
