import java.util.Arrays;

import org.apache.spark.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import scala.Tuple2;

public class NetworkWordCount {
	
	public static void main(String[] args) {
		
		if (args.length < 2) {
			System.out.println("Usage: NetworkWordCount <hostname> <port>");
			System.exit(1);
		}
				
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkCount");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
		JavaReceiverInputDStream<String> lines = jssc.socketTextStream(args[0], Integer.parseInt(args[1]));
		JavaDStream<String> words = lines.flatMap(
				new FlatMapFunction<String, String>() {
					@Override public Iterable<String> call(String x) {
						return Arrays.asList(x.split(" "));
					}
				}
				);
		
		JavaPairDStream<String, Integer> pairs = words.mapToPair(
				new PairFunction<String,String, Integer>() {
					@Override
					public Tuple2<String, Integer> call(String s) {
						return new Tuple2<String, Integer>(s, 1);
					}
				}
				);
		
		JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
				new Function2<Integer, Integer, Integer>() {
					@Override
					public Integer call(Integer i1, Integer i2) {
						return i1 + i2;
					}
				}
				);
		
		wordCounts.print();
		
		jssc.start();
		jssc.awaitTermination();
		
	}

}
