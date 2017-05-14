import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class ProducerClass {

	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put("zk.connect", "localhost:2181");
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("metadata.broker.list", "sandbox.hortonworks.com:6667");
		ProducerConfig producerConfig = new ProducerConfig(properties);
		Producer producer = new Producer(producerConfig); 
		producer.send(new KeyedMessage("KafkaJavaApi", "Sending message via java api"));

	}

}
