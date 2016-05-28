package pl.edu.agh.idziak.gittory.logic.findusages;

import com.github.javaparser.ast.body.MethodDeclaration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import pl.edu.agh.idziak.gittory.gui.root.repotree.ItemContent;

/**
 * Created by Tomasz on 16.05.2016.
 */
public class FindUsagesOperation {
    private final MethodDeclaration methodDeclaration;
    private final TreeItem<ItemContent> treeItem;
    private final ObservableList<MethodUsage> methodUsages = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private boolean stopped;

    private FindUsagesOperation(Builder builder) {
        methodDeclaration = builder.methodDeclaration;
        treeItem = builder.treeItem;
    }

    public static Builder builder() {
        return new Builder();
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public TreeItem<ItemContent> getTreeItem() {
        return treeItem;
    }

    void addMethodUsage(MethodUsage usage) {
        methodUsages.add(usage);
    }

    public ObservableList<MethodUsage> getMethodUsages() {
        return FXCollections.unmodifiableObservableList(methodUsages);
    }

    public void stop() {
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }

    public static final class Builder {
        private MethodDeclaration methodDeclaration;
        private TreeItem<ItemContent> treeItem;

        private Builder() {
        }

        public Builder methodDeclaration(MethodDeclaration val) {
            methodDeclaration = val;
            return this;
        }

        public Builder treeItem(TreeItem<ItemContent> val) {
            treeItem = val;
            return this;
        }

        public FindUsagesOperation build() {
            return new FindUsagesOperation(this);
        }
    }
}
