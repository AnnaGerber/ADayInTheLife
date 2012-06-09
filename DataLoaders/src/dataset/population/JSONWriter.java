package dataset.population;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class JSONWriter
{
  public static final String COLLECTION_NAME = "population";
  private static final String OUTPUT_FILE = "/Users/michael/govhack/dataset/insert_population.js";
  
  private static ArrayList<PopulationYear> _populationByYearList = new ArrayList<PopulationYear>();
  
  public static void main(String[] args) {
    String[] stateList = CSVStrings.MALE_CSV.split("-");
    
    String[] yearList = stateList[0].split(",");
    for (int i = 1; i < yearList.length; i++)
    {
      _populationByYearList.add(new PopulationYear(Integer.parseInt(yearList[i])));
    }
    
    parse(stateList, PopulationYear.MALE);
    
    stateList = CSVStrings.FEMALE_CSV.split("-");
    parse(stateList, PopulationYear.FEMALE);
    
    stateList = CSVStrings.PERSON_CSV.split("-");
    parse(stateList, PopulationYear.PERSON);
    
    try {
      // Output File - clear it down first.
       File outputFile = new File(OUTPUT_FILE);
       outputFile.delete();
       FileWriter fw = new FileWriter(OUTPUT_FILE, true);
       BufferedWriter bw = new BufferedWriter(fw);
       
       bw.write("db." + COLLECTION_NAME + ".remove();\n\n");
       
       for (PopulationYear py : _populationByYearList) 
       {
         write(py.toString(), bw);
       }
       
       bw.write("\ndb." + COLLECTION_NAME + ".ensureIndex({year: 1});");
       
       bw.close();
     } catch (IOException e) {
       e.printStackTrace();
     }
    
    
  }
  
  public static void parse(String[] stateList, String gender)
  {
    String currentState;
    for (int i = 1; i < stateList.length ; i++)
    {  
      String[] populationList = stateList[i].split(",");
      currentState = populationList[0];
      
      for (int j = 1; j < populationList.length; j++)
      {
        _populationByYearList.get(j - 1).setPopulation(gender, currentState, populationList[j]);
      }
    }
  }
  
  public static void write(String record, BufferedWriter out)
  {
    try {
      out.write("db." + COLLECTION_NAME + ".insert(" + record + ");\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}



class PopulationYear
{
  public static final String MALE   = "MALE";
  public static final String FEMALE = "FEMALE";
  public static final String PERSON = "PERSON";
  
  public enum GENDERS {
    MALE, FEMALE, PERSON
  } 
  
  private class StateType {
    private String _stateMnem;
    private int _male, _female, _person;
    
    public StateType(String stateMnem)
    {
      _stateMnem = stateMnem; 
    }
    
    @Override
    public String toString()
    {
      StringBuffer sb = new StringBuffer();
      
      sb.append(_stateMnem + ": {");
      sb.append("male: " + _male);
      sb.append(", female: " + _female);
      sb.append(", total: " + _person);
      sb.append("}");
      
      return sb.toString();
    }
    
  }
  
  private int _year;
  private HashMap<String, StateType> _stateTypeList;
  
  public PopulationYear(int year)
  {
    _year = year;    
    _stateTypeList = new HashMap<String, StateType>();
    _stateTypeList.put("NSW", new StateType("NSW"));
    _stateTypeList.put("VIC", new StateType("VIC"));
    _stateTypeList.put("QLD", new StateType("QLD"));
    _stateTypeList.put("SA",  new StateType("SA"));
    _stateTypeList.put("WA",  new StateType("WA"));
    _stateTypeList.put("TAS", new StateType("TAS"));
    _stateTypeList.put("NT",  new StateType("NT"));
    _stateTypeList.put("ACT", new StateType("ACT"));
    _stateTypeList.put("AUS", new StateType("AUS"));
  }
  
  public void setPopulation(String gender, String state, String population)
  {
    StateType st = _stateTypeList.get(state);
    
    if (population.equals("") || population == null) {
      return;
    } else {
      switch (GENDERS.valueOf(gender)) {
        case MALE:   st._male   = Integer.parseInt(population); break;
        case FEMALE: st._female = Integer.parseInt(population); break;
        case PERSON: st._person = Integer.parseInt(population); break;
      }
    }
  }

  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    
    sb.append("{");
    sb.append("year: \"" + _year + "\"");
    
    for (StateType st : _stateTypeList.values()) {
      sb.append(", " + st);
    }
    
    sb.append("}");
    
    return sb.toString();
  } 
}