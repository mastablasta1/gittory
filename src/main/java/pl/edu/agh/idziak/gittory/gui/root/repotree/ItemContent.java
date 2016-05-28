package pl.edu.agh.idziak.gittory.gui.root.repotree;

import com.github.javaparser.ast.CompilationUnit;
import javafx.scene.control.TreeItem;
import pl.edu.agh.idziak.gittory.util.FileHelper;
import pl.edu.agh.idziak.gittory.logic.RepositoryHandle;

import java.io.File;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class ItemContent {
    private File file;
    private RepositoryHandle repositoryHandle;
    private String fileContent;
    private CompilationUnit compilationUnit;
    private TreeItem<ItemContent> treeItem;

    private ItemContent(Builder builder) {
        file = builder.file;
        repositoryHandle = builder.repositoryHandle;

        if (isJavaFile()) {
            fileContent = FileHelper.safelyReadFileContent(file).orElse(null);
            compilationUnit = FileHelper.parseJava(file).orElse(null);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public File getFile() {
        return file;
    }

    public RepositoryHandle getRepositoryHandle() {
        return repositoryHandle;
    }

    public String getFileContent() {
        if (fileContent == null) {
            fileContent = FileHelper.readFileContent(file);
        }
        return fileContent;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public boolean isFile() {
        return file.isFile();
    }

    public boolean isRepository() {
        return treeItem.getParent().getValue() == null;
    }

    @Override
    public String toString() {
        return file.getName();
    }

    public boolean isJavaFile() {
        return file.isFile() && file.getName().endsWith(".java");
    }

    public boolean isParsedJavaFile() {
        return compilationUnit != null;
    }

    public TreeItem<ItemContent> getTreeItem() {
        return treeItem;
    }

    public void setTreeItem(TreeItem<ItemContent> treeItem) {
        this.treeItem = treeItem;
    }


    public static final class Builder {
        private File file;
        private RepositoryHandle repositoryHandle;

        private Builder() {
        }

        public Builder file(File val) {
            file = val;
            return this;
        }

        public Builder repositoryHandle(RepositoryHandle val) {
            repositoryHandle = val;
            return this;
        }

        public ItemContent build() {
            return new ItemContent(this);
        }
    }
}
