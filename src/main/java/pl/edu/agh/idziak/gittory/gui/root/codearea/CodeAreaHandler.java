package pl.edu.agh.idziak.gittory.gui.root.codearea;

import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.gittory.logic.ActiveCodeSpan;
import pl.edu.agh.idziak.gittory.logic.StringLayout;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomasz on 15.05.2016.
 */
public class CodeAreaHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CodeAreaHandler.class);

    private CodeArea codeArea;
    private ArrayList<ActiveCodeSpan> activeCodeSpans;
    private boolean highlightJavaCode;

    public CodeAreaHandler(StackPane codeAreaStackPane) {
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeAreaStackPane.getChildren().add(new VirtualizedScrollPane<>(codeArea));
        codeArea.setEditable(false);

        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
    }

    public void replaceWithPlainText(String newText) {
        this.highlightJavaCode = false;
        codeArea.replaceText(newText);
    }

    public void replaceWithActiveJavaCode(String fileContent, List<ActiveCodeSpan> spans) {
        highlightJavaCode = true;
        activeCodeSpans = new ArrayList<>(spans);
        codeArea.replaceText(fileContent);
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        if (highlightJavaCode) {
            buildJavaStyleSpans(text, spansBuilder);
        } else {
            spansBuilder.add(Collections.emptyList(), text.length());
        }
        return spansBuilder.create();
    }

    private void buildJavaStyleSpans(String text, StyleSpansBuilder<Collection<String>> spansBuilder) {
        StringLayout stringLayout = new StringLayout(text);
        Matcher matcher = PATTERN.matcher(text);
        int lastKeywordEnd = 0;
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            matcher.group("QUALIFIER") != null ? "qualifier" :
                                                                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKeywordEnd);

            if (styleClass.equals("qualifier")) {
                if (isActiveCodeSpan(matcher.start(), matcher.end())) {
                    styleClass = "active-qualifier";
                }
            }
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKeywordEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKeywordEnd);
    }

    private boolean isActiveCodeSpan(int start, int end) {
        Optional<ActiveCodeSpan> span = activeCodeSpans.stream()
                .filter(s -> s.getStartCol() == start && s.getEndCol() == end)
                .findAny();
        return span.isPresent();
    }

    private static final String[] JAVA_KEYWORDS = new String[]{
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", JAVA_KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String QUALIFIER_PATTERN = "\\b\\w+\\b";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<QUALIFIER>" + QUALIFIER_PATTERN + ")"
    );
}
