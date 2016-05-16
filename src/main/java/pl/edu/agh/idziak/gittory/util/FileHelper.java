package pl.edu.agh.idziak.gittory.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Created by Tomasz on 16.05.2016.
 */
public class FileHelper {
    private static final Logger LOG = LoggerFactory.getLogger(FileHelper.class);

    public static Optional<String> safelyReadFileContent(File file) {
        return Optional.ofNullable(readFileContent(file, false));
    }

    public static String readFileContent(File file) {
        return readFileContent(file, true);
    }

    private static String readFileContent(File file, boolean throwException) {
        try {
            return Files.toString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("Could not load file", e);
            if (throwException)
                throw new RuntimeException("Could not read file", e);
            return null;
        }
    }


    public static Optional<CompilationUnit> parseJava(File file) {
        try {
            return Optional.of(JavaParser.parse(file));
        } catch (ParseException | IOException e) {
            LOG.info("Could not parse java file", e);
            return Optional.empty();
        }
    }
}
