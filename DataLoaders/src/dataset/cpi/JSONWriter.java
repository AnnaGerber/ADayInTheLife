package dataset.cpi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class JSONWriter
{
  public static final float MAX_NSW = 178.8f;
  public static final float MAX_VIC = 176.8f;
  public static final float MAX_QLD = 184.7f;
  public static final float MAX_SA = 183.3f;
  public static final float MAX_WA = 179.5f;
  public static final float MAX_TAS = 178.6f;
  public static final float MAX_NT = 176.6f;
  public static final float MAX_ACT = 179.7f;
  public static final float MAX_AUS = 179.5f;
  
  public static final String INPUT_FILE = "/Users/michael/govhack/ADayInTheLife/workspace/DataLoaders/src/dataset/cpi/consumer_price_index.csv";
  public static final String OUTPUT_FILE = "/Users/michael/govhack/dataset/insert_cpi.js";
  public static final String COLLECTION_NAME = "cpi";
  
  public static void main(String[] args)
  {
    try {
      // Input File
      FileInputStream fis = new FileInputStream(INPUT_FILE);
      DataInputStream dis = new DataInputStream(fis);
      BufferedReader br = new BufferedReader(new InputStreamReader(dis));
      
      // Output File - clear it down first.
      File outputFile = new File(OUTPUT_FILE);
      outputFile.delete();
      FileWriter fw = new FileWriter(OUTPUT_FILE,true);
      BufferedWriter bw = new BufferedWriter(fw);
      
      bw.write("db." + COLLECTION_NAME + ".remove();\n\n");
      
      String currentLine;
      while ((currentLine = br.readLine()) != null) {
        new CPI(currentLine, bw);
      }
      
      bw.write("\n\ndb." + COLLECTION_NAME + ".ensureIndex({year: 1});\n");
      bw.write("db." + COLLECTION_NAME + ".ensureIndex({month: 1});");
      bw.write("db." + COLLECTION_NAME + ".ensureIndex({year:1, month: 1});");
      
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

class CPI
{
  private int _year, _month, _quarter;
  private float _nsw, _vic, _qld, _sa, _wa, _tas, _nt, _act, _aus;
  
  public CPI(String record, BufferedWriter out)
  {
    String[] fields = record.split(",");
    _year = Integer.parseInt(fields[0].substring(4,8));
    String monthString = fields[0].substring(0, 3);

    if (monthString.equals("Mar")) {
      _month = 3;
      _quarter = 1;
    } else if (monthString.equals("Jun")) {
      _month = 6;
      _quarter = 2;
    } else if (monthString.equals("Sep")) {
      _month = 9;
      _quarter = 3;
    } else if (monthString.equals("Dec")) {
      _month = 12;
      _quarter = 4;
    } 
    
    if (!fields[1].equals("9999.99")) _nsw = Float.parseFloat(fields[1]) / JSONWriter.MAX_NSW;    
    if (!fields[2].equals("9999.99")) _vic = Float.parseFloat(fields[2]) / JSONWriter.MAX_VIC;
    if (!fields[3].equals("9999.99")) _qld = Float.parseFloat(fields[3]) / JSONWriter.MAX_QLD;
    if (!fields[4].equals("9999.99")) _sa = Float.parseFloat(fields[4]) / JSONWriter.MAX_SA;
    if (!fields[5].equals("9999.99")) _wa = Float.parseFloat(fields[5]) / JSONWriter.MAX_WA;
    if (!fields[6].equals("9999.99")) _tas = Float.parseFloat(fields[6]) / JSONWriter.MAX_TAS;
    if (!fields[7].equals("9999.99")) _nt = Float.parseFloat(fields[7]) / JSONWriter.MAX_NT;
    if (!fields[8].equals("9999.99")) _act = Float.parseFloat(fields[8]) / JSONWriter.MAX_ACT;
    if (!fields[9].equals("9999.99")) _aus = Float.parseFloat(fields[9]) / JSONWriter.MAX_AUS;
    
    write(out);
  }
  
  private void write(BufferedWriter out) {
    StringBuffer sb = new StringBuffer();
    
    sb.append("db." + JSONWriter.COLLECTION_NAME + ".insert({");
    sb.append("year: \"" + _year + "\"");
    sb.append(", month: \"" + _month + "\"");
    sb.append(", quarter: \"" + _quarter + "\"");
    sb.append(", NSW: " + _nsw);
    sb.append(", VIC: " + _vic);
    sb.append(", QLD: " + _qld);
    sb.append(", SA: " + _sa);
    sb.append(", WA: " + _wa);
    sb.append(", TAS: " + _tas);
    sb.append(", NT: " + _nt);
    sb.append(", ACT: " + _act);
    sb.append(", AUS: " + _aus);
    sb.append("});");
    
    try { 
      out.write(sb.toString() + "\n");
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
