import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Aadhar {

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
