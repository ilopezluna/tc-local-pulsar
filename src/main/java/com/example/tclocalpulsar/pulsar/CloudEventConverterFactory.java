package com.example.tclocalpulsar.pulsar;

import com.example.tclocalpulsar.pulsar.events.NoteEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CloudEventConverterFactory implements GenericConverter {

    final ObjectMapper objectMapper;

    final Map<Class<?>, PojoCloudEventDataMapper<?>> supported;

    @Autowired
    public CloudEventConverterFactory(ObjectMapper objectMapper) {
        this(objectMapper, Set.of(NoteEvent.class));
    }

    public CloudEventConverterFactory(ObjectMapper objectMapper, Set<Class<?>> supported) {
        this.objectMapper = objectMapper;
        this.supported =
            supported
                .stream()
                .collect(Collectors.toMap(type -> type, type -> PojoCloudEventDataMapper.from(objectMapper, type)));
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Stream
            .concat(
                supported
                    .keySet()
                    .stream()
                    .flatMap(clazz -> {
                        return Stream.of(
                            new ConvertiblePair(byte[].class, clazz),
                            new ConvertiblePair(CloudEvent.class, clazz)
                        );
                    }),
                Stream.of(new ConvertiblePair(byte[].class, CloudEvent.class))
            )
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        CloudEvent event = null;

        if (source instanceof CloudEvent ce) {
            event = ce;
        }

        if (source instanceof byte[] bytes) {
            try {
                event = objectMapper.readValue(bytes, CloudEvent.class);
            } catch (IOException e) {
                throw new IllegalArgumentException("invalid source to convert to CloudEvent", e);
            }
        }

        if (targetType.getType() == CloudEvent.class) {
            return event;
        }

        if (supported.containsKey(targetType.getType())) {
            return Optional
                .ofNullable(CloudEventUtils.mapData(event, supported.get(targetType.getType())))
                .map(PojoCloudEventData::getValue)
                .orElse(null);
        }

        return null;
    }
}
