package poc.query.stream.results;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

interface QueryEngineService {
    CompletableFuture<Stream<QueryResult>> executeQuery(String query);
}


