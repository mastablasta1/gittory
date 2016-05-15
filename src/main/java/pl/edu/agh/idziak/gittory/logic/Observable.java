package pl.edu.agh.idziak.gittory.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class Observable<T> {

    private static final Logger LOG = LoggerFactory.getLogger(Observable.class);

    private Set<Consumer<T>> consumers = new HashSet<>();

    public void addObserver(Consumer<T> consumer) {
        consumers.add(consumer);
    }

    public void removeObserver(Consumer<T> consumer) {
        consumers.remove(consumer);
    }

    public void publishEvent(T event) {
        consumers.forEach(consumer -> {
            try {
                consumer.accept(event);
            } catch (RuntimeException e) {
                LOG.error("Observer threw error", e);
            }
        });
    }

}
