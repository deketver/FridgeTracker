package sample;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.http.HttpResponse;
import java.util.Scanner;

public class FoodApiHandler
{
    String api_url = "https://world.openfoodfacts.org/api/v0/product/";
    FoodApiHandler(String barcode)
    {
        api_url = api_url + barcode + ".json";
    }

    public void create_connection() throws IOException
    {
        URL url = new URL(api_url);
        System.out.println("Search string is: " + api_url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if(connection.getResponseCode() == 200)
        {
            Scanner scanner = new Scanner(url.openStream());
            while(scanner.hasNext())
            {
                String temp = scanner.nextLine();
                System.out.println(temp);
                
                // System.out.println(connection.getResponseMessage()); result for status 200 is OK
                //JSONObject object = new JSONObject(temp);
            }


        }

    }
}
