package pl.edu.agh.idziak.gittory;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.edu.agh.idziak.gittory.gui.root.RootView;
import pl.edu.agh.idziak.gittory.gui.root.codearea.CodeAreaHandler;

/**
 * Created by Tomasz on 13.05.2016.
 */
public class Loader extends Application {

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Gittory 0.1");

        RootView rootView = new RootView();

        Scene scene = new Scene(rootView.getView(), 1200, 800);

        scene.getStylesheets().add(CodeAreaHandler.class.getResource("java-keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
