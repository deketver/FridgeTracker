package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggedInController implements Initializable
{
    @FXML
    private Button btn_logout;

    @FXML
    private Button btn_search_item;

    @FXML
    private TextField tf_barcode;

    @FXML
    private TextField tf_expiration;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        btn_logout.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                DBUtils.changeScene(actionEvent, "sample.fxml", "Login", null);
            }
        });

        btn_search_item.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                Pattern pattern = Pattern.compile("^[0-9]*$");
                Pattern date_pattern = Pattern.compile("^[0-9]{2}-[0-9]{2}-[0-9]{2}$]");
                Matcher matcher = pattern.matcher(tf_barcode.getText());
                Matcher matcher_date = date_pattern.matcher(tf_expiration.getText());
                boolean matchFound = matcher.find();
                boolean matchFoundDate = matcher_date.find();
                if(matchFound)
                {
                    System.out.println("Match found for:"+ tf_barcode.getText() + " and date " + tf_expiration.getText());
                    FoodApiHandler data_getter = new FoodApiHandler(tf_barcode.getText());
                    try {
                        data_getter.create_connection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    System.out.println("Match not found");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Provided inputs are not in correct format.");
                    alert.show();
                }
            }
        });
    }
}
