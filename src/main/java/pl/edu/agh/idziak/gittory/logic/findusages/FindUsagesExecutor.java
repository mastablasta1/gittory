package pl.edu.agh.idziak.gittory.logic.findusages;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.gittory.gui.root.repotree.ItemContent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tomasz on 16.05.2016.
 */
public class FindUsagesExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(FindUsagesExecutor.class);

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private MethodUsageViewHandle viewHandle;

    public FindUsagesExecutor(MethodUsageViewHandle viewHandle) {
        this.viewHandle = viewHandle;
    }

    public void executeOperation(FindUsagesOperation operation) {
        executorService.submit(getTaskForOperation(operation));
    }

    private Runnable getTaskForOperation(FindUsagesOperation operation) {
        return () -> {
            try {
                doExecuteOperation(operation);
            } catch (RuntimeException e) {
                LOG.error("Execution failed", e);
            }
        };
    }

    private void doExecuteOperation(FindUsagesOperation operation) {
        ItemContent origin = operation.getTreeItem().getValue();
        TreeItem<ItemContent> treeRoot = findTreeRoot(origin.getTreeItem());

        treeRoot.getChildren().forEach(repoTreeItem -> walkDownTree(repoTreeItem, operation));
    }

    private TreeItem<ItemContent> findTreeRoot(TreeItem<ItemContent> origin) {
        TreeItem<ItemContent> current = origin;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }

    private void processTreeItem(TreeItem<ItemContent> treeItem, FindUsagesOperation operation) {
        ItemContent currentItem = treeItem.getValue();
        if (currentItem == null)
            return;

        if (currentItem.isDirectory())
            walkDownTree(treeItem, operation);
        else if (currentItem.isParsedJavaFile()) {
            findUsagesInFile(currentItem, operation);
        }
    }

    private void walkDownTree(TreeItem<ItemContent> dirItem, FindUsagesOperation operation) {
        LOG.info("Searching directory: " + dirItem.getValue().getFile().getAbsolutePath());
        for (TreeItem<ItemContent> child : dirItem.getChildren()) {
            processTreeItem(child, operation);
        }
    }

    private void findUsagesInFile(ItemContent item, FindUsagesOperation operation) {
        LOG.info("Searching usages in: " + item.getFile().getAbsolutePath());
        CompilationUnit cu = item.getCompilationUnit();
        Accumulator accumulator = new Accumulator();
        accumulator.desirableMethodName = operation.getMethodDeclaration().getName();
        cu.accept(new MethodCallVisitor(), accumulator);
        accumulator.found.forEach(methodCall -> {
            MethodUsage methodUsage = MethodUsage.newBuilder()
                    .methodCallExpr(methodCall)
                    .className(cu.getTypes().get(0).getName())
                    .repository(item.getRepositoryHandle())
                    .line(methodCall.getBeginLine())
                    .path(cu.getPackage().getName().toStringWithoutComments())
                    .viewHandle(viewHandle)
                    .itemContent(item)
                    .build();
            operation.addMethodUsage(methodUsage);
        });
    }


    private static class MethodCallVisitor extends VoidVisitorAdapter<Accumulator> {
        @Override
        public void visit(MethodCallExpr n, Accumulator acc) {
            if (n.getName().equals(acc.desirableMethodName)) {
                LOG.info(n.toString());
                acc.found.add(n);
            }
            super.visit(n, acc);
        }
    }

    private static class Accumulator {
        List<MethodCallExpr> found = new ArrayList<>();
        String desirableMethodName;
    }
}
