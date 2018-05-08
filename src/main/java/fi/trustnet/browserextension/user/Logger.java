package fi.trustnet.browserextension.user;

import java.io.FileWriter;

public class Logger {
    public static void writeToLog(String entry) {
        try {
            FileWriter fw = new FileWriter("exceptions.txt", true);
            fw.write(entry + "\n");
            fw.flush();
            fw.close();
        }
        catch (Exception e) {
        }
    }
}
