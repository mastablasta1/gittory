package pl.edu.agh.idziak.gittory.gui.root.repotree;

import java.io.File;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class RepoTreeItem {
    private File file;

    public RepoTreeItem(File file){
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return file.getName();
    }
}
