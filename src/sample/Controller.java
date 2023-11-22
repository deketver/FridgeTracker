package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sample.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    @FXML
    private Button btn_login;

    @FXML
    private Button btn_singup;

    @FXML
    private TextField tf_username;

    @FXML
    private PasswordField tf_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        btn_login.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                DBUtils.logInUser(actionEvent, tf_username.getText(), tf_password.getText());
            }
        });

        btn_singup.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                DBUtils.changeScene(actionEvent, "sing-up.fxml", "Create your account", null);
            }
        });

    }
}
