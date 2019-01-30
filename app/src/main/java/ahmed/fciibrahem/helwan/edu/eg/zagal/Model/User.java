package ahmed.fciibrahem.helwan.edu.eg.zagal.Model;

public class User {
    private String username;
    private String id;
    private String imageURl;
    private String status;
    private String search;

    public User(String username, String id, String imageURl,String status,String search) {
        this.username = username;
        this.id = id;
        this.imageURl = imageURl;
        this.status=status;
        this.search=search;
    }
    public User()
    {

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURl() {
        return imageURl;
    }

    public void setImageURl(String imageURl) {
        this.imageURl = imageURl;
    }

    public String getStatus() {
        return status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setStatus(String status) {
        this.status = status;

    }
}
