package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable
{
    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_mail;

    @FXML
    private PasswordField ps_first;

    @FXML
    private PasswordField ps_second;

    @FXML
    private Button btn_create_account;

    @FXML
    private Button btn_login;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        btn_login.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                DBUtils.changeScene(actionEvent, "sample.fxml", "Login", null);
            }
        });

        btn_create_account.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                if(!ps_first.getText().equals(ps_second.getText()))
                {
                    System.out.println("Passwords do not match.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Passwords do not match.");
                    alert.show();
                    return;
                }

                if(tf_username.getText().trim().isEmpty() && ps_first.getText().trim().isEmpty())
                {
                    System.out.println("Fields left empty.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Fields left empty.");
                    alert.show();
                    return;
                }

                DBUtils.signUpUser(actionEvent, tf_username.getText(), ps_first.getText(), tf_mail.getText());

            }
        });
    }
}
