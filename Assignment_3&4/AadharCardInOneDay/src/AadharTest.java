import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class AadharTest {
	MapDriver<LongWritable, Text, Text, Text> mapDriver;
	ReduceDriver<Text, Text, Text, Text> reduceDriver;

	@Before
	public void setUp() {
		Aadhar.AadharMapper mapper = new Aadhar.AadharMapper();
		Aadhar.AadharReducer reducer = new Aadhar.AadharReducer();
		mapDriver = MapDriver.newMapDriver(mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
	}

	@Test
	public void testAadharMap() throws IOException {
		mapDriver.withInput(new LongWritable(),
				new Text("CSC e-Governance Services India Limited,CMS Computers Ltd,Maharashtra,Latur,Udgir,413517,F,21,2,0,0,1"));
		mapDriver.withOutput(new Text("count"), new Text("2"));
		mapDriver.runTest();
	}

	@Test
	public void testAadharReduce() throws IOException {
		List<Text> list = new ArrayList<Text>();
		list.add(new Text("1"));
		list.add(new Text("1"));
		reduceDriver.withInput(new Text("No of Aadhars"), list);
		reduceDriver.withOutput(new Text("No of Aadhars"), new Text("2"));
		reduceDriver.runTest();
	}

}
