import java.util.*;

public class split {
    public static int j=0;
    public static String main(String[] args,String curquery){

        //Vector<String[]> arr_alias=simplifyjoin.main(null,curquery,"[\\w]+ AS+ ([a-zA-Z]+)"," "); 
        
        Map<String,String> map_alias=new HashMap<String,String>(); 
        Map<String,String> map_alias_rev=new HashMap<String,String>(); 
        Map<String,Vector<String[]>> map_select_col=new HashMap<String,Vector<String[]>>();
        String main_table_name=Main.getnextword(curquery, Main.find(curquery,"from",0)).getKey().split(" ")[0];
        
        int join_word=Main.find(curquery, "join", 0);

        int where_pos=Main.find(curquery, "where", 0);
        if(where_pos==-1) where_pos=curquery.length();

        int from_pos = Main.find(curquery,"from",0);
        Vector<String[]> conditions;
        Vector<String[]> attributes = simplifyjoin.main(null,curquery.substring(0,from_pos),"[\\w]+(\\.)[\\w]+"," ");
        if(join_word!=-1)
        conditions=simplifyjoin.main(null,curquery.substring(0, where_pos),"[\\w]+.[\\w]+(\\s*)=(\\s*)[\\w]+.[\\w]+","=");
        else
        conditions=simplifyjoin.main(null,curquery.substring(0, curquery.length()),"[\\w]+.[\\w]+(\\s*)=(\\s*)[\\w]+.[\\w]+","=");
        Vector<String> tables= new Vector<>();
        
        for(String[] c:conditions){

            String[] s1 = c[0].split("\\.");
            String[] s2 = c[1].split("\\.");
            s2[0]=s2[0].trim();
            if(!tables.contains(s1[0])){
                tables.add(s1[0]);
                }
            if(!tables.contains(s2[0])){
                tables.add(s2[0]);
                }
        }
        for(String[] s:attributes){
            String[] split = s[0].split("\\.");
            if(!tables.contains(split[0])){
            tables.add(split[0]);
            }
        }
       
        /* for(String s:tables){
            System.out.println(s+"\n");
        }*/

        if(tables.size()<3) {
            int pos=j;
            j++;
            return "CREATE TEMPORARY TABLE temp_table"+pos+" AS "+curquery+";\n";
        } 

        Boolean has_alias = false;
        //System.out.println(main_table_name);
        if(!tables.contains(main_table_name)){
             has_alias=true;
        }
        Map<String,String> table= new HashMap<String,String>();
       
        //System.out.println(from_pos+" ");

        
        if(has_alias){
        for (String a : tables) {
            String keyword= " "+a+" ";
            int alias_pos = Main.find(curquery,keyword,0);
             if(alias_pos==-1)
             {
                keyword=" "+a+",";
                alias_pos = Main.find(curquery,keyword,0);
             }
            alias_pos-=keyword.length();
            //System.out.println(alias_pos);      
            int start_pos=-1;
            int end_pos=alias_pos-2;
            while(curquery.charAt(end_pos)!=' '){
                end_pos--;
            }
            start_pos=end_pos;
            String table_name = curquery.substring(start_pos+1,alias_pos);
            table_name=table_name.trim();
            alias_pos=end_pos;
            end_pos--;
            String temp = "as";

            if(table_name.equals(temp)){
                while(curquery.charAt(end_pos)!=' '){
                    end_pos--;
                }
                start_pos=end_pos;
                table_name = curquery.substring(start_pos+1,alias_pos);
            }

            table_name=table_name.trim();
            table.put(table_name,table_name);
            map_alias.put(table_name,a);
            map_alias_rev.put(a, table_name);
            map_select_col.put(table_name,simplifyjoin.main(null,curquery.substring(0,Main.find(curquery,"from",0)),a+"\\.+([\\w|\\*]+)"," "));
        }
    }
    else{
        for(String a:tables){
        table.put(a,a);
        map_alias.put(a,a);
        map_alias_rev.put(a,a);
        map_select_col.put(a,simplifyjoin.main(null,curquery.substring(0,Main.find(curquery,"from",0)),a+"\\.+([\\w|\\*]+)"," "));
        }
    }  

        String join_query_part;

        Vector<String> single_join=new Vector<>();
        if(join_word!=-1) {

            if(has_alias){
            join_query_part=curquery.substring(Main.find(curquery," "+map_alias.get(main_table_name)+" ",0));
            }
            else
            join_query_part=curquery.substring(Main.find(curquery," "+main_table_name+" ",0));
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
                if(has_alias)
                single_join.add("join "+cur_table+" as "+cur_alias+" on "+b+"="+c);
                else
                single_join.add("join "+cur_table+" on "+b+"="+c);
            }
        }


        Vector<String> split_query = new Vector<String>();

