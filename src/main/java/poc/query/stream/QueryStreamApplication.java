package poc.query.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import poc.query.stream.results.QueryResultKafkaProducer;

@SpringBootApplication
public class QueryStreamApplication  implements CommandLineRunner {
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(QueryStreamApplication.class);
	@Autowired
	private QueryResultKafkaProducer resultsProducer;

	public static void main(String[] args) {
		SpringApplication.run(QueryStreamApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		String sampleQuery = "SELECT * FROM important_events WHERE type='INTERESTING_EVENT'";
		String messageKeyPrefix = "interesting_event";

		try {
			logger.info("StreamingDemo Application started, initiating streamQueryResultsToKafka...");
			resultsProducer.streamQueryResultsToKafka(sampleQuery, messageKeyPrefix);

			logger.info("streamQueryResultsToKafka has been called for query: \"{}\". Asynchronous send operations are in progress. Check logs for details.", sampleQuery);

		} catch (Exception e) {
			logger.error("An error occurred during the StreamingDemo execution: {}", e.getMessage(), e);
		}
		// The app will shut down after this method completes if no other threads are running.
	}

}
