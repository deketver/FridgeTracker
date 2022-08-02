package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DBUtils
{

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username)
    {
        Parent root = null;

        if (username != null)
        {
            System.out.println(username);
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                LoggedInController loggedInController = loader.getController();
                loggedInController.setUserInformation(username);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void changeSceneValidate(ActionEvent event, String fxmlFile, String title, String username, String barcode, String product_name, String[] categories, String expiration_date)
    {
        Parent root = null;

        if (product_name != null)
        {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                ResultsController resultsController = loader.getController();
                resultsController.setUserInformation(username, barcode, product_name, categories,expiration_date);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String password, String email)
    {
        Connection connection = null;
        PreparedStatement psCheckUserExists = null;
        PreparedStatement psInsert = null;
        ResultSet resultSet = null;

        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/veron/Documents/FJFI/BS3/JAVA/fridge_fx/fridge_java.db");
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst())
            {
                //user already exists
                System.out.println("User already exists!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("This username is already taken.");
                alert.show();
            }
            else
            {
                psInsert = connection.prepareStatement("INSERT INTO users (username, password, email_address) VALUES (?, ?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.setString(3, email);
                psInsert.executeUpdate();

                changeScene(event, "logged-in.fxml", "Welcome", username);

            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());

        }
        finally
        {
            if(connection != null)
            {
                try
                {
            connection.close();
                }
                catch(SQLException e)
                {
                    System.out.println(e.getMessage());
                }

            }
        }


    }

    public static void logInUser(ActionEvent event, String username, String password)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/veron/Documents/FJFI/BS3/JAVA/fridge_fx/fridge_java.db");
            preparedStatement = connection.prepareStatement("SELECT password from users WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if(!resultSet.isBeforeFirst())
            {
                // no results available
                System.out.println("User not found in database.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect!");
                alert.show();
            }
            else
            {
                while(resultSet.next())
                {
                    String retrivedPassword = resultSet.getString("password"); //vraci hodnotu pro sloupecek password
                    if (retrivedPassword.equals(password))
                    {
                        changeScene(event, "logged-in.fxml", "Welcome", username);
                    }
                    else
                    {
                        System.out.println("Password did not match");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Provided credentials are incorrect.");
                        alert.show();
                    }
                }

            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void saveProductData(ActionEvent event, String user, String barcode, String product_name, String category, String expiration_date, Integer number_items)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/veron/Documents/FJFI/BS3/JAVA/fridge_fx/fridge_java.db");
            preparedStatement = connection.prepareStatement("SELECT numer_items, deleted from fridge_records WHERE user = ? and barcode = ? and category = ? and expiration_date = ? and deleted = ?");
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, barcode);
            preparedStatement.setString(3, user);
            preparedStatement.setString(4, expiration_date);
            preparedStatement.setString(5, "0");
            resultSet = preparedStatement.executeQuery();

            if (resultSet.isBeforeFirst())
            {
                //record for this user, barcode and date already exists
                System.out.println("Record already exists");

                while(resultSet.next())
                {
                    String retrivedResult = resultSet.getString("numer_items"); //vraci hodnotu pro sloupecek numer_items
                    Integer available_items = Integer.parseInt(retrivedResult);
                    available_items += 1;

                    PreparedStatement alterStatement = connection.prepareStatement("UPDATE fridge_records " +
                            "SET numer_items = ? WHERE user = ? and barcode = ? and category = ? and expiration_date = ? ");
                    alterStatement.setString(1, available_items.toString());
                    alterStatement.setString(2, user);
                    alterStatement.setString(3, barcode);
                    alterStatement.setString(4, category);
                    alterStatement.setString(5, expiration_date);
                    alterStatement.executeUpdate();

                    changeScene(event, "logged-in.fxml", "Welcome", user);
                }
            }
            else
            {
                PreparedStatement psInsert = connection.prepareStatement("INSERT INTO fridge_records (user, barcode," +
                        " product_name, category, expiration_date, numer_items, deleted) VALUES (?, ?, ?, ?, ?, ?, ?)");
                psInsert.setString(1, user);
                psInsert.setString(2, barcode);
                psInsert.setString(3, product_name);
                psInsert.setString(4, category);
                psInsert.setString(5, expiration_date);
                psInsert.setString(6, number_items.toString());
                psInsert.setString(7, "0");
                psInsert.executeUpdate();
                changeScene(event, "logged-in.fxml", "Welcome", user);
            }

        }

        catch (SQLException exception)
        {
            exception.printStackTrace();
        }

    }

    public static ObservableList<FridgeItem> loadUserData(String user)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ObservableList<FridgeItem> ItemsList = FXCollections.observableArrayList();

        try
        {
            System.out.println(user);
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/veron/Documents/FJFI/BS3/JAVA/fridge_fx/fridge_java.db");
            preparedStatement = connection.prepareStatement("SELECT * from fridge_records WHERE user = ? and deleted = ?");
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, "0");
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next())
            {
                ItemsList.add(new FridgeItem(resultSet.getString("user"),resultSet.getString("barcode"),
                        resultSet.getString("product_name"), resultSet.getString("category"),
                        resultSet.getString("expiration_date"), Integer.parseInt(resultSet.getString("numer_items"))));
            }
        }
        catch(SQLException exception)
        {
            exception.printStackTrace();
        }
        return ItemsList;
    }

}
