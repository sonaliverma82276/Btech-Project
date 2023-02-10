import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.DocumentBuilder; 
import org.w3c.dom.Document; 
import org.w3c.dom.NodeList; 
import java.io.File; 

public class prev_code { 

    public static int find(String query,String word){
        int n=query.length();
        String cur="";
        for(int i=0;i<n;i++){
            if(cur.equalsIgnoreCase(word))  {
                return i;
            }
            if(query.charAt(i)==' ') {
                cur="";
            }
            else cur+=query.charAt(i);
        }
        return -1;
    }
    public static int is_join(String query){
        if(find(query,"join")!=-1) return 1;
        if(find(query,"union")!=-1) return 0;
        
        int n=query.length();
        int from_pos=find(query,"from");
        
        if(find(query.substring(from_pos, n),"from")!=-1) return 1; //maybe a subquery (nested query)

        while(from_pos<n&&query.charAt(from_pos)==' ') {
            from_pos++;
        }

        int ok=0;
        for(int i=from_pos;i<n;i++){
            while(i<n&&query.charAt(i)==' ') {
                i++;
                ok=1;
            }
            if(i==n) break;
            if(ok==1) {
                if(query.charAt(i)==',')  return 1;
                else return 0;
            }
            if(query.charAt(i)==',')  return 1;
        }
        return 0;
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
            }


           }
		} 
		
		catch (Exception e) { 
			System.out.println(e); 
		} 
	} 
}