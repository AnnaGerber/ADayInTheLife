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
  private static final String COLLECTION_GOVERNMENT = "government";
  private static final String COLLECTION_RAINFALL = "rainfall";
  
  private static Mongo _mongo;
  
  public static void main(String[] args) {
    MongoDBAPI m = new MongoDBAPI("test");
    
    //System.out.println(m.getCPI("1987-12-02", "AUS"));
    //System.out.println(m.getMinTemperature("1987-12-02", "NSW"));
    //System.out.println(m.getMaxTemperature("1987-12-02", "NSW"));
    //System.out.println(m.getPopulation("1987-12-02", "AUS", "male"));
    
    //System.out.println(m.getPrimeMinister("1987-12-02"));
    
    //System.out.println(m.getRainfall("1987-12-02", "NSW"));
  }
  
  public MongoDBAPI(String dbName)
  {
    if (_mongo == null) {
      try {
        _mongo = new Mongo();
      } catch (UnknownHostException e) {
        e.printStackTrace();
      } catch (MongoException e) {
        e.printStackTrace();
      }
    }
	  
    _db = _mongo.getDB(dbName);
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
  
  public String getPhotoList(String date, String keyword)
  {
    DBCollection coll = _db.getCollection(COLLECTION_PHOTOSEARCH);
    
    BasicDBObject query = new BasicDBObject();
    
    if (date != null && !date.equals("")) {
      query.put("startDate", "" + getYear(date));
    }
    
    if (keyword != null && !keyword.equals("")) {
      query.put("title", java.util.regex.Pattern.compile("(?i)" + keyword));
    }
    
    DBCursor cur = coll.find(query);

    DBObject doc = null;
    if (cur.hasNext()) {
      doc = cur.next();
    }
    
    boolean isFirst = true;
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    while (cur.hasNext()) {
      if (!isFirst) sb.append(", ");
      sb.append(cur.next().toString());
      isFirst = false;
    }
    sb.append("]");
    
    return sb.toString();
  }
  
  public String getPrimeMinister(String date)
  {
    date = date.replaceAll("-", "");
    System.out.println(date);
    
    DBCollection coll = _db.getCollection(COLLECTION_GOVERNMENT);
    
    BasicDBObject query = new BasicDBObject();
    query.put("start.date", new BasicDBObject("$lte", date));
    
    DBCursor cur = coll.find(query);
    cur.sort(new BasicDBObject("start.date", -1)); //.limit(1);
    
    DBObject doc = null;
    if (cur.hasNext()) {
      doc = cur.next();
    }
    
    return doc.toString();
  }
  
  public String getRainfall(String date, String state)
  {
    DBCollection coll = _db.getCollection(COLLECTION_RAINFALL);
    
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
    
    return getValue(doc, "rainfall");
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
