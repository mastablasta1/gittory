package pl.edu.agh.idziak.gittory.logic;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class Event<T> {

    private final T payload;
    private final Type type;

    private Event(Builder<T> builder) {
        payload = builder.payload;
        type = builder.type;
    }

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public Type getType() {
        return type;
    }

    public T getPayload() {
        return payload;
    }

    public enum Type {
        FILE_DOUBLE_CLICKED, REPOSITORIES_CHANGED
    }


    public static final class Builder<T> {
        private Type type;
        private T payload;

        private Builder() {
        }

        public Builder<T> type(Type val) {
            type = val;
            return this;
        }

        public Builder<T> payload(T val) {
            payload = val;
            return this;
        }

        public Event<T> build() {
            return new Event<>(this);
        }
    }
}
