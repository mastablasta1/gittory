package pl.edu.agh.idziak.gittory.logic.findusages;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.gittory.gui.root.repotree.ItemContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tomasz on 16.05.2016.
 */
public class FindUsagesExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(FindUsagesExecutor.class);

    private Map<FindUsagesOperation, Integer> jobMap = new HashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public void executeOperation(FindUsagesOperation operation) {
        if (jobMap.containsKey(operation)) {
            throw new IllegalStateException("Operation already running");
        }

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

        CompilationUnit cu = origin.getCompilationUnit();
        Accumulator accumulator = new Accumulator();
        accumulator.desirableMethodName = operation.getMethodDeclaration().getName();
        cu.accept(new MethodCallVisitor(), accumulator);
        accumulator.found.forEach(operation::addMethodUsage);
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
