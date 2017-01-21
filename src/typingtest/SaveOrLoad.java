package typingtest;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * Used for saving to file and loading from file.
 */
public class SaveOrLoad {

    private static final Path FILE = Paths.get("TypingData.txt");
    private static final Path DATABASE = Paths.get("Words.txt");
    private static final int LENGTH_OF_DATABASE = 9903;

    private SaveOrLoad() {
        //not used
    }
    
    public static void save() {
        String saveFile = Integer.toString(Data.highCPM) + " " + Integer.toString(Data.highWPM) + " "
                + Integer.toString(Data.lastCPM) + " " + Integer.toString(Data.lastWPM);
        byte data[] = saveFile.getBytes();
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(FILE, WRITE, TRUNCATE_EXISTING))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    public static int[] load() {
        String saveFile = null;
        try {
            Files.createFile(FILE);
        } catch (FileAlreadyExistsException x) {
            try (InputStream in = Files.newInputStream(FILE);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    saveFile = line;
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        if (saveFile == null) {
            saveFile = "0 0 0 0";
        }
        //create String array from String that is seperated by spaces
        String[] database = saveFile.split("\\s+");
        for (int i = 0; i < database.length; i++) {
            database[i] = database[i].replaceAll("_", " ");
        }
        int[] nums = new int[4];
        for (int i = 0; i < 4; i++) {
            nums[i] = Integer.parseInt(database[i]);
        }
        return nums;
    }

    public static String[] loadDatabase() {
        String saveFile[] = new String[LENGTH_OF_DATABASE];
        int index = 0;
        try (InputStream in = Files.newInputStream(DATABASE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                saveFile[index] = line;
                index++;
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return saveFile;
    }
}
