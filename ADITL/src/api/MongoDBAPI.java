package api;
import java.io.IOException;
import java.net.UnknownHostException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;


public class MongoDBAPI
{
  public static final String STATE_ACT = "ACT";
  public static final String STATE_ALL = "AUS";
  public static final String STATE_NSW = "NSW";
  public static final String STATE_NT  = "NT";
  public static final String STATE_QLD = "QLD";
  public static final String STATE_SA  = "SA";
  public static final String STATE_TAS = "TAS";
  public static final String STATE_VIC = "VIC";
  public static final String STATE_WA  = "WA";
  
  public static final String GENDER_MALE = "male";
  public static final String GENDER_FEMALE = "female";
  public static final String GENDER_ALL = "total";
  
  public static final String TEMPERATURE_MIN = "min";
  public static final String TEMPERATURE_MAX = "max";
  
  private DB _db;
  private static final String COLLECTION_POPULATION = "population";
  private static final String COLLECTION_TEMPERATURE = "temperature";
  private static final String COLLECTION_CPI = "cpi";
  private static final String COLLECTION_PHOTOSEARCH = "photosearch";
  
  public static void main(String[] args) {
    MongoDBAPI m = new MongoDBAPI("test");
    
    System.out.println(m.getCPI("1987-12-02", "AUS"));
    System.out.println(m.getMinTemperature("1987-12-02", "NSW"));
    System.out.println(m.getMaxTemperature("1987-12-02", "NSW"));
    System.out.println(m.getPopulation("1987-12-02", "AUS", "male"));
  }
  
  public MongoDBAPI(String dbName)
  {
    Mongo m;
    try {
      m = new Mongo();
      _db = m.getDB(dbName);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (MongoException e) {
      e.printStackTrace();
    }
  }
  
  public String getCPI(String date, String state) {
    DBCollection coll = _db.getCollection(COLLECTION_CPI);
    
    BasicDBObject query = new BasicDBObject();
    query.put("year", "" + getYear(date));
    query.put("quarter", "" + ((getMonth(date) % 4) + 1));
    DBCursor cur = coll.find(query);
    
    DBObject doc = null;
    if (cur.hasNext()) {
      doc = cur.next();
    }
    
    return getValue(doc, state);
  }
  
  private String getTemperature(String date, String state, String bound)
  {
    DBCollection coll = _db.getCollection(COLLECTION_TEMPERATURE);
    
    BasicDBObject query = new BasicDBObject();
    query.put("year", "" + getYear(date));
    int y = getYear(date);
    query.put("month", "" + getMonth(date));
    int m = getMonth(date);
    query.put("day", "" + getDay(date));
    int d = getDay(date);
    query.put("state", "" + state);
    
    DBCursor cur = coll.find(query);

    DBObject doc = null;
    if (cur.hasNext()) {
      doc = cur.next();
    }
    
    return getValue(doc, bound);
  }
  
  public String getMinTemperature(String date, String state)
  {
    return getTemperature(date, state, TEMPERATURE_MIN);
  }
  
  public String getMaxTemperature(String date, String state)
  {
    return getTemperature(date, state, TEMPERATURE_MAX);
  }
  
  public String getPopulation(String date, String state, String gender) 
  {
    DBCollection coll = _db.getCollection(COLLECTION_POPULATION);
    
    BasicDBObject query = new BasicDBObject();
    query.put("year", "" + getYear(date));
    
    DBCursor cur = coll.find(query);

    DBObject doc = null;
    if (cur.hasNext()) {
      doc = cur.next();
    }
    
    if (gender == null) gender = GENDER_ALL;
    
    return getValue(doc, state, gender);
  }
  
  private String getValue(DBObject doc, String path)
  {
    if (doc == null) {
      return "";
    }
    
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(doc.toString());
     
      JsonNode nameNode = rootNode.path(path);
      
      return nameNode.asText();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return "";
  }
  
  private String getValue(DBObject doc, String path, String path2)
  {
    if (doc == null) {
      return "";
    }
    
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(doc.toString());
     
      JsonNode nameNode = rootNode.path(path).path(path2);
      
      return nameNode.asText();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return "";
  }
  
  private int getYear(String date) {
    return Integer.parseInt(date.split("-")[0]);
  }
  
  private int getMonth(String date) {
    return Integer.parseInt(date.split("-")[1]);
  }
  
  private int getDay(String date) {
    return Integer.parseInt(date.split("-")[2]);
  }
}
