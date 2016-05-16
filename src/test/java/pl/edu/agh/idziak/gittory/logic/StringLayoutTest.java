package pl.edu.agh.idziak.gittory.logic;

import org.junit.Test;
import pl.edu.agh.idziak.gittory.util.StringLayout;

/**
 * Created by Tomasz on 16.05.2016.
 */
public class StringLayoutTest {
    @Test
    public void getLineAndColumn() throws Exception {
        StringLayout s = new StringLayout(TEST_STRING);

        System.out.println(s.getLineAndColumn(463));
        System.out.println(s.getLineAndColumn(468));
    }

    public static final String TEST_STRING =
            "package pl.edu.agh.idziak.gittory;\n" +
                    "\n" +
                    "import com.airhacks.afterburner.injection.Injector;\n" +
                    "import javafx.application.Application;\n" +
                    "import javafx.fxml.FXMLLoader;\n" +
                    "import javafx.scene.Parent;\n" +
                    "import javafx.scene.Scene;\n" +
                    "import javafx.stage.Stage;\n" +
                    "import pl.edu.agh.idziak.gittory.gui.root.RootView;\n" +
                    "import pl.edu.agh.idziak.gittory.gui.root.codearea.CodeAreaHandler;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by Tomasz on 13.05.2016.\n" +
                    " */\n" +
                    "public class Loader extends Application{\n" +
                    "\n" +
                    "    public void start(Stage primaryStage) throws Exception {\n" +
                    "        primaryStage.setTitle(\"Gittory 0.1\");\n" +
                    "\n" +
                    "        RootView rootView = new RootView();\n" +
                    "\n" +
                    "        Scene scene = new Scene(rootView.getView());\n" +
                    "\n" +
                    "        scene.getStylesheets().add(CodeAreaHandler.class.getResource(\"java-keywords.css\").toExternalForm());\n" +
                    "        primaryStage.setScene(scene);\n" +
                    "        primaryStage.show();\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void stop() throws Exception {\n" +
                    "        Injector.forgetAll();\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void main(String[] args) {\n" +
                    "        launch(args);\n" +
                    "    }\n" +
                    "}\n";

}