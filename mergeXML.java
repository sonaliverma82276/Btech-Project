import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.DocumentBuilder; 
import org.w3c.dom.Document; 
import org.w3c.dom.NodeList; 
import org.w3c.dom.Node;  
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.*;

public class mergeXML{

    public static Document doc = null;

    static void dbinstance(Document docx,NodeList header,String word)
    {
        NodeList selectlist=docx.getElementsByTagName(word);
        for(int i=0;i<selectlist.getLength();i++) {
            Node curselect = doc.importNode(selectlist.item(i), true);
            header.item(0).appendChild(curselect);
        }
    }

    static void subdir (File[]arr, int level,DocumentBuilder db)
    {
    for (File f:arr)
        {
                if (f.isFile ())
                {
                    try {
                        String filename=f.getName();
                        int len=filename.length();
                        if(filename.charAt(len-3)=='x'&&filename.charAt(len-2)=='m'&&filename.charAt(len-1)=='l'){
                        
                            Document doc2=db.parse(f);
                            NodeList header=doc.getElementsByTagName("mapper");
                            dbinstance(doc2,header,"select");
                            dbinstance(doc2,header,"insert");
                            dbinstance(doc2,header,"update");
                            dbinstance(doc2,header,"delete");

                        }
                    }
                    catch(Exception e) {
                        System.out.println(e);
                    }
                }

                else if (f.isDirectory ())
                {
                    subdir(f.listFiles (), level + 1,db);
                }
        }
    }


    public static void main(String argv[]) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        
        try {
            String maindirpath = "D:\\Downloads\\Projects\\descrypto\\descrypto-main";

      File maindir = new File (maindirpath);
      
      if (maindir.exists () && maindir.isDirectory ())
        {

        File arr[] = maindir.listFiles (); 
        System.out.println ("Files from main directory : " + maindir);
        db = dbf.newDocumentBuilder();
        doc=db.parse(new File("D:\\Btech Project\\dbinstances.xml"));
        subdir(arr, 0,db);
        }

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result); 

            Writer output = new BufferedWriter(new FileWriter("D:\\Btech Project\\dbinstances.xml"));
            String xmlOutput = result.getWriter().toString();  
            output.write(xmlOutput);
            output.close();

        } catch (Exception e) {
            e.printStackTrace();
        } 

    }


}