import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class Library{

    private static HashMap<String, Book> books = new HashMap<>();

    public void AddBook(Book book){
        books.put(book.getISBN(), book);
    }

    public Book SearchBook(String bookISBN){
        Book temp = books.get(bookISBN);

        return temp;
    }

    public void ViewBooks(){
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

    public static void updateDatabase(String hostname, String port, String dbName, String username, String password) throws SQLException, ClassNotFoundException {

        try(Connection connection = connectToMySQL(hostname, port, dbName, username, password)){
            
            String selectExistingKeys = "SELECT bookISBN FROM books";
            String insertNewBook = "INSERT INTO books (bookTitle, bookAuthor, bookISBN) VALUES (?, ?, ?)";
        
            HashSet<String> existingKeys = new HashSet<>();
            PreparedStatement selectStatement = connection.prepareStatement(selectExistingKeys);
            ResultSet rs = selectStatement.executeQuery();

            while (rs.next()) {
                existingKeys.add(rs.getString(1));
            }

            for (Map.Entry<String, Book> entry : books.entrySet()) {
                String key = entry.getKey();
                Book value = entry.getValue();
        
                if (!existingKeys.contains(key)) {
                  PreparedStatement insertStatement = connection.prepareStatement(insertNewBook);
                  insertStatement.setString(1, value.getTitle());
                  insertStatement.setString(2, value.getAuthor());
                  insertStatement.setString(3, key);
                  insertStatement.executeUpdate();
                }
              }
        }
    }

    public static void main(String[] ar){

        Library lib = new Library();
        Scanner sc = new Scanner(System.in);

        try(Connection connection = connectToMySQL("127.0.0.1", "3309", "library", "root", "root")){
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");

            while(rs.next()){
                String title = rs.getString("bookTitle");
                String author = rs.getString("bookAuthor");
                String isbn = rs.getString("bookISBN");

                lib.AddBook(new Book(title, author, isbn));
            }
            
            rs.close();
            
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
        }

        int choice, fl = 0;
        String bTitle, bAuthor, bISBN;

        while(true){
            System.out.println("\n**************************************\n1. Add Book\n2. Search For a Book\n3. Display Books\n4. Exit\n*************************************\n");
            System.out.print("Enter your choice : ");
            choice = sc.nextInt();

            switch(choice){
                case 1: System.out.print("Enter Book Title : ");
                        bTitle = sc.next(); 

                        System.out.print("Enter Book Author : ");
                        bAuthor = sc.next(); 

                        System.out.print("Enter Book ISBN : ");
                        bISBN = sc.next(); 
                
                        lib.AddBook(new Book(bTitle, bAuthor, bISBN));
                        break;

                case 2: System.out.print("Enter Book ISBN : ");
                        bISBN = sc.next();   

                        lib.SearchBook(bISBN);
                        break;

                case 3: lib.ViewBooks();
                        break;

                case 4: fl = 1;
                        break;

                default: System.out.println("!!! Invalid Choice !!!");
            }

            if(fl == 1)
                break;
        } 

        try{
            updateDatabase("127.0.0.1", "3309", "library", "root", "root");
        } catch(SQLException | ClassNotFoundException e){
            System.out.println(e);
        }

        sc.close();
    }
}