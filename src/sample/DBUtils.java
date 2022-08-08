package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String dir = System.getProperty("user.dir");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ dir+ "\\fridge_java.db");
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
            String dir = System.getProperty("user.dir");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ dir +"\\fridge_java.db");
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

        //need to add input validation here

        try
        {
            String dir = System.getProperty("user.dir");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ dir +"\\fridge_java.db");
            preparedStatement = connection.prepareStatement("SELECT numer_items, deleted from fridge_records WHERE user = ? and barcode = ? and category = ? and expiration_date = ? and deleted = ?");
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, barcode);
            preparedStatement.setString(3, category);
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
                    int available_items = Integer.parseInt(retrivedResult);
                    available_items = available_items +1;

                    PreparedStatement alterStatement = connection.prepareStatement("UPDATE fridge_records " +
                            "SET numer_items = ? WHERE user = ? and barcode = ? and category = ? and expiration_date = ? ");
                    alterStatement.setString(1, Integer.toString(available_items));
                    alterStatement.setString(2, user);
                    alterStatement.setString(3, barcode);
                    alterStatement.setString(4, category);
                    alterStatement.setString(5, expiration_date);
                    alterStatement.executeUpdate();

                }
                changeScene(event, "logged-in.fxml", "Welcome", user);
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

    // loads data for current user available in database
    public static ObservableList<FridgeItem> loadUserData(String user)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ObservableList<FridgeItem> ItemsList = FXCollections.observableArrayList();

        try
        {
            // printing out user
            //System.out.println(user);


            String dir = System.getProperty("user.dir");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ dir +"\\fridge_java.db");
            preparedStatement = connection.prepareStatement("SELECT * from fridge_records WHERE user = ? and deleted = ?");
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, "0");
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next())
            {
                ItemsList.add(new FridgeItem(resultSet.getString("user"),resultSet.getString("barcode"),
                        resultSet.getString("product_name"), resultSet.getString("category"),
                        resultSet.getString("expiration_date"),
                        Integer.parseInt(resultSet.getString("numer_items")), new Button("Delete")));
            }
        }
        catch(SQLException exception)
        {
            exception.printStackTrace();
        }
        return ItemsList;
    }


    // search history of database records to find a match
    public static FridgeItem searchInDatabase(String barcode)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        FridgeItem search_result = null;
        try
        {
            String dir = System.getProperty("user.dir");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ dir +"\\fridge_java.db");
            preparedStatement = connection.prepareStatement("Select * from fridge_records where barcode = ? LIMIT 1");
            preparedStatement.setString(1, barcode);
            resultSet = preparedStatement.executeQuery();

            if(!resultSet.isBeforeFirst())
            {
                // no results available
                System.out.println("No match found even in the database.");

            }
            else
                {
                while (resultSet.next())
                {
                    search_result = new FridgeItem(resultSet.getString("barcode"),
                            resultSet.getString("product_name"),
                            resultSet.getString("category"), new Button("Delete"));
                }
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }

        return search_result;
    }

    public static void updateDatabaseRecord(FridgeItem fridgeItem) throws SQLException
    {
        if(fridgeItem != null)
        {
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            String expiration_date = fridgeItem.getExpiration_date();
            int number_items = fridgeItem.getNumer_items();
            String user = fridgeItem.getUser();
            String barcode = fridgeItem.getBarcode();
            String category = fridgeItem.getCategory();

            Pattern date_pattern = Pattern.compile("^[0-9]{2}-[0-9]{2}-[0-9]{2}");
            Matcher matcher_date = date_pattern.matcher(expiration_date);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy", Locale.ENGLISH);
            LocalDate dateTime = LocalDate.parse(expiration_date, formatter);

            System.out.println("From DBUtils");
            System.out.println(dateTime);
            System.out.println(expiration_date);
            boolean matchFoundDate = matcher_date.find();
            if(matchFoundDate)
            {
                String dir = System.getProperty("user.dir");
                connection = DriverManager.getConnection("jdbc:sqlite:"+ dir +"\\fridge_java.db");
                preparedStatement = connection.prepareStatement("UPDATE fridge_records " +
                        "SET expiration_date = ?, numer_items = ? WHERE user = ? and barcode = ? and category = ? and deleted = ?");
                preparedStatement.setString(1, expiration_date);
                preparedStatement.setString(2, Integer.toString(number_items));
                preparedStatement.setString(3, user);
                preparedStatement.setString(4, barcode);
                preparedStatement.setString(5, category);
                preparedStatement.setString(6, "0");
                preparedStatement.executeUpdate();
            }
            else
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Wrong date format, record not saved.");
                    alert.show();
                }
        }
        else
        {
            System.out.println("Fridge item is null");
        }
    }

    public static void deleteItem(FridgeItem fridgeItem) throws SQLException
    {
        if(fridgeItem != null)
        {
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            String expiration_date = fridgeItem.getExpiration_date();
            String user = fridgeItem.getUser();
            String barcode = fridgeItem.getBarcode();
            String category = fridgeItem.getCategory();

            String dir = System.getProperty("user.dir");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dir + "\\fridge_java.db");
            preparedStatement = connection.prepareStatement("UPDATE fridge_records " +
                    "SET deleted = ? WHERE user = ? and barcode = ? and category = ? and expiration_date = ?");
            preparedStatement.setString(1, "1");
            preparedStatement.setString(2, user);
            preparedStatement.setString(3, barcode);
            preparedStatement.setString(4, category);
            preparedStatement.setString(5, expiration_date);

            preparedStatement.executeUpdate();

            connection.close();
        }
    }

    public static void deleteExpired() throws SQLException, ParseException
    {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
        System.out.println(formatter.format(now));

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String dir = System.getProperty("user.dir");
        connection = DriverManager.getConnection("jdbc:sqlite:"+ dir +"\\fridge_java.db");
        preparedStatement = connection.prepareStatement("Select * from fridge_records");
        resultSet = preparedStatement.executeQuery();

        if(!resultSet.isBeforeFirst())
        {
            // no results available
            System.out.println("Nothing to delete.");
        }
        else
            {
            while (resultSet.next())
            {
                String record_id = resultSet.getString("record_id");
                String expiration_date = resultSet.getString("expiration_date");
                Date date = formatter.parse(expiration_date);
                if(date.compareTo(now) < 0 )
                {
                    PreparedStatement updateStatement = connection.prepareStatement("UPDATE fridge_records " +
                            "SET deleted = ? WHERE record_id = ?");
                    updateStatement.setString(1, "1");
                    updateStatement.setString(2, record_id);
                    updateStatement.executeUpdate();
                }
            }
        }
    }

    public static String searchName(String product_name) throws SQLException
    {
        String barcode = null;
        String category = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String dir = System.getProperty("user.dir");
        connection = DriverManager.getConnection("jdbc:sqlite:"+ dir +"\\fridge_java.db");
        preparedStatement = connection.prepareStatement("Select * from fridge_records where product_name LIKE ? LIMIT 1");
        preparedStatement.setString(1, product_name);
        resultSet = preparedStatement.executeQuery();

        if(!resultSet.isBeforeFirst())
        {
            // no results available
            System.out.println("No name match found.");
        }
        else
        {
            System.out.println("Match with name found.");
            while (resultSet.next())
            {
                barcode = resultSet.getString("barcode");

            }
        }
        connection.close();
        return barcode;
    }

    public static String createUniqueBarcode() throws SQLException
    {
        String unique_barcode = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String dir = System.getProperty("user.dir");
        connection = DriverManager.getConnection("jdbc:sqlite:"+ dir +"\\fridge_java.db");
        preparedStatement = connection.prepareStatement("select MIN(barcode) as barcode from fridge_records " +
                "where length(barcode) < 13");
        resultSet = preparedStatement.executeQuery();
        String barcode = resultSet.getString("barcode");

        if(barcode.equals(""))
        {

            System.out.println("No short barcode found.");
            unique_barcode = "100000000000";
        }
        else
        {
            while (resultSet.next())
            {
                System.out.println(barcode);
                long current_value = Long.parseLong(barcode);
                current_value += 1;
                unique_barcode = Long.toString(current_value);
            }

        }

        connection.close();

        return unique_barcode;
    }

    public static void printUserDir()
    {
        System.out.println(System.getProperty("user.dir"));
    }

}
