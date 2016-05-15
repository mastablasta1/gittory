package pl.edu.agh.idziak.gittory.gui.root.repotree;

import javafx.scene.control.TreeCell;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class RepoTreeCell extends TreeCell<RepoTreeItem> {

    @Override
    protected void updateItem(RepoTreeItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            setText(item == null ? "" : getItem().toString());
        }
        setGraphic(null);
    }
}
