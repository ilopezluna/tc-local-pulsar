package com.example.tclocalpulsar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class NoteEventsConsumer {

    final EventRepository eventRepository;

    @ReactivePulsarListener(topics = Application.TOPIC, schemaType = SchemaType.JSON)
    Mono<Void> listen(Event event) {
        log.info("Received event: {}", event);
        return eventRepository.save(event).then();
    }
}