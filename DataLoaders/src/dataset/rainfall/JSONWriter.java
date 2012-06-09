package dataset.rainfall;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import dataset.acornsat.Stations;

public class JSONWriter
{
  public static final String INPUT_DIRECTORY = "/Users/michael/govhack/ADayInTheLife/workspace/DataLoaders/src/dataset/rainfall";
  public static final String OUTPUT_FILE = "/Users/michael/govhack/dataset/insert_rainfall.js";
  public static final String COLLECTION_NAME = "rainfall";
  
  public static void main(String[] args)
  {
    File directory = new File(INPUT_DIRECTORY);
    
    String[] filenames = directory.list();
    
    BufferedWriter bw = null;
    try {
      // Output File - clear it down first.
      File outputFile = new File(OUTPUT_FILE);
      outputFile.delete();
      FileWriter fw = new FileWriter(OUTPUT_FILE,true);
      bw = new BufferedWriter(fw);
      
      bw.write("db." + COLLECTION_NAME + ".remove();\n\n");
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    
    String currentLine;
    for (String fn : filenames) {
      if (fn.startsWith("IDCJAC")) {
        System.out.println(fn);
        try {
          // Set up the input files (one for min, one for max).
          FileInputStream fis = new FileInputStream(INPUT_DIRECTORY + "/" + fn);
          DataInputStream dis = new DataInputStream(fis);
          BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    
          while ((currentLine = br.readLine()) != null) {
            if (currentLine.startsWith("Bureau")) { continue; }
            else {
            new RainFall(currentLine, bw, fn);
            }
          }
          
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    
    try {
      bw.write("\n\ndb." + COLLECTION_NAME + ".ensureIndex({year: 1});");
      bw.write("\ndb." + COLLECTION_NAME + ".ensureIndex({state: 1});");
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class RainFall
{
//Product code,Bureau of Meteorology station number,Year,Month,Day,Rainfall amount (millimetres),Period over which rainfall was measured (days),Quality
  //IDCJAC0009,009021,1944,01,01,,,
  
  private String _stationId, _year, _month, _day, _rainfall;
  
  public RainFall(String record, BufferedWriter out, String fn)
  {
    try {
    String[] fields = record.split(",");
    //System.out.println(record);
    _stationId = fields[1];
    _year = "" + Integer.parseInt(fields[2]);
    _month = "" + Integer.parseInt(fields[3]);
    _day = "" + Integer.parseInt(fields[4]);
    
    if (fields.length >= 6) {
      _rainfall = fields[5];
    }
    
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println(_year + ", " + _month + ", " + _day + ", " + _stationId + ", " + fn);
    }
    
    write(out);
    
  }
  
  private void write(BufferedWriter out)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("db." + JSONWriter.COLLECTION_NAME + ".insert({");
    sb.append("stationId: \"" + _stationId + "\"");
    sb.append(", stationName: \"" + Stations.getName(_stationId) + "\"");
    sb.append(", latitude: \"" + Stations.getLat(_stationId) + "\"");
    sb.append(", longitude: \"" + Stations.getLon(_stationId) + "\"");
    sb.append(", elevation: \"" + Stations.getElevation(_stationId) + "\"");
    sb.append(", state: \"" + Stations.getState(_stationId) + "\"");
    
    sb.append(", year: \"" + _year + "\"");
    sb.append(", month: \"" + _month + "\"");
    sb.append(", day: \"" + _day + "\"");
    sb.append(", rainfall: \"" + _rainfall + "\"");
    
    sb.append("});\n");
    
    try {
      out.write(sb.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    //System.out.println(sb.toString());
  }
}
