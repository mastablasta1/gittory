package pl.edu.agh.idziak.gittory.logic.findusages;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.google.common.base.Preconditions;
import pl.edu.agh.idziak.gittory.gui.root.repotree.ItemContent;
import pl.edu.agh.idziak.gittory.logic.RepositoryHandle;

/**
 * Created by Tomasz on 22.05.2016.
 */
public class MethodUsage {
    private ItemContent itemContent;
    private String className;
    private String path;
    private RepositoryHandle repository;
    private int line;
    private MethodCallExpr methodCallExpr;
    private MethodUsageViewHandle viewHandle;

    private MethodUsage(Builder builder) {
        className = builder.className;
        path = builder.path;
        repository = builder.repository;
        line = builder.line;
        methodCallExpr = builder.methodCallExpr;
        viewHandle = builder.viewHandle;
        itemContent = builder.itemContent;
    }


    public static Builder newBuilder() {
        return new Builder();
    }

    public String getClassName() {
        return className;
    }

    public String getPath() {
        return path;
    }

    public RepositoryHandle getRepository() {
        return repository;
    }

    public MethodCallExpr getMethodCallExpr() {
        return methodCallExpr;
    }

    public ItemContent getItemContent() {
        return itemContent;
    }

    public int getLine() {
        return line;
    }

    public void viewDoubleClicked() {
        Preconditions.checkNotNull(viewHandle).doubleClick(this);
    }

    public static final class Builder {
        private String className;
        private String path;
        private RepositoryHandle repository;
        private int line;
        private MethodCallExpr methodCallExpr;
        private MethodUsageViewHandle viewHandle;
        private ItemContent itemContent;

        private Builder() {
        }

        public Builder className(String val) {
            className = val;
            return this;
        }

        public Builder path(String val) {
            path = val;
            return this;
        }

        public Builder repository(RepositoryHandle val) {
            repository = val;
            return this;
        }

        public Builder line(int val) {
            line = val;
            return this;
        }

        public Builder methodCallExpr(MethodCallExpr val) {
            methodCallExpr = val;
            return this;
        }

        public Builder itemContent(ItemContent val) {
            itemContent = val;
            return this;
        }

        public Builder viewHandle(MethodUsageViewHandle val) {
            viewHandle = val;
            return this;
        }

        public MethodUsage build() {
            return new MethodUsage(this);
        }
    }


}
