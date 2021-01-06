package prj.demo.myveganorder;

// TODO: 處理資料庫 送來的資料
public class DataModel {
    private String category;
    private String image;
    private String name;
    private String desc;
    private String price;

    public DataModel(String category, String image, String name, String desc, String price) {
        this.category = category;
        this.image = image;
        this.name = name;
        this.desc = desc;
        this.price = price;
    }

    public String getCategory() { return category; }
    public String getImage() { return image; }
    public String getName() { return name; }
    public String getDesc() { return desc; }
//    public int getPrice() { return Integer.parseInt(price); } // 價格轉為整數
    public String getPrice() { return price; } // 價格轉為整數
}
