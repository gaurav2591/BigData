import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AadharMapper extends Mapper<LongWritable,Text,Text,Text>{
	public void map(LongWritable ofs,Text val,Context cntx) throws IOException, InterruptedException{
		String str = val.toString();
		String[] rec = str.split(",");
		if(!rec[7].equalsIgnoreCase("Age")){
			cntx.write(new Text("count"),new Text(rec[8]));				
		}
	}
}
