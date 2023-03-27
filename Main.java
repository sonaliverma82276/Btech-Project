import javax.xml.parsers.DocumentBuilderFactory;
import javax.lang.model.util.ElementScanner6;
import javax.xml.parsers.DocumentBuilder; 
import org.w3c.dom.Document; 
import org.w3c.dom.NodeList; 
import java.io.File; 
import javafx.util.Pair;
import java.util.*;

public class Main { 

    public static int find(String query,String word,int start){
        query=query.toLowerCase();
        query.replaceAll("( )+", " ");
        word=word.toLowerCase();
        int pos=query.indexOf(word, start);
        if(pos==-1) return -1;

        return pos+word.length();
    }

    public static Pair<String,Integer> getnextword(String query,int pos){
        int n=query.length();
        String ans="";
        while(pos<n&&query.charAt(pos)==' ') pos++;
        int ok=0;
        while(pos<n){
            char a=query.charAt(pos);
            if(a==' '){
            ok++;
            }
            else if(a==',')
            {
                ans+=query.substring(pos, pos+1);
                return new Pair<String, Integer>(ans,pos);
            } else if(ok>3 && ans.contains(" as "))  
             return new Pair<String, Integer>(ans,pos);
             else if(ok>2&&!ans.contains(" as "))
             return new Pair<String, Integer>(ans,pos);
            ans+=query.substring(pos, pos+1);
            pos++;
        }
        return new Pair<String, Integer>(ans,pos);
    }

    public static int is_join(String query){
        if(find(query,"join",0)!=-1) return 1;
        
        //int n=query.length();

        Set<String> table_set = new HashSet<String>();
        int from_pos=find(query,"from",0);
        while(from_pos!=-1){
            Pair<String,Integer> tables=getnextword(query,from_pos);
            if(tables.getKey().indexOf(',')!=-1) return 1;

            table_set.add(tables.getKey());
            from_pos=find(query,"from",from_pos);
        }

        return table_set.size()>1?1:0;
    }

    public static void main(String argv[]) 
	{ 
		try { 
            mergeXML merge=new mergeXML();
            merge.main(null);
			File file = new File( "dbinstances.xml"); 
			DocumentBuilderFactory dbf 
				= DocumentBuilderFactory.newInstance(); 
			
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(file); 

			doc.getDocumentElement().normalize(); 

            NodeList list=doc.getElementsByTagName("select");

            System.out.println("List of Join queries : \n"); 
            int cur_cnt=0;
           for(int i=0;i<list.getLength();i++){
            String curquery=(list.item(i).getTextContent()).toLowerCase();
            if(is_join(curquery)==1) { 
                cur_cnt++;
                System.out.println(cur_cnt+")\n"+curquery+"\n-------------------------------------------");
                // String[] arr_alias=new String[5];
                Vector<String[]> arr_alias=simplifyjoin.main(null,curquery,"[\\w]+ AS+ ([a-zA-Z]+)"," "); 

                if(arr_alias.size()<3) {
                    System.out.println("No need to simplify query\n");
                } 
                else {
                    System.out.println("Simplified Query\n-------------------------------------------\n");
                
                    Map<String,String> map_alias=new HashMap<String,String>(); 
                    Map<String,String> map_alias_rev=new HashMap<String,String>(); 
                    Map<String,Vector<String[]>> map_select_col=new HashMap<String,Vector<String[]>>();
                    String main_table_name=getnextword(curquery, find(curquery,"from",0)).getKey().split(" ")[0];
                        
                    for (String[] a : arr_alias) {
                        map_alias.put(a[0],a[2]);
                        map_alias_rev.put(a[2], a[0]);
                        map_select_col.put(a[0],simplifyjoin.main(null,curquery.substring(0,find(curquery,"from",0)),a[2]+"\\.+([\\w|\\*]+)"," "));
                
                    }

                    int join_word=find(curquery, "join", 0);

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
                        join_query_part=curquery.substring(find(curquery,main_table_name+" as "+map_alias.get(main_table_name),0));
                        int start=0;
                        for(String[] a:conditions){
                            int end=find(join_query_part,a[1],start); 
                            single_join.add(join_query_part.substring(start,end));
                            start=end+1;
                        }
                    } 
                    else {
                        for(String[] a:conditions){
                            String b=a[0],c=a[1];
                            if(find(a[1],map_alias.get(main_table_name)+".",0)==-1){ b=a[1]; c=a[0];} 
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

                    int j=0;
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
                            join_keywords+=(join+";");
                            split_query.add(join_keywords);
                            map_alias.put("temp_table"+j,"t"+j);
                            System.out.println(join_keywords);

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

                    System.out.println("\n-------------------------------------------");
                }
            }


           }
		} 
		
		catch (Exception e) { 
			System.out.println(e); 
		} 
	} 
}