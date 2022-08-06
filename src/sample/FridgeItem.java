package sample;

import javafx.scene.control.Button;

public class FridgeItem
{
    private String user;
    private String barcode;
    private String product_name;
    private String category;
    private String expiration_date;
    private Integer numer_items;
    //private Button edit;// = new Button("Edit");
    private Button delete;

    FridgeItem(String user, String barcode, String product_name, String category, String expiration_date, Integer numer_items, Button delete)
    {
        this.user = user;
        this.barcode = barcode;
        this.product_name = product_name;
        this.category = category;
        this.expiration_date = expiration_date;
        this.numer_items = numer_items;
        //this.edit = edit;
        this.delete = delete;
    }

    // this constructor is used only for finding matches in existing database for adding new items
    FridgeItem(String barcode, String product_name, String category, Button delete)
    {
        this.barcode = barcode;
        this.product_name = product_name;
        this.category = category;
        //this.edit = edit;
        this.delete = delete;

    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getBarcode()
    {
        return barcode;
    }

    public void setBarcode(String barcode)
    {
        this.barcode = barcode;
    }

    public String getProduct_name()
    {
        return product_name;
    }

    public void setProduct_name(String product_name)
    {
        this.product_name = product_name;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getExpiration_date()
    {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date)
    {
        this.expiration_date = expiration_date;
    }

    public Integer getNumer_items()
    {
        return numer_items;
    }

    public void setNumer_items(Integer numer_items)
    {
        this.numer_items = numer_items;
    }

    public Button getDelete()
    {
        return delete;
    }

    public void setDelete(Button delete)
    {
        this.delete = delete;
    }
}
