package aiad.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CsvFileWriter {
    FileWriter pw;
    public CsvFileWriter(String fileName){
        try {
            this.pw = new FileWriter(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String str){
        try {
            pw.write(str + "\n");
            pw.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}