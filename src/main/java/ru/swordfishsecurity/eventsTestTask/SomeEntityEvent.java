package ru.swordfishsecurity.eventsTestTask;

import lombok.NonNull;
import lombok.Value;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

/**
 * Event after Some Entity processing
 */
@Value
public class SomeEntityEvent extends ApplicationEvent {
    Long entityId;
    EntityAction entityAction;

    public SomeEntityEvent(Object source, @NonNull Long entityId, @NonNull EntityAction entityAction) {
        super(source);
        this.entityId = entityId;
        this.entityAction = entityAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SomeEntityEvent that = (SomeEntityEvent) o;
        return entityId.equals(that.entityId) && entityAction == that.entityAction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, entityAction);
    }
}
