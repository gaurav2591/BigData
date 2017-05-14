import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class ReadDataFromFile {

	public static void main(String[] args) throws IOException {
		Configuration configuration = new Configuration();
		configuration.set("fs.defaultFS", "hdfs://sandbox.hortonworks.com:8020");
		FileSystem fileSystem = FileSystem.get(configuration);
		Path path = new Path("/user/gaurav/"+args[0]);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileSystem.open(path)));
		String str = null;
		while((str = bufferedReader.readLine()) != null){
			System.out.println(str);	
		}
		
		fileSystem.close();

	}

}
