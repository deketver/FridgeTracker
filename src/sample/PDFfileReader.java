package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class PDFfileReader
{

    public static ObservableList<FridgeItem> pythonFileProcess(String filenamepath, String user)
    {
        ObservableList<FridgeItem> ItemsList = FXCollections.observableArrayList();
        String product_name = null;
        int number_items = 0;

        try {

            ProcessBuilder builder = new ProcessBuilder("C:\\Users\\veron\\AppData\\Local\\Programs\\Python\\Python39\\python.exe", System.getProperty("user.dir") + "\\python_read_file.py", filenamepath);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error_reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String lines = null;

            int counter = 0;
            while ((lines = reader.readLine()) != null)
            {
                //System.out.println(lines);
                if(counter % 2 ==1 )
                {
                    number_items = Integer.parseInt(lines);
                    FridgeItem item = new FridgeItem(user, "", product_name, "", "", number_items, new Button("Add"));
                    ItemsList.add(item);

                }
                else
                {
                    product_name = lines;
                    System.out.println(product_name);
                }
                counter +=1;
                //System.out.println(number_items);

            }
            System.out.println(counter);
            System.out.println("Finished output print");

            while ((lines = error_reader.readLine()) != null) {
                System.out.println(lines);
            }

            System.out.println("Finished error print");
        } catch (Exception e) {
            System.out.println(e);
        }
        return ItemsList;
    }


}
