import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class AadharTest {
	MapDriver<LongWritable, Text, Text, Text> mapDriver;
	ReduceDriver<Text, Text, Text, IntWritable> reduceDriver;

	@Before
	public void setUp() {
		Aadhar.AaadharMapper mapper = new Aadhar.AaadharMapper();
		Aadhar.AaadharReducer reducer = new Aadhar.AaadharReducer();
		mapDriver = MapDriver.newMapDriver(mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
	}

	@Test
	public void testAadharMap() throws IOException {
		mapDriver.addCacheFile("stateAbbr.csv");
		mapDriver.withInput(new LongWritable(),
				new Text("CSC e-Governance Services India Limited,CMS Computers Ltd,Maharashtra,Latur,Udgir,413517,F,21,2,0,1,1"));
		mapDriver.withOutput(new Text("Maharashtra"), new Text("1:1"));
		mapDriver.runTest();
	}

	@Test
	public void testAadharReduce() throws IOException {
		List<Text> list = new ArrayList<Text>();
		list.add(new Text("1:1"));
		reduceDriver.withInput(new Text("Maharashtra"), list);
		reduceDriver.withOutput(new Text("Maharashtra"), new IntWritable(1));
		reduceDriver.runTest();
	}

}
