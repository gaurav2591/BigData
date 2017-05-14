import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Aadhar {
	
	public static class AadharMapper extends Mapper<LongWritable,Text,Text,Text>{
		private Text key = new Text();
		private Text value = new Text();
		public void map(LongWritable ofs,Text val,Context cntx) throws IOException, InterruptedException{
			String str = val.toString();
			String[] rec = str.split(",");
			if(!rec[7].equalsIgnoreCase("Age")){
				key.set("count");
				value.set(rec[8]);
				cntx.write(key,value);				
			}
		}
	}
	
	public static class AadharReducer extends Reducer<Text,Text,Text,Text>{
		private Text finalKey = new Text();
		private Text value = new Text();
		public void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException{
			String aadharKey = "No of Aadhars";
			finalKey.set(aadharKey);
			long sum = 0;
			for(Text val : values){
				sum+=Long.parseLong(val.toString());
			}
			value.set(String.valueOf(sum));
			context.write(finalKey, value);
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		 Configuration  conf = new Configuration();
		  Job job = Job.getInstance(conf,"Aadhars");
		  job.setJarByClass(Aadhar.class);
		  job.setMapperClass(AadharMapper.class);
		  job.setReducerClass(AadharReducer.class);
		  job.setNumReduceTasks(1);
		  job.setOutputKeyClass(Text.class);
		  job.setOutputValueClass(Text.class);
		  FileInputFormat.addInputPath(job,new Path(args[0]));
		  FileOutputFormat.setOutputPath(job,new Path(args[1]));
		  System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
