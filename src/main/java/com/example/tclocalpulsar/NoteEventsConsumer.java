package com.example.tclocalpulsar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class NoteEventsConsumer {

    final EventRepository eventRepository;

    @ReactivePulsarListener(topics = Application.TOPIC)
    Mono<Void> listen(Long noteId) {
        log.info("Received noteId: {}", noteId);
        var event = new Event();
        event.setNoteId(noteId);
        return eventRepository.save(event).then();
    }
}
