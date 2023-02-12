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
        // String[] tem_arr=alias.split(" ");
        // System.out.println("  try0\n");
        arr_alias.add(alias.split(" "));
      //   for (String[] a : arr_alias) {
      //     System.out.println("  try1\n");
      //     for(String b:a) {
      //         System.out.println(b+"  try\n");
      //     }
      // }
    }
    return arr_alias;
  }
}
