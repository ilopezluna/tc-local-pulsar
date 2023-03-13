package com.example.tclocalpulsar;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EventRepository extends ReactiveCrudRepository<Event, Long> {
    Mono<Event> findByNoteId(Long noteId);
}
