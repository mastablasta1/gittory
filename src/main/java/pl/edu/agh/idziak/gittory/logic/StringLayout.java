package pl.edu.agh.idziak.gittory.logic;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tomasz on 15.05.2016.
 */
public class StringLayout {

    private List<Pair<Integer, Integer>> lineList;
    private String string;

    public StringLayout(String str) {
        string = str;

        lineList = new LinkedList<>();
        String[] lines = string.split("(?<=\n)");

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

    public int toGlobalPosition(int row, int col) {
        Pair<Integer, Integer> lineInfo = lineList.get(row - 1);
        if (lineInfo == null)
            throw new IllegalArgumentException("No line with nr " + row);
        if (lineInfo.getValue() <= col)
            throw new IllegalArgumentException("Column exceeds accual line length " + lineInfo.getValue() + "/" + col);
        return lineInfo.getKey() + col - 1;
    }
}
