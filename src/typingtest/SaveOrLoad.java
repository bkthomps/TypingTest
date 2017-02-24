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
class SaveOrLoad {

    private static final Path FILE = Paths.get("TypingData.txt");
    private static final Path DATABASE = Paths.get("Words.txt");
    private static final int LENGTH_OF_DATABASE = 9100;

    static void save() {
        String saveFile = Integer.toString(Data.highCPM) + " " + Integer.toString(Data.highWPM) + " "
                + Integer.toString(Data.lastCPM) + " " + Integer.toString(Data.lastWPM);
        byte data[] = saveFile.getBytes();
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(FILE, WRITE, TRUNCATE_EXISTING))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println("Error in save method");
        }
    }

    static int[] load() {
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
                System.err.println("Error 1 in load method");
            }
        } catch (IOException x) {
            System.err.println("Error 2 in load method");
        }
        if (saveFile == null) {
            saveFile = "0 0 0 0";
        }
        // Create String array from String that is separated by spaces.
        String[] database = saveFile.split("\\s+");
        for (int i = 0; i < database.length; i++) {
            database[i] = database[i].replaceAll("_", " ");
        }
        final int[] numbers = new int[4];
        for (int i = 0; i < 4; i++) {
            numbers[i] = Integer.parseInt(database[i]);
        }
        return numbers;
    }

    static String[] loadDatabase() {
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
            System.err.println("Error in loadDatabase method");
        }
        return saveFile;
    }
}
