import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
public class Aadhar {
public static class AaadharMapper extends Mapper<LongWritable,Text,Text,Text>{
		
		public void map(LongWritable offset,Text value,Context context) throws IOException, InterruptedException{
			String[] records = value.toString().split(",");
			if(!records[2].equalsIgnoreCase("State") && Integer.parseInt(records[10]) > 0
					&& Integer.parseInt(records[11]) > 0){
				context.write(new Text(records[2]),new Text(records[10]+"-"+records[11]));				
			}
		}
		
	}
	
    public static class AaadharReducer extends Reducer<Text,Text,Text,IntWritable>{
		
		Map<String,Integer> stateCount = new TreeMap<String, Integer>(Collections.reverseOrder());
    	
    	public void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException{
			int sum = 0;
			int emailGiven = 0;
			int phoneNumberGiven = 0;
			for(Text value : values){
				String[] details = value.toString().split("-");
				emailGiven = Integer.parseInt(details[0]);
				phoneNumberGiven = Integer.parseInt(details[1]);
				if(emailGiven >= phoneNumberGiven){
					sum+=phoneNumberGiven;					
				}else{
					sum+=emailGiven;
				}
			}
			stateCount.put(key.toString(),sum);
		}
		
		@Override
		protected void cleanup(
				org.apache.hadoop.mapreduce.Reducer.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			super.cleanup(context);
			for(Map.Entry<String,Integer> entry : stateCount.entrySet()){
				context.write(new Text(entry.getKey()),new IntWritable(entry.getValue()));
			}
		}
	}	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		 
		  Configuration  conf = new Configuration();
		  Job job = Job.getInstance(conf,"State and total number of people given both phone and email");
		  job.setJarByClass(Aadhar.class);
		  job.setMapperClass(AaadharMapper.class);
		  job.setMapOutputKeyClass(Text.class);
		  job.setMapOutputValueClass(Text.class);
	      job.setReducerClass(AaadharReducer.class);
		  job.setOutputKeyClass(Text.class);
		  job.setOutputValueClass(IntWritable.class);
		  FileInputFormat.addInputPath(job,new Path(args[0]));
		  FileOutputFormat.setOutputPath(job,new Path(args[1]));
		  System.exit(job.waitForCompletion(true) ? 0 : 1);
	}


}
