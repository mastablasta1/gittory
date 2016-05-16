package pl.edu.agh.idziak.gittory.logic;

import javafx.geometry.Point2D;

import java.util.function.Consumer;

/**
 * Created by Tomasz on 15.05.2016.
 */
public class ActiveCodeSpan {

    private final int startCol;
    private final int endCol;
    private final int line;
    private final Consumer<Point2D> callback;

    private ActiveCodeSpan(Builder builder) {
        startCol = builder.startCol;
        endCol = builder.endCol;
        line = builder.line;
        callback = builder.callback;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getStartCol() {
        return startCol;
    }

    public int getEndCol() {
        return endCol;
    }

    public int getLine() {
        return line;
    }

    public Consumer<Point2D> getCallback() {
        return callback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveCodeSpan that = (ActiveCodeSpan) o;
        return startCol == that.startCol && endCol == that.endCol && line == that.line;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public static final class Builder {
        private int startCol;
        private int endCol;
        private int line;
        private Consumer<Point2D> callback;

        private Builder() {
        }

        public Builder startCol(int val) {
            startCol = val;
            return this;
        }

        public Builder endCol(int val) {
            endCol = val;
            return this;
        }

        public Builder line(int val) {
            line = val;
            return this;
        }

        public ActiveCodeSpan build() {
            return new ActiveCodeSpan(this);
        }

        public Builder clickCallback(Consumer<Point2D> screenPointConsumer) {
            this.callback = screenPointConsumer;
            return this;
        }
    }

    @Override
    public String toString() {
        return "ActiveCodeSpan{" +
                "startCol=" + startCol +
                ", endCol=" + endCol +
                ", line=" + line +
                '}';
    }
}
