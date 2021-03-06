package dataset.acornsat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class JSONWriter
{
  public static final String INPUT_DIRECTORY = "/Users/michael/govhack/ADayInTheLife/workspace/DataLoaders/src/dataset/acornsat";
  public static final String OUTPUT_FILE = "/Users/michael/govhack/dataset/insert_acornsat.js";
  public static final String COLLECTION_NAME = "temperature";
  
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
    
    
    
    String currentStationID;
    int count = 0;
    for (String fn : filenames) {
      System.out.println(count++);
      if (fn.startsWith("acorn.sat.minT")) {
        // Get the station ID from the filename.
        currentStationID = fn.substring(15, 21);
       
        try {
          // Set up the input files (one for min, one for max).
          FileInputStream fisMin = new FileInputStream(INPUT_DIRECTORY + "/" + "acorn.sat.minT." + currentStationID + ".daily.txt");
          DataInputStream disMin = new DataInputStream(fisMin);
          BufferedReader brMin = new BufferedReader(new InputStreamReader(disMin));
          FileInputStream fisMax = new FileInputStream(INPUT_DIRECTORY + "/" + "acorn.sat.maxT." + currentStationID + ".daily.txt");
          DataInputStream disMax = new DataInputStream(fisMax);
          BufferedReader brMax = new BufferedReader(new InputStreamReader(disMax));
          
          String currentLine, currentDate, currentTemp;
          HashMap<String, String> tempPerYearMin = new HashMap<String, String>();
          HashMap<String, String> tempPerYearMax = new HashMap<String, String>();
          
          while ((currentLine = brMin.readLine()) != null) {
            if (currentLine.startsWith("ACORN-SAT")) continue;
            
            currentDate = currentLine.substring(0, 8);
            currentTemp = currentLine.substring(9, 16).replace(" ", "");
            
            if (currentTemp.equals("99999.9")) {
              currentTemp = null;
            }
            
            tempPerYearMin.put(currentDate, currentTemp);
          }
          
          while ((currentLine = brMax.readLine()) != null) {
            if (currentLine.startsWith("ACORN-SAT")) continue;
            
            currentDate = currentLine.substring(0, 8);
            currentTemp = currentLine.substring(9, 16).replace(" ", "");
            
            if (currentTemp.equals("99999.9")) {
              currentTemp = null;
            }
            
            tempPerYearMax.put(currentDate, currentTemp);
          }
          
          new TemperatureSet(currentStationID, tempPerYearMin, tempPerYearMax, bw);
          
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    
    try {
      bw.write("\n\ndb." + COLLECTION_NAME + ".ensureIndex({year: 1});\n");
      bw.write("\n\ndb." + COLLECTION_NAME + ".ensureIndex({month: 1});\n");
      bw.write("\n\ndb." + COLLECTION_NAME + ".ensureIndex({day: 1});\n");
      bw.write("\n\ndb." + COLLECTION_NAME + ".ensureIndex({state: 1});\n");
      bw.write("\n\ndb." + COLLECTION_NAME + ".ensureIndex({year: 1, month: 2, day: 3});\n");
      bw.write("\n\ndb." + COLLECTION_NAME + ".ensureIndex({year: 1, month: 2, day: 3, state: 4});\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    try {
		bw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    TemperatureSet.printCount();
  }
}

class TemperatureSet
{  
  private String _stationId;
  private HashMap<String, TemperatureDate> _dateList = new HashMap<String, TemperatureDate>();
  
  private static int count = 0;
  
  private class TemperatureDate
  {
    private float _min, _max;
  }
  
  public TemperatureSet(String stationId, HashMap<String, String> minSet, HashMap<String, String> maxSet, BufferedWriter out)
  {
    _stationId = stationId;
    
    for (String s : minSet.keySet())
    {
      TemperatureDate td = new TemperatureDate();
      if (minSet.get(s) != null) td._min = Float.parseFloat(minSet.get(s));
      _dateList.put(s, td);
    }
    
    for (String s : maxSet.keySet())
    {
      TemperatureDate td = _dateList.get(s);
      
      if (td == null) td = new TemperatureDate();
      
      if (maxSet.get(s) != null) td._max = Float.parseFloat(maxSet.get(s));
    }
    
    write(out);
  }
  
  private void write(BufferedWriter out)
  {
    for (String s : _dateList.keySet()) {
      TemperatureDate td = _dateList.get(s);
     
      StringBuffer sb = new StringBuffer();
      sb.append("db." + JSONWriter.COLLECTION_NAME + ".insert({");
      sb.append("stationId: \"" + _stationId + "\"");
      sb.append(", stationName: \"" + Stations.getName(_stationId) + "\"");
      sb.append(", latitude: " + Stations.getLat(_stationId));
      sb.append(", longitude: " + Stations.getLon(_stationId));
      sb.append(", elevation: " + Stations.getElevation(_stationId));
      sb.append(", state: \"" + Stations.getState(_stationId) + "\"");
      sb.append(", year: \"" + Integer.parseInt(s.substring(0,4)) + "\"");
      sb.append(", month: \"" + Integer.parseInt(s.substring(4,6)) + "\"");
      sb.append(", day: \"" + Integer.parseInt(s.substring(6,8)) + "\"");
      sb.append(", min: " + td._min);
      sb.append(", max: " + td._max);
      sb.append("});\n");
      
      try {
        out.write(sb.toString());
      } catch (IOException e) {
        e.printStackTrace();
      }
      
    }
  }
  
  public static void printCount()
  {
    System.out.println("Total Count: " + count);
  }
}