import java.util.ArrayList;
import java.util.List;

public class Book {
    private List<Author> authorList = new ArrayList<>();
    private String ISBN;
    private String title;
    private int edition;
    private String copyright;

    public List<Author> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
    }

    public Book(String ISBN, String title, int edition, String copyright){
        this.ISBN = ISBN;
        this.title = title;
        this.edition = edition;
        this.copyright = copyright;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
}
