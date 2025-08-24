import com.kursx.parser.fb2.FictionBook;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Started FB 2 read...");
            FictionBook fictionBook = new FictionBook(new File("file.xml"));
            System.out.println("Completed FB 2 read...\n");

            System.out.println("Title:\t\t" + fictionBook.getTitle());
            System.out.println("Authors:\t" + fictionBook.getAuthors());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
