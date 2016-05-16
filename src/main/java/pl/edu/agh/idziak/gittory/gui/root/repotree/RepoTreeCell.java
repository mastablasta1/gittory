package pl.edu.agh.idziak.gittory.gui.root.repotree;

import javafx.scene.control.TreeCell;

/**
 * Created by Tomasz on 14.05.2016.
 */
public class RepoTreeCell extends TreeCell<ItemContent> {

    @Override
    protected void updateItem(ItemContent item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            setText(item == null ? "" : getItem().toString());
        }
        setGraphic(null);
    }
}
