package p01;

import java.io.*;
import java.util.*;

class Main {

    public static void main(String[] args) throws IOException {
        
        String csvFile = args[0];
        String templateFile = args[1];

        String fileDir = System.getProperty("user.dir");
        File fc = new File(fileDir + File.separator + "src" + File.separator + "p01" + File.separator + "details.csv");
        
        FileReader fr = new FileReader(fc);
        BufferedReader br = new BufferedReader(fr);
        String lineRead = "";
        ArrayList<String[]> details = new ArrayList<>();

        while ((lineRead = br.readLine())!=null){
            if (lineRead.contains("first_name") && lineRead.contains("last_name") && lineRead.contains("address")) continue;
            else {
                String[] items = lineRead.split(",");
                details.add(items);    
            }
        }

        File ft = new File(fileDir + File.separator + "src" + File.separator + "p01" + File.separator + "template.txt");
        FileReader fr2 = new FileReader(ft);
        BufferedReader br2 = new BufferedReader(fr2);
        ArrayList<String> template = new ArrayList<>();

        while ((lineRead = br2.readLine())!=null){
            template.add(lineRead);   
        }
        
        for (String[] detail : details){
            // first_name,last_name,address,years
            
            for (String s : template){
                String toPrint = "";
                
                if (s.contains("__address__")){
                    // note: in csv file, \n is a literal string. \\n refers to the literal string \n since \<anything> refers to <anything>
                    // so first replace it with the newline character \n then split it accordingly
                    String add = detail[2].replace("\\n", "\n");
                    String[] address = add.split("\\n");
                    
                    toPrint = s.replaceAll("__address__", address[0] + "\n" + address[1]);
                } 
                
                else if (s.contains("__first_name__")) toPrint = s.replaceAll("__first_name__", detail[0]);

                else if (s.contains("__years__.")) toPrint = s.replaceAll("__years__.", detail[3]);

                else toPrint=s;
                System.out.println(toPrint);
            }
            System.out.println();
        }        
    }

}