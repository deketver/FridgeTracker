package sample;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class InvoiceLoadedController implements Initializable
{
    @FXML
    private Button btn_logout;

    private String username = null;

    ObservableList<FridgeItem> fridge_items;

    @FXML
    private TableView<FridgeItem> viewInvoice;

    @FXML
    private TableColumn<FridgeItem, String> tab_barcode;

    @FXML
    private TableColumn<FridgeItem, String> tab_product_name;

    @FXML
    private TableColumn<FridgeItem, String> tab_category;

    @FXML
    private TableColumn<FridgeItem, String> tab_expiration_date;

    @FXML
    private TableColumn<FridgeItem, Integer> tab_num_items;

    @FXML
    private TableColumn<FridgeItem, Button> tab_add_item;


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


    }

    public void setUserInformation(String username, ObservableList<FridgeItem> fridge_items)
    {
        this.username = username;
        this.fridge_items = fridge_items;
        //load_data();
    }
}
