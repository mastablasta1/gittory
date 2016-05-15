package pl.edu.agh.idziak.gittory.logic;

/**
 * Created by Tomasz on 15.05.2016.
 */
public class ActiveCodeSpan {

    private final int startCol;
    private final int endCol;
    private final int line;

    private ActiveCodeSpan(Builder builder) {
        startCol = builder.startCol;
        endCol = builder.endCol;
        line = builder.line;
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

    public static final class Builder {
        private int startCol;
        private int endCol;
        private int line;

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
    }
}
