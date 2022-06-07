package controller;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import utils.DBConnector;


import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private TextField txtRes;

    @FXML
    private Label lblHistory;

    @FXML
    private Label lblHistoryBack;

    @FXML
    private ListView<String> historyList;

    @FXML
   private AnchorPane slider;

    private int decimalClick = 0;

    private String generalOperationObject;

    private Double firstDouble;

    public static ObservableList<String> data_list = FXCollections.observableArrayList();



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Получение данных из БД

        try {
            loadAllData();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Добавление слайдера с историей операций
//        slider.setTranslateX(100);
        lblHistory.setOnMouseClicked(event -> {

            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(slider);

            slide.setToX(0);
            slide.play();

            slider.setTranslateX(176);

            slide.setOnFinished((ActionEvent e)-> {
                lblHistory.setVisible(false);
                lblHistoryBack.setVisible(true);
            });
        });

        lblHistoryBack.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(slider);

            slide.setToX(176);
            slide.play();

            slider.setTranslateX(0);

            slide.setOnFinished((ActionEvent e)-> {
                lblHistory.setVisible(true);
                lblHistoryBack.setVisible(false);
            });
        });
    }



    @FXML
    void handlerDecimalAction(ActionEvent event) {
        if(decimalClick == 0) {
            String decimalObject = ((Button) event.getSource()).getText();
            String oldText = txtRes.getText();
            String newText = oldText + decimalObject;
            txtRes.setText(newText);
            decimalClick = 1;
        }
    }

    // Обработка нажатия на кнопку "="

    @FXML
    void handlerEqualAction(ActionEvent event) {
        double secondDouble;
        double result = 0;
        String secondText = txtRes.getText();
        String operation = "";
        secondDouble = Double.parseDouble(secondText);

        switch (generalOperationObject){
            case "+":
                result = firstDouble + secondDouble;
                operation = "+";
                break;
            case "-":
                result = firstDouble - secondDouble;
                operation = "-";
                break;
            case "*":
                result = firstDouble * secondDouble;
                operation = "*";
                break;
            case "/":
                result = firstDouble / secondDouble;
                operation = "/";
                break;
            default:
        }
//        String format = String.format("%.1f", result);
        txtRes.setText(String.valueOf(result));

        try {
            DBConnector.dbExecuteQuery("INSERT INTO history(first_number, operator, second_number, answer) "
                    + "VALUES ('"+firstDouble+"','"+operation+"','"+secondDouble+"','"+result+"')");
            loadAllData();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    // Обработка нажатия на кнопку с числами

    @FXML
    void numbersHandler(ActionEvent event) {
        String numberObject = ((Button) event.getSource()).getText();
        String oldText = txtRes.getText();
        String newText = oldText + numberObject;
        txtRes.setText(newText);
    }

    // Обработка нажатия на кнопку с математическими операторами

    @FXML
    void operationsHandler(ActionEvent event) {
        generalOperationObject = ((Button) event.getSource()).getText();
        switch (generalOperationObject) {
            case "C":
                txtRes.setText("");
                decimalClick = 0;
                break;
            case "+/-":
                double plusMinus = Double.parseDouble(String.valueOf(txtRes.getText()));
                plusMinus = plusMinus * (-1);
                txtRes.setText(String.valueOf(plusMinus));
                break;
            case "+":
            case "-":
            case "*":
            case "/":
            case "%":
                String currentText = txtRes.getText();
                firstDouble = Double.parseDouble(currentText);
                txtRes.setText("");
                decimalClick = 0;
                break;
            default:
        }
    }

    // Метод для получения данных из БД

    public void loadAllData() throws ClassNotFoundException, SQLException {
        try {
            data_list.clear();

            ResultSet rs = DBConnector.dbExecute("SELECT CONCAT (first_number, ' ',  operator, ' ', second_number, '=', answer) AS result FROM history ORDER BY id DESC" );
            while (rs.next()) data_list.add(rs.getString("result"));


            historyList.setItems(data_list);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
