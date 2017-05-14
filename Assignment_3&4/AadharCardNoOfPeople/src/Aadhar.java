import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
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
	private Text finalKey = new Text();
	private Text value = new Text();
		public void map(LongWritable ofs,Text val,Context ctx) throws IOException, InterruptedException{
			String str = val.toString();
			String[] rec = str.split(",");
			if(!rec[2].equalsIgnoreCase("State") && Integer.parseInt(rec[10]) > 0
					&& Integer.parseInt(rec[11]) > 0){
				finalKey.set(rec[2]);
				value.set(rec[10]+":"+rec[11]);
				ctx.write(finalKey,value);				
			}
		}
		
	}
	
    public static class AaadharReducer extends Reducer<Text,Text,Text,IntWritable>{
		
		Map<String,Integer> map = new TreeMap<String, Integer>(Collections.reverseOrder());
    	
    	public void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException{
			int total = 0;
			int isEmail = 0;
			int isPhone = 0;
			for(Text value : values){
				String[] val = value.toString().split(":");
				isEmail = Integer.parseInt(val[0]);
				isPhone = Integer.parseInt(val[1]);
				if(isEmail >= isPhone){
					total+=isPhone;					
				}else{
					total+=isEmail;
				}
			}
			map.put(key.toString(),total);
		}
		
		@Override
		protected void cleanup(
				Context context)
				throws IOException, InterruptedException {
			super.cleanup(context);
			for(Map.Entry<String,Integer> entry : map.entrySet()){
				context.write(new Text(entry.getKey()),new IntWritable(entry.getValue()));
			}
		}
	}	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		 
		  Configuration  conf = new Configuration();
		  Job job = Job.getInstance(conf,"Assignment 4");
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
