import java.util.InputMismatchException;
import java.util.Scanner;

public class BookApplication {
    public static void main(String[] args) {
        BookDatabaseManager bookDatabaseManager = new BookDatabaseManager();
        Scanner scanner = new Scanner(System.in);

        int userChoice;

        do {
            showMainMenu();
            userChoice = retrieveUserChoice(scanner);

            switch (userChoice) {
                case 1:
                    for (Book book : bookDatabaseManager.getBookList()) {
                        String authors = "";
                        for (int i = 0; i < book.getAuthorList().size(); i++) {
                            Author author = book.getAuthorList().get(i);
                            authors += String.format("%s %s", author.getFirstName(), author.getLastName());
                            if (i < book.getAuthorList().size() - 1) {
                                authors += " | ";
                            }
                        }
                        System.out.println(String.format(
                                "Title: %s\nISBN: %s\nEdition: %d\nCopyright: %s\nAuthors: %s\n",
                                book.getTitle(), book.getISBN(), book.getEdition(), book.getCopyright(), authors
                        ));
                    }
                    break;
                case 2:
                    for (Author author : bookDatabaseManager.getAuthorList()) {
                        String books = "";
                        for (int i = 0; i < author.getBookList().size(); i++) {
                            Book authorBook = author.getBookList().get(i);
                            books += authorBook.getTitle();
                            if (i < author.getBookList().size() - 1) {
                                books += " | ";
                            }
                        }
                        System.out.println(String.format(
                                "Author ID: %d\nFirst Name: %s\nLast Name: %s\nBooks: %s\n",
                                author.getAuthorID(), author.getFirstName(), author.getLastName(), books
                        ));
                    }
                    break;
                case 3:
                    scanner.nextLine();
                    System.out.println("\nISBN: ");
                    String isbn = scanner.nextLine();
                    System.out.println("Title: ");
                    String title = scanner.nextLine();
                    System.out.println("Edition: ");
                    int edition = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Copyright: ");
                    String copyright = scanner.nextLine();

                    Book newBook = new Book(isbn, title, edition, copyright);
                    System.out.println("Number of Authors: ");
                    int numAuthors = scanner.nextInt();

                    for (int i = 0; i < numAuthors; i++) {
                        System.out.println("Enter Author ID:");
                        int authorID = scanner.nextInt();
                        Author existingAuthor = bookDatabaseManager.findAuthorById(authorID);
                        if (existingAuthor != null) {
                            newBook.getAuthorList().add(existingAuthor);
                        } else {
                            System.out.println("Author with ID " + authorID + " does not exist. Please enter a valid Author ID.");
                            i--; // Decrementing i to ensure the loop doesn’t advance to the next iteration.
                        }
                    }

                    if (bookDatabaseManager.addNewBook(newBook)) {
                        System.out.println("Book added successfully!");
                    } else {
                        System.out.println("Failed to add the book.");
                    }
                    break;
                case 4:
                    int authorID = bookDatabaseManager.getAuthorList().size() + 1;
                    scanner.nextLine();
                    System.out.println("First Name: ");
                    String firstName = scanner.nextLine();
                    System.out.println("Last Name: ");
                    String lastName = scanner.nextLine();
                    System.out.println(String.format(
                            "%s %s has been added.\nAuthor ID: %d\n",
                            firstName, lastName, authorID
                    ));

                    Author newAuthor = new Author(authorID, firstName, lastName);
                    bookDatabaseManager.addNewAuthor(newAuthor);
                    break;
                case 5:
                    System.out.println("Thank you for using our Books-Database application. Goodbye.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again!\n");
            }
        } while (userChoice != 5);
    }

    public static void showMainMenu() {
        System.out.println("Books Database Main Menu");
        System.out.println("Please select one of the following choices below:\n");
        System.out.println("1) Print all the books from the database (showing the authors)");
        System.out.println("2) Print all the authors from the database (showing the books)");
        System.out.println("3) Add a book to the database for an existing author");
        System.out.println("4) Add a new author");
        System.out.println("5) Quit\n");
    }

    public static int retrieveUserChoice(Scanner inputDevice) {
        while (true) {
            try {
                System.out.print("Enter an option (1-5):\n");
                return inputDevice.nextInt();
            } catch (InputMismatchException e) {
                inputDevice.nextLine();
                System.err.print("Invalid choice. Please try again!\n");
            }
        }
    }
}