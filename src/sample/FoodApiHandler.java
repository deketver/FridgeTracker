package sample;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoodApiHandler
{
    String api_url = "https://world.openfoodfacts.org/api/v0/product/";
    String product_name = null;
    String[] categories = null;

    FoodApiHandler(String barcode)
    {
        api_url = api_url + barcode + ".json";
    }

    public void create_connection() throws IOException
    {
        URL url = new URL(api_url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if(connection.getResponseCode() == 200)
        {
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext())
            {
                String temp = scanner.nextLine();

                Pattern category_pattern = Pattern.compile("categories\":\"([A-Z,a-zěščřžýáíéúů: -]*)");
                Pattern name_pattern = Pattern.compile("product_name\":\"([A-Z,a-z-ěščřžýáíéúů ]*)");

                Matcher category_matcher = category_pattern.matcher(temp);
                Matcher name_matcher = name_pattern.matcher(temp);


                boolean matchFound = category_matcher.find();
                boolean nameMatch = name_matcher.find();
                if (matchFound)
                {
                    String unprocesed_categories = category_matcher.group();
                    System.out.println(unprocesed_categories);
                    try
                    {
                        String[] split_categories = unprocesed_categories.split("\"");
                        System.out.println(Arrays.toString(split_categories));
                        categories = split_categories[split_categories.length - 1].split(",");
                        System.out.println(Arrays.toString(categories));

                        Pattern patern_en = Pattern.compile("en:");
                        for(int i = 0; i < categories.length; i++)
                        {
                            Matcher en_matcher = patern_en.matcher(categories[i]);
                            if(en_matcher.find())
                            {
                                String[] splitted_result = categories[i].split(":");
                                categories[i] = splitted_result[splitted_result.length -1];
                            }
                            //categories[i] = categories[i].replaceAll("\\s+","");

                        }

                    } catch (Exception e)
                    {
                        System.out.println(e.getMessage());
                    }
                }
                if (nameMatch) {
                    String unprocesed_name = name_matcher.group();
                    System.out.println(unprocesed_name);
                    try {
                        String[] split_product_name = unprocesed_name.split("\"");
                        product_name = split_product_name[split_product_name.length - 1];
                        System.out.println(product_name);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());

                    }
                }
                // System.out.println(connection.getResponseMessage()); result for status 200 is OK

            }
        }

    }

    public String return_product_name()
    {
        return product_name;
    }

    public String[] return_categories()
    {
        return categories;
    }
}
