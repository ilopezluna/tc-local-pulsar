package com.example.tclocalpulsar.pulsar.events;

import lombok.extern.jackson.Jacksonized;

@Jacksonized
public record Unknown(Long noteId) implements NoteEvent {}
