package dataset.photosearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class JSONWriter
{  
  private static final String INPUT_FILE  = "/Users/michael/govhack/workspace/DataLoaders/src/dataset/photosearch/PhotoMetaDataClean.csv";
  private static final String OUTPUT_FILE = "/Users/michael/govhack/dataset/insert_photometadata.js";
  
  public static final String COLLECTION_NAME = "photosearch";
  
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
      
      try { 
        bw.write("db." + COLLECTION_NAME + ".remove();\n\n");
      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      }
      
      // Write each photo record.
      String currentLine;
      int i = 0;
      while ((currentLine = br.readLine()) != null) {
        if (i >= 26190 && i <= 40000) { //debug
          new Photo(currentLine, bw);
        }
        i++;
      }
      
      try { 
        bw.write("db." + COLLECTION_NAME + ".ensureIndex({\"start-date\": 1});\n\n");
        bw.write("db." + COLLECTION_NAME + ".ensureIndex({\"archives-location\": 1});\n\n");
        bw.write("db." + COLLECTION_NAME + ".ensureIndex({\"start-date\": 1, \"archives-location\": 2});\n\n");
      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      }
      
      // Close input and output files.
      dis.close();
      bw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

class Photo
{
  private int _barcodeNo;
  private String _title, _startDate, _locationWithinArchives, _largeImageUrl, _smallImageUrl;
  
  public Photo(String record, BufferedWriter out)
  {
    record = record.replaceAll("\"\"", "'");
    record = record.replaceAll("Ð", "-");
    String[] fields = record.split(",");
    
    if (fields[1].startsWith("\"")) {
      _barcodeNo = Integer.parseInt(fields[0]);
      
      int titleStart = record.indexOf("\"", 0) + 1;
      int titleEnd = record.indexOf("\"", titleStart);
      
      _title = record.substring(titleStart, titleEnd);
      
      String recordEnd = record.substring(titleEnd + 2); // +2 allow for end quote and comma.
      
      fields = recordEnd.split(",");
      
      _startDate = fields[0];
      _locationWithinArchives = fields[1];
      _largeImageUrl = fields[2];
      _smallImageUrl = fields[3];
      
    } else {
      _barcodeNo = Integer.parseInt(fields[0]);
      _title = fields[1];
      _startDate = fields[2];
      _locationWithinArchives = fields[3];
      _largeImageUrl = fields[4];
      _smallImageUrl = fields[5];
    }
    
    write(out);
  }
  
  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    
    sb.append("db." + JSONWriter.COLLECTION_NAME + ".insert({");
    
    sb.append("barcode:" + _barcodeNo);
    sb.append(", title: \"" + _title + "\" ");
    sb.append(", \"startDate\": \"" + _startDate + "\" ");
    sb.append(", \"archivesLocation\": \"" + _locationWithinArchives + "\" ");
    sb.append(", \"largeImageUrl\": \"" + _largeImageUrl + "\" ");
    sb.append(", \"smallImageUrl\": \"" + _smallImageUrl + "\" ");
    
    sb.append("});");
    
    return sb.toString();
  }
  
  private void write(BufferedWriter out)
  {
    try { 
      out.write(this.toString() + "\n");
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
