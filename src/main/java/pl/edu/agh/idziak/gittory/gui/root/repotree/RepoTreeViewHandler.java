package pl.edu.agh.idziak.gittory.gui.root.repotree;

import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.gittory.logic.Event;
import pl.edu.agh.idziak.gittory.util.Observable;
import pl.edu.agh.idziak.gittory.logic.RepositoryHandle;
import pl.edu.agh.idziak.gittory.logic.RepositoryService;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class RepoTreeViewHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RepoTreeViewHandler.class);

    private TreeView<ItemContent> treeView;
    private RepositoryService service;
    private Observable<Event<ItemContent>> fileEventObservable = new Observable<>();

    public RepoTreeViewHandler(TreeView<ItemContent> treeView, RepositoryService service) {
        this.service = service;
        this.treeView = treeView;

        initView();

        service.addChangeListener(event -> {
            if (event.getType() == Event.Type.REPOSITORIES_CHANGED) {
                updateTree();
            }
        });
    }

    private void initView() {
        treeView.setEditable(false);
        treeView.setShowRoot(false);

        treeView.setCellFactory(treeView -> {
            TreeCell<ItemContent> treeCell = new RepoTreeCell();
            treeCell.setOnMouseClicked(getTreeCellMouseEventHandler(treeCell));
            return treeCell;
        });

        TreeItem<ItemContent> root = new TreeItem<>();
        root.setExpanded(true);
        treeView.setRoot(root);

        updateTree();
    }

    public void addFileEventListener(Consumer<Event<ItemContent>> observer) {
        fileEventObservable.addObserver(observer);
    }

    private EventHandler<MouseEvent> getTreeCellMouseEventHandler(TreeCell<ItemContent> treeCell) {
        return event -> {
            TreeItem<ItemContent> treeItem = treeCell.getTreeItem();
            if (treeItem == null || treeItem.getValue() == null) {
                return;
            }
            ItemContent itemContent = treeItem.getValue();

            if (event.getClickCount() == 2) {
                if (itemContent.isFile()) {
                    fileEventObservable.publishEvent(Event.<ItemContent>builder()
                            .type(Event.Type.FILE_DOUBLE_CLICKED)
                            .payload(itemContent)
                            .build());
                }
            }
        };
    }

    private void updateTree() {
        TreeItem<ItemContent> root = treeView.getRoot();

        for (RepositoryHandle handle : service.getRepositories()) {

            Optional<TreeItem<ItemContent>> existingRepoItem = findExistingItemForRepository(root, handle);

            if (!existingRepoItem.isPresent()) {
                loadRepositoryTree(handle);
            }
        }
    }

    private Optional<TreeItem<ItemContent>> findExistingItemForRepository(TreeItem<ItemContent> root, RepositoryHandle handle) {
        return root.getChildren().stream()
                .filter(treeItem -> treeItem.getValue() != null
                        && treeItem.getValue().getRepositoryHandle() == handle)
                .findAny();
    }

    private void loadRepositoryTree(RepositoryHandle handle) {
        TreeItem<ItemContent> treeRoot = treeView.getRoot();

        ItemContent repoItemContent = ItemContent.builder()
                .file(handle.getRepository().getWorkTree())
                .repositoryHandle(handle)
                .build();

        TreeItem<ItemContent> repoTreeItem = new TreeItem<>(repoItemContent);
        repoItemContent.setTreeItem(repoTreeItem);
        treeRoot.getChildren().add(repoTreeItem);

        buildSubtree(repoTreeItem);
    }

    private void buildSubtree(TreeItem<ItemContent> treeItem) {
        ItemContent content = treeItem.getValue();
        if (!content.isDirectory()) {
            return;
        }
        File[] files = content.getFile().listFiles(gitFolderFilter);

        for (File file : files) {
            ItemContent childItemContent = ItemContent.builder().file(file).build();
            TreeItem<ItemContent> childItem = new TreeItem<>(childItemContent);
            childItemContent.setTreeItem(childItem);
            treeItem.getChildren().add(childItem);
            buildSubtree(childItem);
        }
    }

    private static final FilenameFilter gitFolderFilter = (dir, name) -> !".git".equals(name);
}
