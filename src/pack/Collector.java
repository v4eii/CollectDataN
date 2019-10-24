package pack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pack.util.Config;

/**
 * Ну тут сложно что-либо сказать
 * @author v4e
 */
public class Collector extends Application{
    public static void main(String[] args)
    {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    
        Config.initializeConfig();
    
        Application.setUserAgentStylesheet(STYLESHEET_CASPIAN);
        Parent root = FXMLLoader.load(getClass().getResource("/pack/view/MainView.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("Обработка заявлений");
        primaryStage.setMaximized(false);
        primaryStage.show();
        
    }



}
