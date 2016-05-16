package pl.edu.agh.idziak.gittory.util;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz on 15.05.2016.
 */
public class StringLayout {

    private List<Pair<Integer, Integer>> lineList;
    private String string;

    public StringLayout(String str) {
        string = str;

        String[] lines = string.split("(?<=\n)");
        lineList = new ArrayList<>(lines.length);

        int pos = 0;
        for (String line : lines) {
            int lineLength = line.length();
            lineList.add(new Pair<>(pos, lineLength));
            pos += lineLength;
        }
    }

    public int linesCount() {
        return lineList.size();
    }

    public String getString() {
        return string;
    }

    public Pair<Integer, Integer> getLineAndColumn(int pos) {
        int i = 1;
        for (Pair<Integer, Integer> lineAndCol : lineList) {
            Integer linePos = lineAndCol.getKey();
            Integer lineLength = lineAndCol.getValue();
            if (linePos + lineLength > pos) {
                return new Pair<>(i, pos - linePos + 1);
            }
            i++;
        }
        throw new IllegalArgumentException("Position exceeds string length");
    }
}
