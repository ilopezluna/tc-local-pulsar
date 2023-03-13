package com.example.tclocalpulsar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class NoteControllerTest extends AbstractIntegrationTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    NoteRepository noteRepository;

    @Test
    void findAll() {
        webTestClient
            .get()
            .uri(NoteController.URI)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Note.class)
            .value(notes -> assertThat(notes).isEmpty());
    }

    @Test
    void save() {
        var note = new Note();
        var text = RandomStringUtils.randomAlphabetic(10);
        note.setText(text);
        webTestClient
            .post()
            .uri(NoteController.URI)
            .bodyValue(note)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Note.class)
            .value(stored -> assertThat(stored).isNotNull().extracting(Note::getText).isEqualTo(text));

        StepVerifier
            .create(
                noteRepository.findByText(text).flatMap(noteFromDB -> eventRepository.findByNoteId(noteFromDB.getId()))
            )
            .expectNextCount(1)
            .verifyComplete();
    }
}
