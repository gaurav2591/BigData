import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class AadharTest {
	MapDriver<LongWritable, Text, Text, IntWritable> mapDriver;
	ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;

	@Before
	public void setUp() {
		Aadhar.AaadharMapper mapper = new Aadhar.AaadharMapper();
		Aadhar.AaadharReducer reducer = new Aadhar.AaadharReducer();
		mapDriver = MapDriver.newMapDriver(mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
	}

	@Test
	public void testAadharMap() throws IOException {
		mapDriver.withInput(new LongWritable(),
				new Text("CSC e-Governance Services India Limited,CMS Computers Ltd,Maharashtra,Latur,Udgir,413517,F,21,2,1,0,1"));
		mapDriver.withOutput(new Text("CMS Computers Ltd"), new IntWritable(1));
		mapDriver.runTest();
	}

	@Test
	public void testAadharReduce() throws IOException {
		Map<String,Integer> map = new HashMap();
		List<IntWritable> list = new ArrayList<IntWritable>();
		list.add(new IntWritable(1));
		reduceDriver.withInput(new Text("CMS Computers Ltd"), list);
		reduceDriver.withOutput(new Text("Total Num of Agencies rejected"), new IntWritable(1));
		reduceDriver.withOutput(new Text("CMS Computers Ltd"), new IntWritable(1));
		reduceDriver.runTest();
	}

}
