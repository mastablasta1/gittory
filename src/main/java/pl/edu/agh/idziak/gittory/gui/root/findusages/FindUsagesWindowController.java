package pl.edu.agh.idziak.gittory.gui.root.findusages;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import pl.edu.agh.idziak.gittory.logic.findusages.FindUsagesOperation;
import pl.edu.agh.idziak.gittory.logic.findusages.MethodUsage;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Created by Tomasz on 18.05.2016.
 */
public class FindUsagesWindowController implements Initializable {

    @FXML
    private TableView<MethodUsage> usagesTableView;

    @FXML
    private Label methodLabel;

    private FindUsagesOperation operation;
    private static final String USAGES_OF_METHOD_LABEL_PREFIX = "Usages of method: ";

    void setOperation(FindUsagesOperation operation) {
        this.operation = operation;
        reloadView();
    }

    private void reloadView() {
        String methodDeclaration = operation.getMethodDeclaration().getDeclarationAsString(true, false, true);
        methodLabel.setText(USAGES_OF_METHOD_LABEL_PREFIX + methodDeclaration);
        usagesTableView.setItems(operation.getMethodUsages());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumns tableColumns = new TableColumns();
        usagesTableView.setRowFactory(view -> {
            TableRow<MethodUsage> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2)
                    row.getItem().viewDoubleClicked();
            });
            return row;
        });
        usagesTableView.getColumns().addAll(tableColumns.getAll());
    }

    private static class TableColumns {
        TableColumn<MethodUsage, String> repository = buildTableColumn("Repository");
        TableColumn<MethodUsage, String> classAndLine = buildTableColumn("Class and line");
        TableColumn<MethodUsage, String> path = buildTableColumn("Package");

        TableColumns() {
            repository.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getRepository().getName()));
            classAndLine.setCellValueFactory(this::getClassAndLineFromCellData);
            path.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPath()));
        }

        private ReadOnlyObjectWrapper<String> getClassAndLineFromCellData(TableColumn.CellDataFeatures<MethodUsage, String> cellData) {
            MethodUsage methodUsage = cellData.getValue();
            return new ReadOnlyObjectWrapper<>(methodUsage.getClassName() + ":" + methodUsage.getLine());
        }

        private static TableColumn<MethodUsage, String> buildTableColumn(String repository) {
            return new TableColumn<>(repository);
        }

        Collection<TableColumn<MethodUsage, String>> getAll() {
            return Arrays.asList(repository, classAndLine, path);
        }


    }
}
