package libraryApplecationapp;
import java.sql.*;
import java.util.Scanner;

class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "Srinivas";
    private static final String PASSWORD = "Srinivas@123";
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}

class BookManager {
    private Connection connection = DBConnection.getConnection();

    public void addBook(String title, String author, String genre) {
        try {
            String query = "INSERT INTO books (title, author, genre, availability) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, genre);
            ps.setBoolean(4, true);
            ps.executeUpdate();
            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook(int bookId, String title, String author, String genre) {
        try {
            String query = "UPDATE books SET title = ?, author = ?, genre = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, genre);
            ps.setInt(4, bookId);
            ps.executeUpdate();
            System.out.println("Book updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBook(int bookId) {
        try {
            String query = "DELETE FROM books WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, bookId);
            ps.executeUpdate();
            System.out.println("Book deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchBooks(String keyword) {
        try {
            String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR genre LIKE ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Title: " + rs.getString("title") +
                        ", Author: " + rs.getString("author") + ", Genre: " + rs.getString("genre") +
                        ", Available: " + rs.getBoolean("availability"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class MemberManager {
    private Connection connection = DBConnection.getConnection();

    public void addMember(String name, String contact) {
        try {
            String query = "INSERT INTO members (name, contact, membership_date) VALUES (?, ?, CURDATE())";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, contact);
            ps.executeUpdate();
            System.out.println("Member added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewMembers() {
        try {
            String query = "SELECT * FROM members";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                        ", Contact: " + rs.getString("contact") + ", Membership Date: " + rs.getDate("membership_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class TransactionManager {
    private Connection connection = DBConnection.getConnection();

    public void issueBook(int bookId, int memberId) {
        try {
            String query = "INSERT INTO transactions (book_id, member_id, issue_date, due_date) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY))";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            ps.executeUpdate();

            String updateBook = "UPDATE books SET availability = FALSE WHERE id = ?";
            ps = connection.prepareStatement(updateBook);
            ps.setInt(1, bookId);
            ps.executeUpdate();

            System.out.println("Book issued successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void returnBook(int bookId) {
        try {
            String query = "UPDATE transactions SET return_date = CURDATE() WHERE book_id = ? AND return_date IS NULL";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, bookId);
            ps.executeUpdate();

            String updateBook = "UPDATE books SET availability = TRUE WHERE id = ?";
            ps = connection.prepareStatement(updateBook);
            ps.setInt(1, bookId);
            ps.executeUpdate();

            System.out.println("Book returned successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewTransactions() {
        try {
            String query = "SELECT * FROM transactions";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("Transaction ID: " + rs.getInt("id") +
                        ", Book ID: " + rs.getInt("book_id") +
                        ", Member ID: " + rs.getInt("member_id") +
                        ", Issue Date: " + rs.getDate("issue_date") +
                        ", Due Date: " + rs.getDate("due_date") +
                        ", Return Date: " + rs.getDate("return_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class LibraryApp {
    public static void main(String[] args) {
        BookManager bookManager = new BookManager();
        MemberManager memberManager = new MemberManager();
        TransactionManager transactionManager = new TransactionManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Library Management System ---");
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. Search Books");
            System.out.println("5. Add Member");
            System.out.println("6. View Members");
            System.out.println("7. Issue Book");
            System.out.println("8. Return Book");
            System.out.println("9. View Transactions");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter Title: ");
                    String title = scanner.next();
                    System.out.print("Enter Author: ");
                    String author = scanner.next();
                    System.out.print("Enter Genre: ");
                    String genre = scanner.next();
                    bookManager.addBook(title, author, genre);
                    break;
                case 2:
                    System.out.print("Enter Book ID: ");
                    int bookId = scanner.nextInt();
                    System.out.print("Enter New Title: ");
                    title = scanner.next();
                    System.out.print("Enter New Author: ");
                    author = scanner.next();
                    System.out.print("Enter New Genre: ");
                    genre = scanner.next();
                    bookManager.updateBook(bookId, title, author, genre);
                    break;
                case 3:
                    System.out.print("Enter Book ID: ");
                    bookId = scanner.nextInt();
                    bookManager.deleteBook(bookId);
                    break;
                case 4:
                    System.out.print("Enter Keyword: ");
                    String keyword = scanner.next();
                    bookManager.searchBooks(keyword);
                    break;
                case 5:
                    System.out.print("Enter Name: ");
                    String name = scanner.next();
                    System.out.print("Enter Contact: ");
                    String contact = scanner.next();
                    memberManager.addMember(name, contact);
                    break;
                case 6:
                    memberManager.viewMembers();
                    break;
                case 7:
                    System.out.print("Enter Book ID: ");
                    bookId = scanner.nextInt();
                    System.out.print("Enter Member ID: ");
                    int memberId = scanner.nextInt();
                    transactionManager.issueBook(bookId, memberId);
                    break;
                case 8:
                    System.out.print("Enter Book ID: ");
                    bookId = scanner.nextInt();
                    transactionManager.returnBook(bookId);
                    break;
                case 9:
                    transactionManager.viewTransactions();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
