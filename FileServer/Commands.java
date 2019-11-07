import java.util.ArrayList;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;
import java.io.File;

public class Commands {
    public static boolean validateCommand(String command) {
        Pattern pattern = Pattern.compile("^(cd ..|cd \w/+|mkdir \w+|ls|get \w+.\w+|put \w+.\w+)$");
        Matcher matcher = pattern.matcher(command);

        return matcher.matches();
    }

    public static void getFile(String filePath, String fileName) {
        File getFile = new File();

        if (!getFile.isFile(filePath + fileName)
            return;

        
    }

    public static void putFile(String filePath, String fileName) {
        File newFile = new File();

        if (newFile.isFile(filePath + fileName) || newFile.isDirectory(filePath + fileName))
            return false;

        return newFile.createNewFile(filePath + fileName);
    }

    public static String[] listDir(String path) {
        return list(path);
    }


}
