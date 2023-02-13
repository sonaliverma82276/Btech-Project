import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.util.*;

class simplifyjoin {

  // public Vector<String[]> arr_alias;

  public static Vector<String[]> main(String[] args,String query,String regex,String sp) {
    final String queryString = query;
    return deriveAlias(queryString,regex,sp);
  }

  private static Vector<String[]> deriveAlias(String queryString,String regex,String sp) {
    String query = queryString.replace('\n', ' ');
    final Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher(query);
    Vector<String[]> arr_alias=new Vector<>();
    while (matcher.find()) {
        String alias=query.substring(matcher.start(),matcher.end());
        // System.out.println("  try0\n");
        arr_alias.add(alias.split(sp));
    }
    
    return arr_alias;
  }
}
