import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.util.*;

class simplifyjoin {

  // public Vector<String[]> arr_alias;

  public static Vector<String[]> main(String[] args,String query,String regex) {
    final String queryString = query;
    return deriveAlias(queryString,regex);
  }

  private static Vector<String[]> deriveAlias(String queryString,String regex) {
    String query = queryString.replace('\n', ' ');
    final Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher(query);
    Vector<String[]> arr_alias=new Vector<>();
    while (matcher.find()) {
        String alias=query.substring(matcher.start(),matcher.end());
        System.out.println("  try0\n");
        arr_alias.add(alias.split(" "));
    }

    final Pattern condition = Pattern.compile("[\\w]+.[\\w]+(\\s*)=(\\s*)[\\w]+.[\\w]+");
    final Matcher match = condition.matcher(query);
    Map<String,String> conditionMap = new HashMap<String,String>();
    while (match.find()){
      String substr = query.substring(match.start(),match.end());
      String parts[] = substr.split("=");
      conditionMap.put(parts[0],parts[1]);
    }
    
    return arr_alias;
  }
}
