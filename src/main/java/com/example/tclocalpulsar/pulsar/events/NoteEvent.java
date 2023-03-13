package com.example.tclocalpulsar.pulsar.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Unknown.class)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = NoteCreated.class, name = "note_created"),
        @JsonSubTypes.Type(value = RemoveNote.class, name = "note_removed"),
    }
)
@JsonDeserialize(as = NoteCreated.class)
public sealed interface NoteEvent permits NoteCreated, RemoveNote, Unknown {
    Long noteId();
}
