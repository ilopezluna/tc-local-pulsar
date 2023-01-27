package com.example.tclocalpulsar;

import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.api.Schema;
import org.springframework.pulsar.reactive.core.ReactivePulsarTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.tclocalpulsar.Application.TOPIC;

@RestController
@RequiredArgsConstructor
public class NoteController {

    public static final String URI = "/notes";

    final NoteRepository repository;

    final ReactivePulsarTemplate<Event> pulsarTemplate;

    @GetMapping(URI)
    Flux<Note> findAll() {
        return repository.findAll();
    }

    @PostMapping(URI)
    Mono<Note> save(@RequestBody Note note) {
        return repository
            .save(note)
            .delayUntil(n -> {
                pulsarTemplate.setSchema(Schema.JSON(Event.class));
                var event = new Event();
                event.setNoteId(note.getId());
                return pulsarTemplate.send(TOPIC, event);
            });
    }
}
