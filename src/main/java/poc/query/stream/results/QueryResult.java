package poc.query.stream.results;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Getter
@JsonSerialize
public class QueryResult {
    private static final Logger logger = LoggerFactory.getLogger(QueryResult.class);
    private final Map<String, Object> resultData;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public QueryResult() {
        this.resultData = new HashMap<>();
    }

    public void addField(String columnName, Object value) {
        this.resultData.put(columnName, value);
    }

    
    @Override
    public String toString() {
        return "QueryResult{" +
                "data=" + resultData +
                '}';
    }
}
