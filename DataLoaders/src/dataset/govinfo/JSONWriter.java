package dataset.govinfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JSONWriter
{
  public static final String OUTPUT_FILE = "/Users/michael/govhack/dataset/insert_govinfo.js";
  public static final String COLLECTION_NAME = "government";
  
  public static void main(String[] args) {
    String[] govInfoList = CSVStrings.GOV_INFO.split("~");
    
    System.out.println();
    
    try {
      // Output File - clear it down first.
      File outputFile = new File(OUTPUT_FILE);
      outputFile.delete();
      FileWriter fw = new FileWriter(OUTPUT_FILE,true);
      BufferedWriter bw = new BufferedWriter(fw);
      
      bw.write("db." + COLLECTION_NAME + ".remove();");
      
      for (String s : govInfoList) {
        new Government(s, bw);
      }
      
      bw.write("db." + COLLECTION_NAME + ".ensureIndex({\"start.year\": 1, \"start.month\": 2, \"start.day\": 3});");
      bw.write("db." + COLLECTION_NAME + ".ensureIndex({\"end.year\": 1, \"end.month\": 2, \"end.day\": 3});");
      
      bw.close();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    
  }
}

class Government
{
  private String _ministry, _startYear, _startMonth, _startDay, _endYear, _endMonth, _endDay, _party;
  
  public Government(String record, BufferedWriter out)
  {
    String[] fields = record.split(",");
    
    _ministry = fields[1];
    _party = fields[4];
    
    String[] startYear = fields[2].split(" ");
    String[] endYear = fields[3].split(" ");
    
    _startYear = startYear[2];
    _startMonth = getNumericMonth(startYear[1]);
    _startDay = startYear[0];
    
    _endYear = endYear[2];
    _endMonth = getNumericMonth(endYear[1]);
    _endDay = endYear[0];
    
    write(out);
  }
  
  private String getNumericMonth (String month)
  {
    if (month.equals("January")) {
      return "1";
    } else if (month.equals("February")) {
      return "2";
    } else if (month.equals("March")) {
      return "3";
    } else if (month.equals("April")) {
      return "4";
    } else if (month.equals("May")) {
      return "5";
    } else if (month.equals("June")) {
      return "6";
    } else if (month.equals("July")) {
      return "7";
    } else if (month.equals("August")) {
      return "8";
    } else if (month.equals("September")) {
      return "9";
    } else if (month.equals("October")) {
      return "10";
    } else if (month.equals("November")) {
      return "11";
    } else if (month.equals("December")) {
      return "12";
    }
    
    return "";
  }
  
  private void write(BufferedWriter out)
  {
    StringBuffer sb = new StringBuffer();
    
    sb.append("db." + JSONWriter.COLLECTION_NAME + ".insert({");
    sb.append("ministry: \"" + _ministry + "\"");
    sb.append(", party: \"" + _party + "\"");
    sb.append(", start: {");
    sb.append("date: \"" + String.format("%04d", Integer.parseInt(_startYear)) + String.format("%02d", Integer.parseInt(_startMonth)) + String.format("%02d", Integer.parseInt(_startDay)) + "\"");
    sb.append(", year: \"" + _startYear + "\"");
    sb.append(", month: \"" + _startMonth + "\"");
    sb.append(", day: \"" + _startDay + "\"");
    sb.append("}");
    sb.append(", end: {");
    sb.append("date: \"" + String.format("%04d", Integer.parseInt(_endYear)) + String.format("%02d", Integer.parseInt(_endMonth)) + String.format("%02d", Integer.parseInt(_endDay)) + "\"");
    sb.append(", year: \"" + _endYear + "\"");
    sb.append(", month: \"" + _endMonth + "\"");
    sb.append(", day: \"" + _endDay + "\"");
    sb.append("}");
    sb.append("});");
    
    try {
      out.write(sb.toString() + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
