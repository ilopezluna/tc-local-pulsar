package com.example.tclocalpulsar;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface NoteRepository extends ReactiveCrudRepository<Note, Long> {
    Mono<Note> findByText(String text);
}
