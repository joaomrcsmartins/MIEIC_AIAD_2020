package aiad;

import aiad.util.CsvFileWriter;
import aiad.util.NetworkMap;


public class Initiator {
    public static void main(String[] args){
        Environment env = Environment.getInstance();
        env.startSystem();
        CsvFileWriter csvFile = new CsvFileWriter("metrics.csv");
        NetworkMap map = new NetworkMap(env, csvFile);
    }
}
