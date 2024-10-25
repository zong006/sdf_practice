package p03;

public class Item {
    private String prod_id;
    private String title;
    private float price;
    private int rating;
    
    public String getProd_id() {
        return prod_id;
    }
    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    
}
