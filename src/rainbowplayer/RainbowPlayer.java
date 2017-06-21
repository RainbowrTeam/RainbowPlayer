package rainbowplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rainbowplayer.Core.FeatureManager;
//import javafx.Font;

public class RainbowPlayer extends Application {
       
    static {
    //   Font.loadFont(Application.class.getResource("/fonts/awesome.ttf").toExternalForm());
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("UserInterfaceFXML.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Stylesheet.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Rainbow Player");
        
        // initialize features
        FeatureManager.getInstance().initializeFeatures();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
