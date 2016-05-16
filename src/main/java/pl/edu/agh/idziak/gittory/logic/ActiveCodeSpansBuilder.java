package pl.edu.agh.idziak.gittory.logic;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tomasz on 16.05.2016.
 */
public class ActiveCodeSpansBuilder {
    private List<ActiveCodeSpan> activeCodeSpans = new LinkedList<>();

    public void addActiveCodeSpan(ActiveCodeSpan span) {
        activeCodeSpans.add(span);
    }

    public List<ActiveCodeSpan> getActiveCodeSpans() {
        return activeCodeSpans;
    }
}
