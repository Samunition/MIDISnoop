import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainLauncher extends Application {

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Todo show a splash screen?
        // Todo show the main scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MidiSnoopView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("MIDI Snooper : version 0.1");
        Scene mainScene = new Scene(root, 600, 400);
        mainScene.setRoot(root);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            ((MidiSnoopController) loader.getController()).shutdown();
            Platform.exit();
        });
    }
}
