import java.util.*;

import javafx.util.Pair;

public class split {
    public static int j=0;
    public static String main(String[] args,String curquery){

        Vector<String[]> arr_alias=simplifyjoin.main(null,curquery,"[\\w]+ AS+ ([a-zA-Z]+)"," "); 

        if(arr_alias.size()<3) {
            int pos=j;
            j++;
            return "CREATE TEMPORARY TABLE temp_table"+pos+" AS "+curquery+";\n";
        } 
        
        Map<String,String> map_alias=new HashMap<String,String>(); 
        Map<String,String> map_alias_rev=new HashMap<String,String>(); 
        Map<String,Vector<String[]>> map_select_col=new HashMap<String,Vector<String[]>>();
        String main_table_name=Main.getnextword(curquery, Main.find(curquery,"from",0)).getKey().split(" ")[0];
            
        for (String[] a : arr_alias) {
            map_alias.put(a[0],a[2]);
            map_alias_rev.put(a[2], a[0]);
            map_select_col.put(a[0],simplifyjoin.main(null,curquery.substring(0,Main.find(curquery,"from",0)),a[2]+"\\.+([\\w|\\*]+)"," "));

        }

        int join_word=Main.find(curquery, "join", 0);

        Vector<String[]> conditions=simplifyjoin.main(null,curquery,"[\\w]+.[\\w]+(\\s*)=(\\s*)[\\w]+.[\\w]+","=");
        

        for(String[] a:conditions){
            String cur_alias0=a[0].substring(0,a[0].indexOf('.')); 
            String cur_table0=map_alias_rev.get(cur_alias0.trim()); 
            String cur_alias1=a[1].substring(0,a[1].indexOf('.'));
            String cur_table1=map_alias_rev.get(cur_alias1.trim());
        }


        String join_query_part;
        Vector<String> single_join=new Vector<>();

        if(join_word!=-1) {
            join_query_part=curquery.substring(Main.find(curquery,main_table_name+" as "+map_alias.get(main_table_name),0));
            int start=0;
            for(String[] a:conditions){
                int end=Main.find(join_query_part,a[1],start); 
                single_join.add(join_query_part.substring(start,end));
                start=end+1;
            }
        } 
        else {
            for(String[] a:conditions){
                String b=a[0],c=a[1];
                if(Main.find(a[1],map_alias.get(main_table_name)+".",0)==-1){ b=a[1]; c=a[0];} 
                String cur_alias=b.substring(0,b.indexOf('.'));
                String cur_table=map_alias_rev.get(cur_alias);
                single_join.add("join "+cur_table+" as "+cur_alias+" on "+b+"="+c);
            }
        }
        

        Vector<String> split_query = new Vector<String>();
        Map<String,String> table= new HashMap<String,String>();

        for(String[] a: arr_alias){
            table.put(a[0],a[0]);
        }

        String result="";
        for(String join:single_join){ 
            String join_keywords = "Select ";

            String[] a = conditions.get(j);
            
            String[] split1 = a[0].split("\\.");

            String[] split2 = a[1].split("\\.");

            String table_alias1 = split1[0];
            String table_alias2 = split2[0];

            table_alias2=table_alias2.trim();
            String table_name1 =table.get(map_alias_rev.get(table_alias1)); 
            String table_name2 =table.get(map_alias_rev.get(table_alias2)); 

            for(String[] attr:map_select_col.get(table_name1)){ 
            join_keywords+=attr[0];
            join_keywords+=",";
            }

            for(String[] attr:map_select_col.get(table_name2)){ 
            join_keywords+=attr[0];
            join_keywords+=",";
            }
            join_keywords=join_keywords.substring(0,join_keywords.length()-1);
            join_keywords+=(" into temp_table"+j);
            join_keywords+=" from";
            join_keywords+=" ";

            if(join.contains(" "+table_name1+" ")){
                join_keywords+=table_name2;
                join_keywords+=" as ";
                join_keywords+=map_alias.get(table_name2);
                }else{
                join_keywords+=table_name1;
                join_keywords+=" as ";
                join_keywords+=map_alias.get(table_name1);
                }
                join_keywords+=" ";
                join = join.replace(a[0], map_alias.get(table.get(map_alias_rev.get(table_alias1)))+"."+split1[1]);
                join = join.replace(a[1], map_alias.get(table.get(map_alias_rev.get(table_alias2)))+"."+split2[1]);
                join_keywords+=(join+";\n");
                split_query.add(join_keywords);
                map_alias.put("temp_table"+j,"t"+j);
                result+=join_keywords;

                table.replace(table_name1,"temp_table"+j);
        
                table.replace(table_name2,"temp_table"+j);

                Vector<String[]> attribute1=new Vector<>();
                Vector<String[]> attribute=new Vector<>();
                for(String[] attr:map_select_col.get(table_name1)){ 
                    String[] split_old=attr[0].split("\\."); 
                    attr[0]= map_alias.get(table.get(table_name1))+"."+split_old[1];
                    attribute1.add(attr);
                    attribute.add(attr);
                }
                map_select_col.replace(table_name1,attribute1);

                Vector<String[]> attribute2=new Vector<>();
                for(String[] attr:map_select_col.get(table_name2)){ 
                    String[] split_old=attr[0].split("\\."); 
                    attr[0]= map_alias.get(table.get(table_name2))+"."+split_old[1]; 
                    attribute2.add(attr);
                    attribute.add(attr);
                }
                map_select_col.replace(table_name2,attribute2);
                map_select_col.put("temp_table"+j,attribute);
            j++;

        }

        return result+"\n";
    }
}
