import javax.xml.parsers.DocumentBuilderFactory; 
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
            String curquery=list.item(i).getTextContent();
            if(is_join(curquery)==1) { 
                cur_cnt++;
                System.out.println(cur_cnt+")\n"+curquery+"\n-------------------------------------------");
                // String[] arr_alias=new String[5];
                Vector<String[]> arr_alias=simplifyjoin.main(null,curquery,"[\\w]+ AS+ ([a-zA-Z]+)"); 
                Map<String,String> map_alias=new HashMap<String,String>();  
                Map<String,Vector<String[]>> map_select_col=new HashMap<String,Vector<String[]>>();
                for (String[] a : arr_alias) {
                    map_alias.put(a[0],a[2]); System.out.println(a[2]+" \n");
                    map_select_col.put(a[0],simplifyjoin.main(null,curquery.substring(0,find(curquery,"from",0)),a[2]+"\\.+([a-zA-Z]+)"));
                    for(String[] b:map_select_col.get(a[0])) System.out.println(b[0]+" "); System.out.println("\n");
                    for(String b:a) {
                        // System.out.println(b+" \n");
                    }
                }
                System.out.println("\n-------------------------------------------");
            }


           }
		} 
		
		catch (Exception e) { 
			System.out.println(e); 
		} 
	} 
}