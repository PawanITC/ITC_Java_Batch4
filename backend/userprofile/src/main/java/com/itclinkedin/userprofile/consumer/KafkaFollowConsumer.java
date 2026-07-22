package com.itclinkedin.userprofile.consumer;

import com.itclinkedin.userprofile.events.UserFollowedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaFollowConsumer {

    @KafkaListener(topics = "social-follow-events", groupId = "linkedin-profile-debug-group")
    public void receive(@Payload UserFollowedEvent event,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        String followerFullName = event.getFollowerFirstName() + " " + event.getFollowerLastName();
        String followingFullName = event.getFollowingFirstName() + " " + event.getFollowingLastName();
        
        System.out.println("\n📣 [REAL-TIME SOCIAL ACTIVITY DETECTED]");
        System.out.println("👥 Activity:     " + followerFullName + " (" + event.getFollowerEmail() + ") started following user: " + followingFullName + " (" + event.getFollowingEmail() + ")");
        System.out.println("🗺️ Broker Route: Shard Partition [" + partition + "] using key [" + key + "]");
        System.out.println("🆔 Event ID:     " + event.getEventId());
        System.out.println("=================================================================================\n");
    }
}