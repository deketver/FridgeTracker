package sample;

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
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                LoggedInController loggedInController = loader.getController();
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
                resultsController.setUserInformation(barcode, product_name, categories,expiration_date);
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

                System.out.println("Here ok 1");
                changeScene(event, "logged-in.fxml", "Welcome", username);
                System.out.println("Here ok 2");
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

    public static void saveProductData(String user, Integer barcode, String product_name, String category, String expiration_date, Integer number_items)
    {

    }

}
