import org.apache.spark.streaming.StreamingContext
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.mllib.clustering.KMeansModel
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.feature.HashingTF

object FilterOnCluster {
  val tf = new HashingTF(1000)
  def main(args: Array[String]): Unit = {
    println("Filtering Starts")
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName)
    val ssc = new StreamingContext(conf, Seconds(5))
    System.setProperty("twitter4j.oauth.consumerKey", "")
    System.setProperty("twitter4j.oauth.consumerSecret", "")
    System.setProperty("twitter4j.oauth.accessToken", "")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "")
    val stream = TwitterUtils.createStream(ssc, None)
    val statuses = stream.map(_.getText)
    val model = new KMeansModel(ssc.sparkContext.objectFile[Vector](
      "hdfs://sandbox.hortonworks.com:8020/user/gaurav/spark/model").collect())
    val filteredTweets = statuses
      .filter(t => model.predict(featurize(t)) == Integer.parseInt(args(0)))
    println("-----------Filtered Twees----------")
    filteredTweets.print()
    ssc.start()
    ssc.awaitTermination()

  }

  def featurize(s: String): Vector = { tf.transform(s.sliding(2).toSeq) }
}