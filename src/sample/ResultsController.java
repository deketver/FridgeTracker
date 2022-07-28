package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ResultsController implements Initializable
{
    @FXML
    private Label l_barcodenum;

    @FXML
    private TextField tf_product_name;

    @FXML
    private ChoiceBox<String> choice_category;

    @FXML
    private Spinner<Integer> num_items;

    @FXML
    private Button btn_confirm;

    @FXML
    private Button btn_cancel;

    @FXML
    private Label l_expiration_date;

    @FXML
    private Button btn_logout;


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

        btn_cancel.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                DBUtils.changeScene(actionEvent, "logged-in.fxml", "Welcome", null);
            }
        });

        btn_confirm.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {

                DBUtils.saveProductData(null, l_barcodenum.getText(), );
            }
        });

    }

    public void setUserInformation(String barcode, String product_name, String[] categories, String expiration_date)
    {
        l_barcodenum.setText(barcode);
        tf_product_name.setText(product_name);
        if(categories != null)
        {
        ObservableList<String> observableList = FXCollections.observableList(Arrays.asList(categories));
        choice_category.setItems(observableList);
        choice_category.setValue(categories[0]);
        }
        else {
            choice_category.setValue("None");
        }

        var factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 1);
        factory.setWrapAround(true);

        num_items.setValueFactory(factory);
        l_expiration_date.setText(expiration_date);
    }

}
