package ru.promelectronika.util_stuff;

import ru.promelectronika.logHandler.LogHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NetFileCreator {


    public static void surrogateNetFile(String path, String tempPath, String networkName, String password) {

        try {
            File fileForReading = new File(path);
            File tempFile = new File(tempPath + "temp.txt");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForReading));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            List<String> list = bufferedReader.lines().toList();

            for (String line : list) {
                if (line.startsWith("id=")) {
                    line = "id=" + networkName;
                }
                if (line.startsWith("ssid=")) {
                    line = "ssid=" + networkName;
                }
                if (line.startsWith("psk=")) {
                    line = "psk=" + password;
                }
                bufferedWriter.write(line + '\n');

            }
            LogHandler.loggerMain.info("__BEFORE_SUBSTITUTE_LIST  : " + list);
            bufferedReader.close();
            bufferedWriter.close();
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(tempFile));
            BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(path));
            List<String> list2 = bufferedReader2.lines().toList();
            for (String line : list2) {
                bufferedWriter2.write(line + '\n');
            }
            LogHandler.loggerMain.info("__AFTER_SUBSTITUTE_LIST    : " + list2);
            bufferedReader2.close();
            bufferedWriter2.close();
            boolean delete = tempFile.delete();
            LogHandler.loggerMain.info("__TEMP_FILE_DELETED    : " + list2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
