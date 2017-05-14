import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AadharReducer extends Reducer<Text,Text,Text,Text>{
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
