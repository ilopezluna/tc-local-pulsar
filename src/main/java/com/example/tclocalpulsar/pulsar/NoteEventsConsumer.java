package com.example.tclocalpulsar.pulsar;

import com.example.tclocalpulsar.Event;
import com.example.tclocalpulsar.EventRepository;
import com.example.tclocalpulsar.pulsar.events.NoteEvent;
import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class NoteEventsConsumer {

    final EventRepository eventRepository;
    final ConversionService conversionService;

    @ReactivePulsarListener(consumerCustomizer = "noteEventsConsumerCustomizer")
    Mono<Void> listen(byte[] message) {
        return Mono
            .fromSupplier(() -> conversionService.convert(message, CloudEvent.class))
            .flatMap(event -> handle(event, conversionService.convert(message, NoteEvent.class)));
    }

    private Mono<Void> handle(CloudEvent cloudEvent, NoteEvent noteEvent) {
        return Mono.just(new Event(cloudEvent.getId(), noteEvent.noteId())).flatMap(eventRepository::save).then();
    }
}
