package pl.edu.agh.idziak.gittory.gui.root.findusages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.edu.agh.idziak.gittory.logic.findusages.FindUsagesOperation;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Tomasz on 16.05.2016.
 */
public class FindUsagesWindowsHandler {

    private static final String FXML_FILE_NAME = "find-usages-window.fxml";
    private static final String LOAD_ERROR_MESSAGE = "Could not load resource (findUsages window)";

    private final URL fxmlResource;

    public FindUsagesWindowsHandler() {
        fxmlResource = getFxmlResource();
    }

    public void showFindUsagesWindow(FindUsagesOperation findUsagesOperation) {
        Parent root = loadFxmlResource(fxmlResource);

        Stage stage = new Stage();
        stage.setTitle("Find usages: " + findUsagesOperation.getMethodDeclaration().getName() + "()");
        stage.setScene(new Scene(root, 400, 400));
        stage.show();
        stage.requestFocus();
    }

    private URL getFxmlResource() {
        URL resource = getClass().getResource(FXML_FILE_NAME);
        if (resource == null) {
            throw new IllegalStateException("Resource file not found: " + FXML_FILE_NAME);
        }
        loadFxmlResource(resource);
        return resource;
    }

    private Parent loadFxmlResource(URL resource) {
        try {
            return FXMLLoader.load(resource);
        } catch (IOException e) {
            throw new IllegalStateException(LOAD_ERROR_MESSAGE, e);
        }
    }
}
