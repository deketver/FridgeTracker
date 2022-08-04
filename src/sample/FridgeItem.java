package sample;

public class FridgeItem
{
    private String user;
    private String barcode;
    private String product_name;
    private String category;
    private String expiration_date;
    private Integer numer_items;

    FridgeItem(String user, String barcode, String product_name, String category, String expiration_date, Integer numer_items)
    {
        this.user = user;
        this.barcode = barcode;
        this.product_name = product_name;
        this.category = category;
        this.expiration_date = expiration_date;
        this.numer_items = numer_items;
    }

    FridgeItem(String barcode, String product_name, String category)
    {
        this.barcode = barcode;
        this.product_name = product_name;
        this.category = category;

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
}
