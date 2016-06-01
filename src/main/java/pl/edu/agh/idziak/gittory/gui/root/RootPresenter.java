package pl.edu.agh.idziak.gittory.gui.root;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.gittory.gui.root.codearea.CodeAreaHandler;
import pl.edu.agh.idziak.gittory.gui.root.findusages.FindUsagesWindowsManager;
import pl.edu.agh.idziak.gittory.gui.root.repotree.ItemContent;
import pl.edu.agh.idziak.gittory.gui.root.repotree.RepoTreeViewHandler;
import pl.edu.agh.idziak.gittory.logic.*;
import pl.edu.agh.idziak.gittory.logic.findusages.FindUsagesExecutor;
import pl.edu.agh.idziak.gittory.logic.findusages.FindUsagesOperation;
import pl.edu.agh.idziak.gittory.logic.findusages.MethodUsageViewHandle;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * Created by Tomasz on 13.05.2016.
 */
public class RootPresenter implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(RootPresenter.class);

    @FXML
    public Label labelFileInfo;
    @FXML
    private BorderPane rightBorderPane;
    @FXML
    private StackPane codeAreaStackPane;
    @FXML
    private TreeView<ItemContent> repositoryTreeView;
    @FXML
    private Button buttonOpenRepo;

    @Inject
    private RepositoryService repositoryService;

    private final FindUsagesExecutor findUsagesExecutor;

    private RepoTreeViewHandler repoTreeViewHandler;
    private FindUsagesWindowsManager findUsagesWindowsManager;
    private CodeAreaHandler codeAreaHandler;

    private TreeItem<ItemContent> currentlyActiveFile;

    public RootPresenter() {
        MethodUsageViewHandle viewHandle = MethodUsageViewHandle.newBuilder()
                .doubleClickCallback(param -> {
                    repoTreeViewHandler.setSelectedItem(param.getItemContent());
                    displayFileContent(param.getItemContent());
                    codeAreaHandler.selectLine(param.getLine());
                    getScene().getWindow().requestFocus();
                    return null;
                }).build();

        findUsagesExecutor = new FindUsagesExecutor(viewHandle);
    }

    private Scene getScene() {
        return codeAreaStackPane.getScene();
    }

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
                displayFileContent(event.getPayload());
            }
        });

        findUsagesWindowsManager = new FindUsagesWindowsManager();
    }

    private void displayFileContent(ItemContent item) {
        if (item.isJavaFile()) {
            ActiveCodeSpansBuilder builder = new ActiveCodeSpansBuilder();

            CompilationUnit compUnit = item.getCompilationUnit();
            if (compUnit != null) {
                compUnit.accept(new CodeVisitor(), builder);
            }
            codeAreaHandler.replaceWithActiveJavaCode(item.getFileContent(), builder.getActiveCodeSpans());
        } else {
            codeAreaHandler.replaceWithPlainText(item.getFileContent());
        }

        showFileInfo(item);
        currentlyActiveFile = item.getTreeItem();
    }

    private void showFileInfo(ItemContent item) {
        try {
            failableShowFileInfo(item);
        } catch (Exception e) {
            labelFileInfo.setText("(Could not fetch item info)");
        }
    }

    private void failableShowFileInfo(ItemContent item) throws IOException {
        Repository jgitRepo = item.getRepositoryHandle().getRepository();
        RevWalk revCommits = new RevWalk(jgitRepo);
        RevCommit commit = revCommits.parseCommit(jgitRepo.resolve(Constants.HEAD));

        try {
            Git git = new Git(jgitRepo);
            String repoPath = item.getRepositoryHandle().getRepository().getWorkTree().toURI().relativize(item.getFile().toURI()).getPath();
            Iterable<RevCommit> commits = git.log().addPath(repoPath).call();

            Iterator<RevCommit> iterator = commits.iterator();
            if (iterator.hasNext()) {
                RevCommit lastCommit = iterator.next();
                PersonIdent authorIdent = lastCommit.getAuthorIdent();
                String name = authorIdent.getName();
                Date when = authorIdent.getWhen();
                labelFileInfo.setText("Last commit by " + name + " on " + new SimpleDateFormat("hh:mm dd.MM.yyyy").format(when));
            } else {
                labelFileInfo.setText("File not commited");
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }


    }

    private class CodeVisitor extends VoidVisitorAdapter<ActiveCodeSpansBuilder> {
        @Override
        public void visit(MethodDeclaration methodDecl, ActiveCodeSpansBuilder builder) {
            NameExpr expr = methodDecl.getNameExpr();
            ActiveCodeSpan span = ActiveCodeSpan.builder()
                    .startCol(expr.getBeginColumn())
                    .endCol(expr.getEndColumn())
                    .line(expr.getBeginLine())
                    .clickCallback((point2D) -> RootPresenter.this.handleFindUsagesOfMethod(methodDecl))
                    .build();
            assert expr.getBeginLine() == expr.getEndLine();
            builder.addActiveCodeSpan(span);
            super.visit(methodDecl, builder);
        }
    }

    private void handleFindUsagesOfMethod(MethodDeclaration n) {
        FindUsagesOperation operation = FindUsagesOperation.builder().methodDeclaration(n).treeItem(currentlyActiveFile).build();
        findUsagesExecutor.executeOperation(operation);
        findUsagesWindowsManager.showFindUsagesWindow(operation);
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
