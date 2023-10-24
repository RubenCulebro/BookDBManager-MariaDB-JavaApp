import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDatabaseManager {
    static private String DATABASE_URL = "jdbc:mariadb://localhost:3306/sampleDB";
    static private String USER = "root";
    static private String PASS = "root";
    private List<Author> authorList = new ArrayList<>();
    private List<Book> bookList = new ArrayList<>();


    public BookDatabaseManager(){
        loadAuthors();
        loadBooks();
    }

    public List<Author> getAuthorList() {
        return authorList;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    private void loadDB(String sql) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                if (!sql.contains("titles")) {
                    Author author = new Author(rs.getInt("authorID"), rs.getString("firstName"), rs.getString("lastName"));

                    String query = "SELECT t.isbn, t.title, t.editionNumber, t.copyright " +
                            "FROM titles t JOIN authorISBN ai " +
                            "ON t.isbn = ai.isbn " +
                            "JOIN authors a " +
                            "ON ai.authorID = a.authorID " +
                            "WHERE a.authorID = ?";

                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, author.getAuthorID());
                    ResultSet rsBooks = pstmt.executeQuery();

                    while (rsBooks.next()) {
                        Book book = new Book(rsBooks.getString("isbn"), rsBooks.getString("title"), rsBooks.getInt("editionNumber"), rsBooks.getString("copyright"));
                        author.getBookList().add(book);
                    }
                    this.getAuthorList().add(author);
                } else {
                    Book book = new Book(rs.getString("isbn"), rs.getString("title"), rs.getInt("editionNumber"), rs.getString("copyright"));

                    String query = "SELECT a.authorID, a.firstName, a.lastName " +
                            "FROM authors a JOIN authorISBN ai " +
                            "ON a.authorID = ai.authorID " +
                            "JOIN titles t " +
                            "ON ai.isbn = t.isbn " +
                            "WHERE t.isbn = ?";

                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, book.getISBN());
                    ResultSet rsAuthors = pstmt.executeQuery();

                    while (rsAuthors.next()) {
                        Author author = new Author(rsAuthors.getInt("authorID"), rsAuthors.getString("firstName"), rsAuthors.getString("lastName"));
                        book.getAuthorList().add(author);
                    }
                    this.getBookList().add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadAuthors(){
        String sqlAuthors = "SELECT * FROM authors";
        loadDB(sqlAuthors);
    }

    public void loadBooks(){
        String sqlBooks = "SELECT * FROM titles";
        loadDB(sqlBooks);
    }

    public void addNewAuthor(Author author){
        String sqlAuthors= "INSERT INTO authors (authorID, firstName, lastName) VALUES (?, ?, ?)";
        String sqlAuthorISBN = "INSERT INTO authorISBN (authorID, isbn) VALUES (?, ?)";

        PreparedStatement pstmt = null;
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS)){
            pstmt = conn.prepareStatement(sqlAuthors);
            pstmt.setInt(1, author.getAuthorID());
            pstmt.setString(2, author.getFirstName());
            pstmt.setString(3, author.getLastName());
            pstmt.execute();

            pstmt = conn.prepareStatement(sqlAuthorISBN);
            pstmt.setInt(1, author.getAuthorID());
            for(Book book : author.getBookList()){
                pstmt.setString(2, book.getISBN());
                pstmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.getAuthorList().clear();
        loadAuthors();
    }

    public boolean addNewBook(Book book){
        String sqlBooks = "INSERT INTO titles (isbn, title, editionNumber, copyright) VALUES (?, ?, ?, ?)";
        String sqlAuthorISBN = "INSERT INTO authorISBN (authorID, isbn) VALUES (?, ?)";

        PreparedStatement pstmt = null;
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS)){
            pstmt = conn.prepareStatement(sqlBooks);
            pstmt.setString(1, book.getISBN());
            pstmt.setString(2, book.getTitle());
            pstmt.setInt(3, book.getEdition());
            pstmt.setString(4, book.getCopyright());
            pstmt.execute();

            pstmt = conn.prepareStatement(sqlAuthorISBN);
            pstmt.setString(2, book.getISBN());
            for(Author author : book.getAuthorList()){
                pstmt.setInt(1, author.getAuthorID());
                pstmt.execute();
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("The book with ISBN " + book.getISBN() + " is already registered.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.getBookList().clear();
        loadBooks();
        return true;
    }

    public Author findAuthorById(int authorId) {
        Author author = null;
        String query = "SELECT * FROM authors WHERE authorID = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, authorId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("authorID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                author = new Author(id, firstName, lastName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return author;
    }
}