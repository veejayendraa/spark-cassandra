package com.vij.spark.cassandra.spark_cassandra;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;

public class SparkCassandraApp implements Serializable {
	private transient SparkConf conf;

	private SparkCassandraApp(SparkConf conf) {
		this.conf = conf;
	}

	private void run() {
		JavaSparkContext sc = new JavaSparkContext(conf);
		generateData(sc);
		aggregate(sc);
		//showResults(sc);
		sc.stop();
	}
	private void aggregate(JavaSparkContext sc){
		
		System.out.println("************AGGREGATION STARTED ***********");
		
		 CassandraConnector connector = CassandraConnector.apply(sc.getConf());
		 try (Session session = connector.openSession()) {
			 JavaRDD<TradeData> tradeDataRDD = javaFunctions(sc).cassandraTable("tradesensstorage", "tradelevelstorage", mapRowTo(TradeData.class));
			 
			   // Transform TradeLevelData to BookLevelData.Ignore Universal Trade ID (UTID) for transforming TradeData to BookData
		        JavaRDD<BookData> bookDataRDD = tradeDataRDD.map(tradeData-> new BookData(tradeData.getbusinessdate(), tradeData.getbookid(),  tradeData.getrisktypeid(), tradeData.getAmt()));	    
		      
		     /*  
		      * Java 1.7 
		      *  JavaRDD<BookData> bookDataRDD = tradeDataRDD.map(new Function<TradeData,BookData>(){
		    	  
		    	  public BookData call(TradeData tradeData) {
		    	      return new BookData(tradeData.getbusinessdate(), tradeData.getbookid(), tradeData.getInstance(), tradeData.getrisktypeid(), tradeData.getAmt());
		    	    }
		      });*/
		        
		        JavaPairRDD<BookData, Double> bookDataAmtPairedRDD = bookDataRDD.mapToPair(bookData-> new Tuple2<BookData, Double>(bookData,bookData.getAmt()) );
		        /*
		        bookDataRDD.mapToPair(new PairFunction<BookData, BookData, Double>() {

					@Override
					public Tuple2<BookData, Double> call(BookData arg0)
							throws Exception {
						return new Tuple2(arg0,arg0.getAmt());
					}
				});
	*/	      
		        
		        JavaPairRDD<BookData, Double> bookDataAmtPairedReducedRDD =  bookDataAmtPairedRDD.reduceByKey(Double::sum);
		        
		        JavaRDD<BookData> finalBookDataRDD = bookDataAmtPairedReducedRDD.map(s->new BookData(s._1).setAmt(s._2));
		       
		        javaFunctions(finalBookDataRDD).writerBuilder("tradesensstorage", "booklevelstorage", mapToRow(BookData.class)).saveToCassandra();

		 }
		 
	}
	private void generateData(JavaSparkContext sc) {
	        CassandraConnector connector = CassandraConnector.apply(sc.getConf());

	        // Prepare the schema
	        try (Session session = connector.openSession()) {
	            session.execute("DROP KEYSPACE IF EXISTS tradesensstorage");
	            session.execute("CREATE KEYSPACE tradesensstorage WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
	            session.execute("CREATE TABLE tradesensstorage.tradelevelstorage (businessdate TIMESTAMP,bookid int,utid text,risktypeid int,amt double,PRIMARY KEY((businessdate,bookid),utid,risktypeid))");
	            session.execute("CREATE TABLE tradesensstorage.booklevelstorage  (businessdate TIMESTAMP,bookid int,risktypeid int,amt double,PRIMARY KEY((businessdate,bookid),risktypeid))");
		       
	            System.out.println("****************KEYSPACE AND TABLES CREATED***************");
	        }
		        final SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd"); 
		        // Prepare the data at trade level
		        List<TradeData> tradeData;
				try {
					tradeData = Arrays.asList(
					        new TradeData(ft.parse("2015-07-02"), 234234,"ICE@12345",1013,110.50),
					        new TradeData(ft.parse("2015-07-02"), 234234,"ICE@12346",1013,10.50),
					        new TradeData(ft.parse("2015-07-02"), 334235,"ICE@12347",1013,19.00),
					        new TradeData(ft.parse("2015-07-02"), 334235,"ICE@12348",1013,11.00),
					        new TradeData(ft.parse("2015-07-02"), 334235,"ICE@12348",3003,211.00),
					        new TradeData(ft.parse("2015-07-02"), 334235,"ICE@12347",3003,411.00));
				
		             
		        JavaRDD<TradeData> tradeDataRDD = sc.parallelize(tradeData);
		        javaFunctions(tradeDataRDD).writerBuilder("tradesensstorage", "tradelevelstorage", mapToRow(TradeData.class)).saveToCassandra();
		        System.out.println("*******TRADE LEVEL STORAGE DONE***********");
	     	        
				}catch (ParseException e) {
						
						e.printStackTrace();
					}
		
			 
			}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err
					.println("Syntax: com.datastax.spark.demo.JavaDemo <Spark Master URL> <Cassandra contact point>");
			System.exit(1);
		}

		SparkConf conf = new SparkConf();
		conf.setAppName("Java API demo");
		conf.setMaster(args[0]);
		conf.set("spark.cassandra.connection.host", args[1]);

		SparkCassandraApp app = new SparkCassandraApp(conf);
		app.run();
	}
}
