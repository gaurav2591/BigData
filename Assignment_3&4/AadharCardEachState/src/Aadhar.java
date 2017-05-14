import java.io.BufferedReader;
import java.io.DataInputStream;
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

		    URI[] files = context.getCacheFiles();

		    Path path = new Path(files[0]);
		    FileSystem fileSystem = FileSystem.get(context.getConfiguration());
			FSDataInputStream fsDataInputStream = fileSystem.open(path);
		    DataInputStream dataInputStream = new DataInputStream(fsDataInputStream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(dataInputStream));
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

		
		
		
		public void map(LongWritable ofs,Text value,Context cntx) throws IOException, InterruptedException{
			String[] rec = value.toString().split(",");
			if(!rec[2].equalsIgnoreCase("State") && map.get(rec[2]) != null){
				cntx.write(new Text(map.get(rec[2])),new IntWritable(Integer.parseInt(rec[8])));				
			}
		}
		
	}
	
    public static class AaadharReducer extends Reducer<Text,IntWritable,Text,IntWritable>{
		
		Map<String,Integer> count = new TreeMap();
    	
    	public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException{
			int sum = 0;
			for(IntWritable value : values){
				sum+=value.get();
			}
			count.put(key.toString(),sum);
		}
		
		@Override
		protected void cleanup(
				org.apache.hadoop.mapreduce.Reducer.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			super.cleanup(context);
			for(Map.Entry<String,Integer> entry : count.entrySet()){
				context.write(new Text(entry.getKey()),new IntWritable(entry.getValue()));
			}
		}
	}	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		 
		  Configuration  conf = new Configuration();
		  Job job = Job.getInstance(conf,"State abbr and total aadhar generated");
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
