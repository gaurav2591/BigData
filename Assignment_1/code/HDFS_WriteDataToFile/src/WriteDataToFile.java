import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class WriteDataToFile {

	public static void main(String[] args) throws IOException {
		Configuration configuration = new Configuration();
		configuration.set("fs.defaultFS", "hdfs://sandbox.hortonworks.com:8020");
		FileSystem fileSystem = FileSystem.get(configuration);
		Path path = new Path("/user/gaurav/"+args[0]);
		StringBuilder builder = new StringBuilder();
		builder.append("Writing Data to new File");
		builder.append("\n");
		builder.append("Starting new line");
		byte[] bytes = builder.toString().getBytes();
		FSDataOutputStream dataOutputStream = fileSystem.create(path);
		dataOutputStream.write(bytes);
		dataOutputStream.close();
		System.out.println("Data written");
		fileSystem.close();
	}

}
