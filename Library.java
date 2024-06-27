import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Library{

    private HashMap<String, Book> books = new HashMap<>();

    public void AddToBooks(Book book){
        books.put(book.getISBN(), book);
    }

    public void printBooks(){
        for(Map.Entry<String, Book> entry: books.entrySet()){
            Book temp = entry.getValue();

            System.out.println(temp.getTitle()+ " " +temp.getAuthor()+ " " +temp.getISBN());
        }
    }

    public static Connection connectToMySQL(String hostname, String port, String dbName, String username, String password) throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", hostname, port, dbName);
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        return connection;
    }

    public static void main(String[] ar){

        Library lib = new Library();

        try(Connection connection = connectToMySQL("127.0.0.1", "3309", "library", "root", "root")){
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");

            while(rs.next()){
                String title = rs.getString("bookTitle");
                String author = rs.getString("bookAuthor");
                String isbn = rs.getString("bookISBN");

                lib.AddToBooks(new Book(title, author, isbn));
            }
            
            rs.close();
            
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
        }

        lib.printBooks();   
    }
}