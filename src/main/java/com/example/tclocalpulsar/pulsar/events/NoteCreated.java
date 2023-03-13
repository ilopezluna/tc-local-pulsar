package com.example.tclocalpulsar.pulsar.events;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record NoteCreated(Long noteId) implements NoteEvent {}