        String result="";
        for(String join:single_join){ 
            String join_keywords = "Select ";

            String[] c = conditions.get(j);
            String first=c[0];
            String second=c[1];
            //System.out.println(c[0]+" "+c[1]+" c condition\n");
            String[] s1 = c[0].split("\\.");
            String[] s2 = c[1].split("\\.");
            //System.out.println(s1[0]+" "+s2[0]); 
            String t1 = s1[0];
            String t2 = s2[0];

            t2=t2.trim(); t1=t1.trim();            
            String[] b=conditions.get(j);
            //System.out.println(map_alias_rev.get(t1));

            // System.out.println(table.get(map_alias_rev.get(t1))+" "+map_alias.get(table.get(map_alias_rev.get(t1)))+" "+s1[1]);
            String cur=map_alias.get(table.get(map_alias_rev.get(t1)));
            while(!(cur+"."+s1[1]).equals(b[0])) {
                b[0]=cur+"."+s1[1];
                cur=map_alias.get(table.get(map_alias_rev.get(cur)));
            }
            cur=map_alias.get(table.get(map_alias_rev.get(t2)));
            while(!(cur+"."+s2[1]).equals(b[1])) {
                b[1]=cur+"."+s2[1];
                cur=map_alias.get(table.get(map_alias_rev.get(cur)));
            }
              //  System.out.println(b[0]+" "+b[1]+" that\n");
                conditions.set(j,b);
                // System.out.println(conditions.get(j)[0]+" "+conditions.get(j)[1]+" that2\n");

                String[] a = conditions.get(j);
                String[] split1 = a[0].split("\\.");
    
                String[] split2 = a[1].split("\\.");
    
                String table_alias1 = split1[0];
                String table_alias2 = split2[0];
                table_alias2=table_alias2.trim();

            String table_name1 =table.get(map_alias_rev.get(table_alias1)); 
            String table_name2 =table.get(map_alias_rev.get(table_alias2)); 
            // System.out.println(table_name1+" "+table_name2+" "+ table_alias1);

            // System.out.println(table_name1);

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
                if(has_alias){
                join_keywords+=table_name2;
                join_keywords+=" as ";
                join_keywords+=map_alias.get(table_name2);
                }else{
                join_keywords+=table_name2;  
                }
                }else{
                if(has_alias){
                join_keywords+=table_name1;
                join_keywords+=" as ";
                join_keywords+=map_alias.get(table_name1);
                }
                else{
                    join_keywords+=table_name1;  
                }
                }
                join_keywords+=" ";
                //System.out.println(map_alias.get(table.get(map_alias_rev.get(table_alias1)))+" "+first+" here print\n\n");
                join = join.replace(first, map_alias.get(table.get(map_alias_rev.get(table_alias1)))+"."+split1[1]);
                join = join.replace(second, map_alias.get(table.get(map_alias_rev.get(table_alias2)))+"."+split2[1]);
                join_keywords+=(join+";\n\n");
                split_query.add(join_keywords);
                map_alias.put("temp_table"+j,"t"+j);
                map_alias_rev.put("t"+j,"temp_table"+j);
                result+=join_keywords;

                table.put(table_name1,"temp_table"+j);
                table.put(table_name2,"temp_table"+j);
                table.put("temp_table"+j,"temp_table"+j);

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

        // add rest of the query
        if(where_pos!=curquery.length() && join_word!=-1) {
            result+= "Select temp_table"+j+".* from temp_table"+j+" where "+curquery.substring(where_pos,curquery.length());
            Vector<String[]> where_conditions=simplifyjoin.main(null,curquery.substring(where_pos, curquery.length()),"[\\w]+.[\\w]+(\\s*)=(\\s*)[\\w]+.[\\w]+","=");
            for(String[] a:where_conditions){
                String[] split1 = a[0].split("\\.");
                String[] split2 = a[1].split("\\.");
                String table_alias1 = split1[0];
                String table_alias2 = split2[0];

                table_alias2=table_alias2.trim();
                String table_name1 =map_alias.get(table.get(map_alias_rev.get(table_alias1))); 
                String table_name2 =map_alias.get(table.get(map_alias_rev.get(table_alias2))); 
                String cur=map_alias.get(table.get(map_alias_rev.get(table_alias1)));
                while(!(cur).equals(table_name1)) {
                    table_name1=cur;
                    cur=map_alias.get(table.get(map_alias_rev.get(cur)));
                }
                cur=map_alias.get(table.get(map_alias_rev.get(table_alias2)));
                while(!(cur).equals(table_name2)) {
                    table_name2=cur;
                    cur=map_alias.get(table.get(map_alias_rev.get(cur)));
                }
                result = result.replace(a[0], table_name1+"."+split1[1]);
                result = result.replace(a[1], table_name2+"."+split2[1]);
               
            }
        }
        return result+"\n";

    }
}
