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
public class FindUsagesWindowsManager {

    private static final String FXML_FILE_NAME = "find-usages-window.fxml";
    private static final String LOAD_ERROR_MESSAGE = "Could not load resource (findUsages window)";

    private final URL fxmlResource;

    public FindUsagesWindowsManager() {
        fxmlResource = locateFxmlResource();
    }

    public void showFindUsagesWindow(FindUsagesOperation findUsagesOperation) {
        FindUsagesResourceSet resourceSet = loadResources();

        resourceSet.controller.setOperation(findUsagesOperation);

        Stage stage = new Stage();
        stage.setTitle("Find usages: " + findUsagesOperation.getMethodDeclaration().getName() + "()");
        stage.setScene(new Scene(resourceSet.root, 500, 700));
        stage.show();
        stage.requestFocus();
    }

    private URL locateFxmlResource() {
        URL fxml = getClass().getResource(FXML_FILE_NAME);
        if (fxml == null) {
            throw new IllegalStateException("Resource file not found: " + FXML_FILE_NAME);
        }
        return fxml;
    }

    private FindUsagesResourceSet loadResources() {
        try {
            FXMLLoader loader = new FXMLLoader(fxmlResource);
            FindUsagesResourceSet resourceSet = new FindUsagesResourceSet();
            resourceSet.root = loader.load();
            resourceSet.controller = loader.getController();
            return resourceSet;
        } catch (IOException e) {
            throw new IllegalStateException(LOAD_ERROR_MESSAGE, e);
        }
    }

    private static class FindUsagesResourceSet {
        Parent root;
        FindUsagesWindowController controller;
    }
}
