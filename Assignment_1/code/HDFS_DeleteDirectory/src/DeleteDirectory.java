import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DeleteDirectory {
	
	public static void main(String[] args) throws IOException {
		Configuration configuration = new Configuration();
		configuration.set("fs.defaultFS", "hdfs://sandbox.hortonworks.com:8020");
		FileSystem fileSystem = FileSystem.get(configuration);
		Path path = new Path("/user/gaurav/"+args[0]);
		fileSystem.delete(path, true);
		System.out.println("Directory Deleted");
		fileSystem.close();
	}

}
