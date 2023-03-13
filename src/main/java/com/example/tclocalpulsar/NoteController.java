package com.example.tclocalpulsar;

import com.example.tclocalpulsar.pulsar.NoteEventsProducer;
import com.example.tclocalpulsar.pulsar.events.NoteCreated;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class NoteController {

    public static final String URI = "/notes";

    final NoteRepository repository;
    final NoteEventsProducer noteEventsProducer;

    @GetMapping(URI)
    Flux<Note> findAll() {
        return repository.findAll();
    }

    @PostMapping(URI)
    Mono<Note> save(@RequestBody Note note) {
        return repository
            .save(note)
            .delayUntil(stored -> noteEventsProducer.sendNoteEvent(NoteCreated.builder().noteId(stored.getId()).build())
            );
    }
}
