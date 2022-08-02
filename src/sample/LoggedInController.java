package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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

    @FXML
    private TableView<FridgeItem> productView;

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

    private String username;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        load_data();
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
            String product_name = null;
            String[] categories = null;

            @Override
            public void handle(ActionEvent actionEvent)
            {
                Pattern pattern = Pattern.compile("^[0-9]*$");
                Pattern date_pattern = Pattern.compile("^[0-9]{2}-[0-9]{2}-[0-9]{2}");
                Matcher matcher = pattern.matcher(tf_barcode.getText());
                Matcher matcher_date = date_pattern.matcher(tf_expiration.getText());
                boolean matchFound = matcher.find();
                boolean matchFoundDate = matcher_date.find();
                if(matchFound && matchFoundDate)
                {
                    //System.out.println(matchFoundDate);
                    System.out.println("Match found for:"+ tf_barcode.getText() + " and date " + tf_expiration.getText());
                    FoodApiHandler data_getter = new FoodApiHandler(tf_barcode.getText());
                    try {
                        data_getter.create_connection();
                        product_name = data_getter.return_product_name();
                        categories = data_getter.return_categories();
                        if(product_name == null)
                        {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Product was not found in the database. Insert data manually.");
                            alert.show();
                        }
                        else
                        {
                            DBUtils.changeSceneValidate(actionEvent,
                                    "validate_results.fxml",
                                    "Check results",
                                    username,
                                    tf_barcode.getText(),
                                    product_name,
                                    categories,
                                    tf_expiration.getText());
                        }

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
    public void setUserInformation(String username)
    {
        this.username = username;
    }

    private void load_data()
    {
        tab_barcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        tab_product_name.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        tab_category.setCellValueFactory(new PropertyValueFactory<>("category"));
        tab_expiration_date.setCellValueFactory(new PropertyValueFactory<>("expiration_date"));
        tab_num_items.setCellValueFactory(new PropertyValueFactory<>("numer_items"));

        ObservableList<FridgeItem> fridge_items = DBUtils.loadUserData(this.username);

        productView.setItems(fridge_items);

    }
}
