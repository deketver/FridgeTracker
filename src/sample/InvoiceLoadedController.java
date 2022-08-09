package sample;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class InvoiceLoadedController implements Initializable
{
    @FXML
    private Button btn_logout;

    private String username = null;

    ObservableList<FridgeItem> fridge_items;
    @FXML
    private Button btn_back;

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
        load_data();
        btn_logout.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                DBUtils.changeScene(actionEvent, "sample.fxml", "Login", null);
            }
        });

        btn_back.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                DBUtils.changeScene(actionEvent, "logged-in.fxml", "Welcome!", username);
            }
        });

    }

    public void setUserInformation(String username, ObservableList<FridgeItem> fridge_items)
    {
        this.username = username;
        this.fridge_items = fridge_items;
        load_data();
    }

    public void load_data()
    {
        tab_barcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        tab_product_name.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        tab_category.setCellValueFactory(new PropertyValueFactory<>("category"));
        tab_expiration_date.setCellValueFactory(new PropertyValueFactory<>("expiration_date"));
        tab_num_items.setCellValueFactory(new PropertyValueFactory<>("numer_items"));
        //tab_edit.setCellValueFactory(new PropertyValueFactory<>("edit"));
        tab_add_item.setCellValueFactory(new PropertyValueFactory<>("delete"));

        //fridge_items = DBUtils.loadUserData(username);

        viewInvoice.setItems(fridge_items);

        /*
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
        }*/

        edit_table_col();
    }

    private void edit_table_col()
    {

        tab_barcode.setCellFactory(TextFieldTableCell.forTableColumn());

        tab_barcode.setOnEditCommit(e->{e.getTableView().getItems().get(e.getTablePosition().getRow()).setBarcode(e.getNewValue());
        });

        tab_product_name.setCellFactory(TextFieldTableCell.forTableColumn());

        tab_product_name.setOnEditCommit(e->{e.getTableView().getItems().get(e.getTablePosition().getRow()).setProduct_name(e.getNewValue());
        });

        tab_category.setCellFactory(TextFieldTableCell.forTableColumn());

        tab_category.setOnEditCommit(e->{e.getTableView().getItems().get(e.getTablePosition().getRow()).setCategory(e.getNewValue());
        });

        tab_expiration_date.setCellFactory(TextFieldTableCell.forTableColumn());

        tab_expiration_date.setOnEditCommit(e->{e.getTableView().getItems().get(e.getTablePosition().getRow()).setExpiration_date(e.getNewValue());
        });

        tab_num_items.setCellFactory(TextFieldTableCell.<FridgeItem, Integer>forTableColumn(new IntegerStringConverter()));

        tab_num_items.setOnEditCommit(e->{e.getTableView().getItems().get(e.getTablePosition().getRow()).setNumer_items(e.getNewValue());
        });

    }
}
