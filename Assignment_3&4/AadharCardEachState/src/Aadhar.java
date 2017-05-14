import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
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
public static class AaadharMapper extends Mapper<LongWritable,Text,Text,IntWritable>{
		
		private Map<String, String> map = new HashMap();
		
		protected void setup(Context context) throws java.io.IOException, InterruptedException{
			
			super.setup(context);
			 URI[] cacheFiles = context.getCacheFiles();
			 Path path1 = new Path(cacheFiles[0]);
			 BufferedReader br = new BufferedReader(new FileReader(path1.getName()));
			String eachLine = br.readLine();
			while(eachLine != null) {
				String[] rec = eachLine.split(",");
				String state = rec[0];
				String abb = rec[1];
				if(!state.equalsIgnoreCase("State/UT")){
					map.put(state,abb);
				}	
				eachLine = br.readLine();
			}
				br.close();
			
			if (map.isEmpty()) {
				throw new IOException("Error occurred");
			}
		}

		
		
		private Text finalKey = new Text();
		private IntWritable value = new IntWritable();
		public void map(LongWritable ofs,Text val,Context cntx) throws IOException, InterruptedException{
			String str = val.toString();
			String[] rec = str.split(",");
			if(!rec[2].equalsIgnoreCase("State") && map.get(rec[2]) != null){
				finalKey.set(map.get(rec[2]));
				value.set(Integer.parseInt(rec[8]));
				cntx.write(finalKey,value);				
			}
		}
		
	}
	
    public static class AaadharReducer extends Reducer<Text,IntWritable,Text,IntWritable>{
		
		Map<String,Integer> map = new TreeMap();
    	
    	public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException{
			int total = 0;
			for(IntWritable value : values){
				total+=value.get();
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
		  Job job = Job.getInstance(conf,"Aadhar Card Each State");
		  job.setJarByClass(Aadhar.class);
		  job.setMapperClass(AaadharMapper.class);
	      job.setReducerClass(AaadharReducer.class);
		  job.setCombinerClass(AaadharReducer.class);
		  job.setOutputKeyClass(Text.class);
		  job.setOutputValueClass(IntWritable.class);
		  job.addCacheFile(new Path(args[0]).toUri());
		  FileInputFormat.addInputPath(job,new Path(args[1]));
		  FileOutputFormat.setOutputPath(job,new Path(args[2]));
		  System.exit(job.waitForCompletion(true) ? 0 : 1);
	}


}
