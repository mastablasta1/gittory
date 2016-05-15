package pl.edu.agh.idziak.gittory.gui.root.repotree;

import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.gittory.logic.Event;
import pl.edu.agh.idziak.gittory.logic.Observable;
import pl.edu.agh.idziak.gittory.logic.RepositoryHandle;
import pl.edu.agh.idziak.gittory.logic.RepositoryService;

import java.io.File;
import java.io.FilenameFilter;
import java.util.function.Consumer;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class RepoTreeViewHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RepoTreeViewHandler.class);

    private TreeView<RepoTreeItem> treeView;
    private RepositoryService service;
    private Observable<Event<File>> fileEventObservable = new Observable<>();

    public RepoTreeViewHandler(TreeView<RepoTreeItem> treeView, RepositoryService service) {
        this.service = service;
        this.treeView = treeView;

        initView();

        service.addChangeListener(event -> {
            if (event.getType() == Event.Type.REPOSITORIES_CHANGED) {
                updateView();
            }
        });
    }

    private void initView() {
        treeView.setEditable(false);
        treeView.setShowRoot(false);

        treeView.setCellFactory(treeView -> {
            TreeCell<RepoTreeItem> treeCell = new RepoTreeCell();
            treeCell.setOnMouseClicked(getTreeCellMouseEventHandler(treeCell));
            return treeCell;
        });

        TreeItem<RepoTreeItem> root = new TreeItem<>();
        root.setExpanded(true);
        treeView.setRoot(root);

        buildTree();
    }

    public void addFileEventListener(Consumer<Event<File>> observer) {
        fileEventObservable.addObserver(observer);
    }

    private EventHandler<MouseEvent> getTreeCellMouseEventHandler(TreeCell<RepoTreeItem> treeCell) {
        return event -> {
            TreeItem<RepoTreeItem> treeItem = treeCell.getTreeItem();
            if (treeItem == null || treeItem.getValue() == null) {
                return;
            }

            if (event.getClickCount() == 2) {
                File file = treeItem.getValue().getFile();
                if (file.isDirectory()) {
                    listDirectoryItem(treeItem, file);
                } else {
                    fileEventObservable.publishEvent(Event.<File>builder().type(Event.Type.FILE_DOUBLE_CLICKED).payload(file).build());
                }
            }
        };
    }


    private void listDirectoryItem(TreeItem<RepoTreeItem> dirItem, File dir) {
        if (!dir.isDirectory() || !dirItem.getChildren().isEmpty()) {
            return;
        }
        File[] files = dir.listFiles(gitFolderFilter);
        LOG.info("Listed folder " + dir);
        if (files == null) {
            LOG.warn("Could not list folder");
            return;
        }
        dirItem.getChildren().clear();
        for (File f : files) {
            dirItem.getChildren().add(new TreeItem<>(new RepoTreeItem(f)));
        }
        dirItem.setExpanded(true);
    }

    private void buildTree() {
        TreeItem<RepoTreeItem> root = treeView.getRoot();
        root.getChildren().clear();

        for (RepositoryHandle handle : service.getRepositories()) {
            Repository repo = handle.getRepository();
            File workTree = repo.getWorkTree();
            TreeItem<RepoTreeItem> repoRoot = new TreeItem<>(new RepoTreeItem(workTree));
            root.getChildren().add(repoRoot);
        }
    }

    private void updateView() {
        buildTree();
    }

    private static FilenameFilter gitFolderFilter = (dir, name) -> !".git".equals(name);
}
