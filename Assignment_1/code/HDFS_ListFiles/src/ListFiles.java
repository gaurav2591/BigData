import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

public class ListFiles {

	public static void main(String[] args) throws IOException {
		Configuration configuration = new Configuration();
		configuration.set("fs.defaultFS", "hdfs://sandbox.hortonworks.com:8020");
		FileSystem fileSystem = FileSystem.get(configuration);
		Path toPath = new Path(args[0]);
		RemoteIterator<LocatedFileStatus> iterator = fileSystem.listFiles(toPath, true);
		while (iterator.hasNext()) {
			LocatedFileStatus locatedFileStatus = (LocatedFileStatus) iterator.next();
			System.out.println(locatedFileStatus.getPath().getName());
		}
		System.out.println("Files are listed above");
		fileSystem.close();

	}

}
