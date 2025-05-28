package poc.query.stream.results;


import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class QueryEngineServiceImpl implements QueryEngineService {
    private static final Logger logger = LoggerFactory.getLogger(QueryEngineServiceImpl.class);

    @Override
    public CompletableFuture<Stream<QueryResult>> executeQuery(String query) {
        logger.info("Executing query: \"{}\"", query);
        // Simulate fetching data from CData Query engine/ Connector
        return CompletableFuture.supplyAsync(() -> {
            List<QueryResult> results = new ArrayList<>();
            // process 5 rows of results
            IntStream.range(0, 5).forEach(i -> {
                QueryResult row = new QueryResult();
                String id = UUID.randomUUID().toString();
                row.addField("id", id);
                row.addField("query_ref", query.hashCode() + "_" + i);
                row.addField("event_type", "sample_event_" + i);
                row.addField("value", Math.random() * 100);
                row.addField("timestamp", System.currentTimeMillis());
                results.add(row);
                try {
                    Thread.sleep(100); // add delay in fetching each row
                } catch (InterruptedException e) {
                    logger.warn("Thread interrupted during data generation for query: {}", query, e);
                    // ExceptionHandler code here
                }
            });
            logger.info("QueryEngineService: Finished generating {} mock results for query: \"{}\"", results.size(), query);
            return results.stream();
        });
    }
}