import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("view/MainView.fxml"));
        Scene rootScene = new Scene(root);
        Stage rootStage = new Stage();

        rootStage.setScene(rootScene);
        rootStage.setTitle("Калькулятор");
        rootStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
