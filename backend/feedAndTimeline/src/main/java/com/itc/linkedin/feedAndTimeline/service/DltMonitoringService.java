package com.itc.linkedin.feedAndTimeline.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DltMonitoringService {

    private static final List<String> DLT_TOPICS = List.of(
            "post.created.dlt",
            "post.deleted.dlt",
            "post.liked.dlt",
            "comment.created.dlt"
    );

    private final AdminClient adminClient;
    private final ProcessedEventService processedEventService;

    public Map<String, Object> getDltStatus() throws Exception {
        Map<String, TopicDescription> descriptions =
                adminClient.describeTopics(DLT_TOPICS).allTopicNames().get();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("consumer", processedEventService.consumerName());

        Map<String, Object> topics = new LinkedHashMap<>();
        for (String topic : DLT_TOPICS) {
            TopicDescription description = descriptions.get(topic);
            long messageCount = endOffsetSum(topic, description.partitions().size());

            Map<String, Object> topicStatus = new LinkedHashMap<>();
            topicStatus.put("partitions", description.partitions().size());
            topicStatus.put("messages", messageCount);
            topicStatus.put("processedPrimaryEvents", processedEventService.countProcessedForTopic(baseTopic(topic)));

            topics.put(topic, topicStatus);
        }

        response.put("topics", topics);
        return response;
    }

    private long endOffsetSum(String topic, int partitionCount) throws Exception {
        Map<TopicPartition, OffsetSpec> requests = new LinkedHashMap<>();
        for (int partition = 0; partition < partitionCount; partition++) {
            requests.put(new TopicPartition(topic, partition), OffsetSpec.latest());
        }

        ListOffsetsResult result = adminClient.listOffsets(requests);

        long total = 0L;
        for (TopicPartition partition : requests.keySet()) {
            total += result.partitionResult(partition).get().offset();
        }
        return total;
    }

    private String baseTopic(String dltTopic) {
        return dltTopic.replace(".dlt", "");
    }
}
