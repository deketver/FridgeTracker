package sample;

import com.sun.glass.ui.CommonDialogs;
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
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    ObservableList<FridgeItem> invoice_items;

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

    // invoice loader
    @FXML
    private ComboBox<String> combo_store_selection;

    @FXML
    private Button btn_invoice_upload;

    @FXML
    private Button btn_choose_file;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        load_data();
        setCategories();
        setPossibleStores();

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
                                System.out.println(databaseMatch.getCategory());
                                categories = new String[1];
                                categories[0] = databaseMatch.getCategory();
                                System.out.println(Arrays.toString(categories));
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
                } catch (SQLException | ParseException exception)
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
                String choice = null;
                String barcode = tf_barcode_man.getText();
                if(combo_category_man.getValue().equals("Manual entry"))
                {
                    choice = tf_man_category_man.getText();
                }
                else
                {
                    choice = combo_category_man.getValue();
                }

                if(barcode.equals(""))
                {
                    try
                    {
                        barcode = DBUtils.createUniqueBarcode();
                    }
                    catch( SQLException exception)
                    {
                        exception.printStackTrace();
                    }
                }
                DBUtils.saveProductData(actionEvent, username, barcode,tf_product_name_man.getText(),
                        choice, tf_expiration_date_man.getText(), number_item_man.getValue() );
            }
        });

        btn_search_barcode.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                String product_name = tf_product_name_man.getText();
                String barcode;
                if(product_name.equals(""))
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Product name has to be filled in order to search barcode.");
                    alert.show();
                }
                else
                    {
                        try
                        {
                            barcode = DBUtils.searchName(product_name);
                            if(barcode == null)
                            {
                                System.out.println("Product not found in database via name");
                                // try to search on the internet

                                // TBD - problems how to go around Google personalization without API Key

                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setContentText("Item not found.");
                                alert.show();
                            }
                            else
                            {
                                System.out.println("barcode is "+ barcode);
                                tf_barcode_man.setText(barcode);
                            }
                        }
                        catch(SQLException exception)
                        {
                            exception.printStackTrace();
                        }
                    }
            }
        });

        btn_choose_file.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
                File f = fc.showOpenDialog(null);
                if(f!=null)
                {
                    System.out.println("File found");
                    System.out.println(f.getAbsolutePath());
                    invoice_items = PDFfileReader.pythonFileProcess(f.getAbsolutePath(), username);
                    for(FridgeItem item: invoice_items)
                    {
                        System.out.println(item.getProduct_name());
                    }
                }
            }
        });

        btn_invoice_upload.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                DBUtils.changeSceneInvoice(actionEvent, "invoice_loaded.fxml", "Choose items to add!", username, invoice_items);
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

    private void setPossibleStores()
    {
        ArrayList<String> possible_values = new ArrayList<String>();
        possible_values.add("Košík");
        possible_values.add("Rohlík");

        ObservableList<String> observableList = FXCollections.observableList(possible_values);
        combo_store_selection.setItems(observableList);

    }

    private void setCategories()
    {
        ArrayList<String> possible_values = new ArrayList<String>();
        possible_values.add("Dairy");
        possible_values.add("Cheese");
        possible_values.add("Fruit");
        possible_values.add("Vegetables");
        possible_values.add("Ham");
        possible_values.add("Meat");
        possible_values.add("Smoked meats");
        possible_values.add("Drinks");
        possible_values.add("Juices");
        possible_values.add("Sauces");
        possible_values.add("Vegetarian");
        possible_values.add("Vegan");
        possible_values.add("Frozen");
        possible_values.add("Manual entry");
        ObservableList<String> observableList = FXCollections.observableList(possible_values);
        combo_category_man.setItems(observableList);

        var factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 1);
        factory.setWrapAround(true);

        number_item_man.setValueFactory(factory);
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
