package com.example.tclocalpulsar.pulsar;

import com.example.tclocalpulsar.pulsar.events.NoteEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.pulsar.reactive.core.ReactivePulsarTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class NoteEventsProducer {

    @Value("${spring.pulsar.producer.topic-name}")
    String topic;

    final ReactivePulsarTemplate<byte[]> template;
    final Clock clock;
    final ObjectMapper objectMapper;

    public Mono<MessageId> sendNoteEvent(NoteEvent noteEvent) {
        return Mono
            .fromCallable(() -> {
                var cloudEvent = CloudEventBuilder
                    .v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create("urn:" + noteEvent.getClass().getPackageName()))
                    .withType(noteEvent.getClass().toString())
                    .withTime(OffsetDateTime.now(clock))
                    .withData(PojoCloudEventData.wrap(noteEvent, objectMapper::writeValueAsBytes))
                    .build();

                return EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE).serialize(cloudEvent);
            })
            .doOnNext(ignored -> log.info("Event about to be published: {}", noteEvent))
            .flatMap(message ->
                template
                    .newMessage(message)
                    .withMessageCustomizer(msg -> msg.key(noteEvent.noteId().toString()))
                    .withSenderCustomizer(s -> s.maxInflight(100))
                    .withTopic(topic)
                    .send()
            );
    }
}
