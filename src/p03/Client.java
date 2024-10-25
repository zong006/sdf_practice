package p03;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException {
        
        // for simplicity sake, hardcoded in
        int port = 3000;
        String host = "localhost";
    
        Socket socket = new Socket(host, port);

        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);

        boolean sendingItem = false;
        String lineRead = "";
        Item item = null;
        int itemCounter = 0;
        boolean stopReadingItems = false;

        Map<String, String> fromServer = new HashMap<>();
        List<Item> items = new ArrayList<>();

        while ((lineRead = br.readLine())!=null && !stopReadingItems){
            
            if (lineRead.contains("request_id")){
                fromServer.put(lineRead.split(":")[0].trim(), lineRead.split(":")[1].trim());
            }
            else if (lineRead.contains("budget")){
                fromServer.put(lineRead.split(":")[0].trim(), lineRead.split(":")[1].trim());
            }
            else if (lineRead.contains("item_count")){
                fromServer.put(lineRead.split(":")[0].trim(), lineRead.split(":")[1].trim());
            }            
            else if (lineRead.contains("prod_start")){
                // start reading item from prod_start until prod_end comes
                sendingItem = true;
                item = new Item();
            }
            else if (lineRead.contains("prod_end")){
                // start reading item from prod_start until prod_end comes
                sendingItem = false;
                if (item != null){
                    items.add(item);
                }
                itemCounter += 1;
            }
            if (sendingItem){
                if (lineRead.contains("prod_id")){
                    item.setProd_id(lineRead.split(":")[1].trim());    
                }
                else if (lineRead.contains("title")){
                    item.setTitle(lineRead.split(":")[1].trim());    
                }
                else if (lineRead.contains("price")){
                    float price = Float.parseFloat(lineRead.split(":")[1].trim());
                    item.setPrice(price);
                }
                else if (lineRead.contains("rating")){
                    int rating = Integer.parseInt(lineRead.split(":")[1].trim());
                    item.setRating(rating);
                }
                else {continue;}
            }
            if (fromServer.containsKey("item_count") && itemCounter==Integer.parseInt(fromServer.get("item_count"))){
                stopReadingItems = true;
            }
        }

        sortItems(items);
        List<Item> selectedItems = greedy(items, Float.parseFloat(fromServer.get("budget")));

        // send it back to the server
        String selectedItemID = "";
        float amtSpent = 0;
    
        for (Item it : selectedItems){
            selectedItemID += it.getProd_id() + ",";
            amtSpent += it.getPrice();        
        }

        selectedItemID = selectedItemID.substring(0, selectedItemID.length()-1); //to remove the last comma
        float remAmount = Float.parseFloat(fromServer.get("budget")) - amtSpent;

        bw.write("request_id: " + fromServer.get("request_id") + "\n");
        bw.write("name: some name" + "\n");
        bw.write("email: sample@email.com" + "\n");
        bw.write("items: " + selectedItemID + "\n");
        bw.write("spent: " + amtSpent + "\n");
        bw.write("remaining: " + remAmount + "\n");
        bw.write("client_end" + "\n");
        bw.flush();
    
        while ((lineRead=br.readLine())!=null) {
            if (lineRead.contains("success") || lineRead.contains("failed")){
                System.out.println(lineRead);
                break;
            }
        }   

        bw.close();
        osw.close();
        os.close();
        br.close();
        isr.close();
        is.close();
        socket.close();
    }

    public static void sortItems(List<Item> items){
        Comparator<Item> compareTwo = Comparator.comparing(Item::getRating, Comparator.reverseOrder()).thenComparing(Item::getPrice, Comparator.reverseOrder());
        items.sort(compareTwo);
    }

    public static List<Item> greedy(List<Item> items, float budget){
        List<Item> selectedItems = new ArrayList<>();
        float remBudget = budget;
        for (Item item : items){
            if (item.getPrice() > remBudget){
                continue;
            }
            else {
                selectedItems.add(item);
                remBudget -= item.getPrice();
            }
        }
        return selectedItems;
    }
}
