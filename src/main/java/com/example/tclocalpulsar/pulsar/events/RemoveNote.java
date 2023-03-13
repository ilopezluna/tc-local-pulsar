package com.example.tclocalpulsar.pulsar.events;

import lombok.extern.jackson.Jacksonized;

@Jacksonized
public record RemoveNote(Long noteId) implements NoteEvent {}
