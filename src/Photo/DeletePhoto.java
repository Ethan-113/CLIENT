package Photo;

import java.io.File;

public class DeletePhoto {
    static String path = "pic\\zy.jpg";

    public static void Delete() {

        File file = new File(path);

        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}