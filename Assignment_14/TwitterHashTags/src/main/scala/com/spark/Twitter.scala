package com.spark

import java.util.Date

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.fs.Path

object Twitter {
  def main(args: Array[String]): Unit = {
    val config = new SparkConf().setAppName("Twitter-HashTags")
    val sc = new SparkContext(config)
    sc.setLogLevel("WARN") 
    val slideInterval = new Duration(1 * 1000)
    val windowLength = new Duration(5 * 1000)
    val ssc = new StreamingContext(sc, Seconds(10)) 
    System.setProperty("twitter4j.oauth.consumerKey", "") 
    System.setProperty("twitter4j.oauth.consumerSecret", "")
    System.setProperty("twitter4j.oauth.accessToken", "")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "")
    val stream = TwitterUtils.createStream(ssc, None)
    val hashTagsStream = stream.map { _.getText }.flatMap { _.split(" ") }.filter { _.startsWith("#") }
    val windowHashTagsStream = hashTagsStream.map { (_,1) }.reduceByKeyAndWindow((x: Int, y: Int) => x + y,Seconds(300))
    val sortedHashTagsStream = windowHashTagsStream.transform( rdd => rdd.sortBy(pair =>  pair._2, false))
   /* println("COUNT:"+windowHashTagsStream.count())*/
//    windowHashTagsStream.print()
    var num=1
    sortedHashTagsStream.foreachRDD(hashTagCountRDD => {
    println("Top HashTags count : "+hashTagCountRDD.count())
    val top10hashTagCountRDD = hashTagCountRDD.top(10)
    println("------ TOP HASHTAGS For window "+num+"------------")
    val millis = new Date().getTime();
    val rddToBeSaved = sc.parallelize(top10hashTagCountRDD, 1)
    println("+++++rddToBeSaved++++"+rddToBeSaved)
    rddToBeSaved.saveAsTextFile("hdfs://sandbox.hortonworks.com:8020/user/gaurav/spark/twitter/top10HashTags/TopHashTags_"+millis)
    num = num + 1
    top10hashTagCountRDD.foreach(rdd => {
      println("HashTag : "+rdd._1+" , Count : "+rdd._2)
       val hConf =  HBaseConfiguration.create() 
       hConf.addResource(new Path("/etc/hbase/conf/core-site.xml"))
       hConf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"))
      val hTable = new HTable(hConf, "test") 
      val rowkey = "row_"+new Date().getTime()
      val put = new Put(Bytes.toBytes(rowkey))
      put.add(Bytes.toBytes("data"),Bytes.toBytes("hashTag"), Bytes.toBytes(rdd._1))
      put.add(Bytes.toBytes("data"),Bytes.toBytes("count"), Bytes.toBytes(rdd._2))
      hTable.put(put)
      hTable.close()
    })
  })
  
    ssc.start()
    ssc.awaitTermination()
  /*windowHashTagsStream.saveAsTextFiles(prefix, suffix)*/
  }
  
  
}