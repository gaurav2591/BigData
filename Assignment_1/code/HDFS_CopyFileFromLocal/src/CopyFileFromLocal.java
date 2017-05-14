import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class CopyFileFromLocal {

	public static void main(String[] args) throws IOException {
		Configuration configuration = new Configuration();
		configuration.set("fs.defaultFS", "hdfs://sandbox.hortonworks.com:8020");
		FileSystem fileSystem = FileSystem.get(configuration);
		Path toPath = new Path("/user/gaurav/"+args[1]);
		Path fromPath = new Path(args[0]);
		fileSystem.copyFromLocalFile(fromPath, toPath);
		System.out.println("File Copied");
		fileSystem.close();

	}

}
