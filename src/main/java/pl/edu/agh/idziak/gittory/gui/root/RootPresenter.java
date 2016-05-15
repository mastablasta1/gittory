package pl.edu.agh.idziak.gittory.gui.root;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.gittory.gui.root.codearea.CodeAreaHandler;
import pl.edu.agh.idziak.gittory.gui.root.repotree.RepoTreeItem;
import pl.edu.agh.idziak.gittory.gui.root.repotree.RepoTreeViewHandler;
import pl.edu.agh.idziak.gittory.logic.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Tomasz on 13.05.2016.
 */
public class RootPresenter implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(RootPresenter.class);

    @FXML
    private StackPane codeAreaStackPane;

    @FXML
    private TreeView<RepoTreeItem> repositoryTreeView;

    @FXML
    private Button buttonOpenRepo;

    @Inject
    private RepositoryService repositoryService;

    private RepoTreeViewHandler repoTreeViewHandler;

    private CodeAreaHandler codeAreaHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        repoTreeViewHandler = new RepoTreeViewHandler(repositoryTreeView, repositoryService);

        buttonOpenRepo.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Open Git repository");
            File dir = directoryChooser.showDialog(buttonOpenRepo.getScene().getWindow());
            if (dir == null)
                return;
            loadRepositoryIntoService(dir);
        });

        codeAreaHandler = new CodeAreaHandler(codeAreaStackPane);

        repoTreeViewHandler.addFileEventListener(event -> {
            if (event.getType() == Event.Type.FILE_DOUBLE_CLICKED) {
                String fileContent = readFileContent(event.getPayload());
                presentFileContent(fileContent, event.getPayload());
            }
        });
    }

    private void presentFileContent(String fileContent, File file) {
        boolean isJavaFile = file.getName().endsWith(".java");

        if (isJavaFile) {
            ActiveCodeSpansBuilder builder = new ActiveCodeSpansBuilder(new StringLayout(fileContent));
            try {
                CompilationUnit cu = JavaParser.parse(new StringReader(fileContent), true);
                cu.accept(new CodeVisitor(), builder);
            } catch (ParseException e) {
                LOG.info("Parse error", e);
            }

            codeAreaHandler.replaceWithActiveJavaCode(fileContent, builder.activeCodeSpans);
        } else {
            codeAreaHandler.replaceWithPlainText(fileContent);
        }
    }

    private class CodeVisitor extends VoidVisitorAdapter<ActiveCodeSpansBuilder> {
        @Override
        public void visit(MethodDeclaration n, ActiveCodeSpansBuilder builder) {
            NameExpr nameExpr = n.getNameExpr();
            int startPos = builder.stringLayout.toGlobalPosition(nameExpr.getBeginLine(), nameExpr.getBeginColumn());
            int endPos = builder.stringLayout.toGlobalPosition(nameExpr.getEndLine(), nameExpr.getEndColumn());
            ActiveCodeSpan span = ActiveCodeSpan.builder().startCol(startPos).endCol(endPos).build();
            builder.activeCodeSpans.add(span);
            super.visit(n, builder);
        }
    }

    private static class ActiveCodeSpansBuilder {
        private List<ActiveCodeSpan> activeCodeSpans = new LinkedList<>();
        private StringLayout stringLayout;

        public ActiveCodeSpansBuilder(StringLayout stringLayout) {
            this.stringLayout = stringLayout;
        }
    }

    private String readFileContent(File file) {
        try {
            return Files.toString(file, StandardCharsets.UTF_8).replaceAll("\r","");
        } catch (IOException e) {
            LOG.error("Could not load file", e);
            Throwables.propagate(e);
        }
        return null;
    }

    private void loadRepositoryIntoService(File dir) {
        try {
            RepositoryHandle repo = RepositoryService.loadGitRepository(dir);
            repositoryService.addRepository(repo);
        } catch (IOException e) {
            LOG.error("Could not load repo", e);
        }
    }
}
