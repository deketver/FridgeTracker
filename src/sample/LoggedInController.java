package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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

    @FXML
    private TableColumn<FridgeItem, Button> tab_delete;

    @FXML
    private Button bt_delete_expired;

    private String username = null;

    ObservableList<FridgeItem> fridge_items;

    // manual adding items
    @FXML
    private TextField tf_barcode_man;

    @FXML
    private TextField tf_product_name_man;

    @FXML
    private ComboBox<String> combo_category_man;

    @FXML
    private TextField tf_man_category_man;

    @FXML
    private Spinner<Integer> number_item_man;

    @FXML
    private TextField tf_expiration_date_man;

    @FXML
    private Button btn_save;

    @FXML
    private Button btn_search_barcode;

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
                FridgeItem databaseMatch = null;

                //patterns and matcher for barcode and expiration_date

                Pattern pattern = Pattern.compile("^[0-9]*$");
                Pattern date_pattern = Pattern.compile("^[0-9]{2}-[0-9]{2}-[0-9]{2}");
                Matcher matcher = pattern.matcher(tf_barcode.getText());
                Matcher matcher_date = date_pattern.matcher(tf_expiration.getText());
                boolean matchFound = matcher.find();
                boolean matchFoundDate = matcher_date.find();

                if(matchFound && matchFoundDate)
                {
                    // if user input data correctly

                    System.out.println("Match found for:"+ tf_barcode.getText() + " and date " + tf_expiration.getText());
                    FoodApiHandler data_getter = new FoodApiHandler(tf_barcode.getText());
                    try
                    {
                        // search items on world.openfoodfackts

                        data_getter.create_connection();
                        product_name = data_getter.return_product_name();
                        categories = data_getter.return_categories();
                        if(product_name != null)
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
                        else
                        {
                            // search results in local available database of results which were inserted before

                            System.out.println("Search current database results");
                            databaseMatch = DBUtils.searchInDatabase(tf_barcode.getText());
                            if(databaseMatch != null)
                            {
                                product_name = databaseMatch.getProduct_name();
                                categories[0] = databaseMatch.getCategory();
                                DBUtils.changeSceneValidate(actionEvent,
                                        "validate_results.fxml",
                                        "Check results",
                                        username,
                                        tf_barcode.getText(),
                                        product_name,
                                        categories,
                                        tf_expiration.getText());

                            }
                        }
                        if(product_name == null)
                        {
                            // no results found, user have to insert data manually

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Product was not found in the database. Insert data manually.");
                            alert.show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    // User input is not in correct format

                    System.out.println("Match not found");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Provided inputs are not in correct format.");
                    alert.show();
                }


            }
        });

        bt_delete_expired.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                try
                {
                    DBUtils.deleteExpired();
                } catch (SQLException exception)
                {
                    exception.printStackTrace();
                    System.out.println("Not possible to delete results from database");
                }
                load_data();
            }
        });

        btn_save.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {


            }
        });


    }
    public void setUserInformation(String username)
    {
        this.username = username;
        load_data();
    }

    public String getUsername()
    {
        return this.username;
    }

    private void load_data()
    {
        // loads data from database in TableView

        tab_barcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        tab_product_name.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        tab_category.setCellValueFactory(new PropertyValueFactory<>("category"));
        tab_expiration_date.setCellValueFactory(new PropertyValueFactory<>("expiration_date"));
        tab_num_items.setCellValueFactory(new PropertyValueFactory<>("numer_items"));
        //tab_edit.setCellValueFactory(new PropertyValueFactory<>("edit"));
        tab_delete.setCellValueFactory(new PropertyValueFactory<>("delete"));

        fridge_items = DBUtils.loadUserData(username);

        productView.setItems(fridge_items);

        for(FridgeItem item: fridge_items)
        {
            Button button = item.getDelete();
            button.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent actionEvent)
                {
                    try
                    {
                        DBUtils.deleteItem(item);
                        load_data();
                    } catch (SQLException exception)
                    {
                        exception.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Item was not possible to delete.");
                        alert.show();
                    }
                }
            });
        }

        edit_table_col();

    }

    private void edit_table_col()
    {
        /*
        tab_barcode.setCellFactory(TextFieldTableCell.forTableColumn());

        tab_barcode.setOnEditCommit(e->{e.getTableView().getItems().get(e.getTablePosition().getRow()).setBarcode(e.getNewValue());
        });
         */

        tab_expiration_date.setCellFactory(TextFieldTableCell.forTableColumn());

        tab_expiration_date.setOnEditCommit(e->{e.getTableView().getItems().get(e.getTablePosition().getRow()).setExpiration_date(e.getNewValue());
            var result = e.getTableView().getItems().get(e.getTablePosition().getRow());
            try
            {
                DBUtils.updateDatabaseRecord(result);
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Item was not updated.");
                alert.show();
            }
        });

        tab_num_items.setCellFactory(TextFieldTableCell.<FridgeItem, Integer>forTableColumn(new IntegerStringConverter()));


        tab_num_items.setOnEditCommit(e->{e.getTableView().getItems().get(e.getTablePosition().getRow()).setNumer_items(e.getNewValue());
            var result = e.getTableView().getItems().get(e.getTablePosition().getRow());
            try
            {
                DBUtils.updateDatabaseRecord(result);
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Item was not updated.");
                alert.show();
            }
        });

        productView.setEditable(true);
    }
}
