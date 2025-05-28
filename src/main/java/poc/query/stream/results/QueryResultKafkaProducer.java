package poc.query.stream.results;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class QueryResultKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(QueryResultKafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName;
    private final QueryEngineServiceImpl queryEngineService;

    @Autowired
    public QueryResultKafkaProducer(KafkaTemplate<String, String> kafkaTemplate,
                                    @Value("${app.kafka.topic-name}") String topicName,
                                    QueryEngineServiceImpl queryEngineService) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
        this.queryEngineService = queryEngineService;
        logger.info("QueryResultKafkaProducer initialized. Producing to topic: '{}'", topicName);
    }

    public void streamQueryResultsToKafka(String query, String messageKeyPrefix) {
        logger.info("Request received to stream results for query: \"{}\" with key prefix: \"{}\"", query, messageKeyPrefix);
        queryEngineService.executeQuery(query).thenAccept(resultsStream -> {
            logger.info("Received results stream for query: \"{}\". Starting to produce to Kafka topic: {}", query, topicName);
            resultsStream.forEach(queryResult -> {
                Object queryResultId = queryResult.getResultData().get("id"); // Assuming that a filed 'id' exists
                String key = (messageKeyPrefix != null ? messageKeyPrefix + "_" : "") +
                        (queryResultId != null ? queryResultId.toString() : UUID.randomUUID().toString());
                String queryResultJson = queryResult.toJson();

                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, key, queryResultJson);

                CompletableFuture<SendResult<String, String>> completableFuture = kafkaTemplate.send(producerRecord);

                completableFuture.whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        logger.error("Error sending record to Kafka with key {}: {}", producerRecord.key(), throwable.getMessage(), throwable);
                        //  error handling: retry, DLQ, alerting
                    } else {
                        RecordMetadata recordMetadata = sendResult.getRecordMetadata();
                        logger.info("Sent record successfully: topic={}, partition={}, offset={}, key={}, timestamp={}",
                                recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), producerRecord.key(), recordMetadata.timestamp());
                    }
                });
            });
            logger.info("All messages from the current batch for query \"{}\" submitted to KafkaTemplate.", query);
        }).exceptionally(ex -> {
            logger.error("Failed to execute query \"{}\" or process its stream: {}", query, ex.getMessage(), ex);
            // Handle query execution failure
            return null;
        });
    }
}