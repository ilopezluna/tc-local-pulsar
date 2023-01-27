package com.example.tclocaldev;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoteControllerTest extends AbstractIntegrationTest {

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
        var text = "test note";
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
    }
}
