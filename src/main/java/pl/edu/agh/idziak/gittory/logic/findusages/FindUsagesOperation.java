package pl.edu.agh.idziak.gittory.logic.findusages;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import javafx.scene.control.TreeItem;
import pl.edu.agh.idziak.gittory.gui.root.repotree.ItemContent;
import pl.edu.agh.idziak.gittory.util.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Tomasz on 16.05.2016.
 */
public class FindUsagesOperation {
    private final MethodDeclaration methodDeclaration;
    private final TreeItem<ItemContent> treeItem;
    private final List<MethodCallExpr> foundMethodUsages = new ArrayList<>();
    private final Observable<MethodCallExpr> usageAddedObservable = new Observable<>();

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

    public synchronized List<MethodCallExpr> getFoundMethodUsages() {
        return new ArrayList<>(foundMethodUsages);
    }

    public synchronized void addUsageAddedObserver(Consumer<MethodCallExpr> observer) {
        usageAddedObservable.addObserver(observer);
    }

    synchronized void addMethodUsage(MethodCallExpr usage) {
        foundMethodUsages.add(usage);
        usageAddedObservable.publishEvent(usage);
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
