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

    public static Pair<Integer,Integer> solve_parenthesis(String s){

        Stack<Integer> stack = new Stack<>();
        int t=0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                int cur=i;
                while(s.charAt(cur+1)==' ') cur++;
                if(!s.substring(cur+1,cur+8).equals("select ")) {
                    t++;
                    continue;
                }
                stack.push(i);
            } else if (s.charAt(i) == ')') {
                if(t>0) {
                    t--; continue;
                }
                return new Pair<Integer,Integer>(stack.pop(),i);
            }
        }
        return null;
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
                split.j=0;
                cur_cnt++;
                System.out.println(cur_cnt+")\n"+curquery+"\n-------------------------------------------\n Simplified Query\n-------------------------------------------\n");
                
                Pair<Integer,Integer> paren_pos=solve_parenthesis(curquery);
                String result="";
                while(paren_pos!=null) {
                    String subquery=curquery.substring(paren_pos.getKey()+1,paren_pos.getValue());
                    result+=split.main(null, subquery)+"\n";
                    int pos=split.j-1;
                    curquery=curquery.replace("("+subquery+")", "temp_table"+pos);
                    paren_pos=solve_parenthesis(curquery);
                }
                result+=split.main(null, curquery);
                System.out.println(result+"-------------------------------------------");
               
            }


           }
		} 
		
		catch (Exception e) { 
			System.out.println(e); 
		} 
	} 
}