import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class exchange {

    public static BlockingQueue queue = null;

    public static int totalNumberOfExchanges = 0;
    public static int totalNumberOfReplies = 0;

    public static void main(String[] args) throws InterruptedException {

        long startTime = System.currentTimeMillis();
        long currentTime = startTime;

        BufferedReader br = null;
        String line = null;
        String match_key = null;
        String match_value = null;

        try {
            br = new BufferedReader(new FileReader("calls.txt"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Map<String, List<String>> tuples = new HashMap<>();

        tuples = new HashMap<String, List<String>>();
        try {
            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\[");
                String key = parts[0];
                String value = parts[1];
                match_key = key.substring(key.indexOf("{") + 1, key.indexOf(","));
                match_value = value.substring(value.indexOf(" ") + 1, value.indexOf("}."));
                String[] matchValueAry = match_value.split("\\]");
                match_value = matchValueAry[0];
                String[] list = match_value.split(",");
                int numberOfExchangesForCaller = list.length;
                totalNumberOfExchanges += numberOfExchangesForCaller * 2;
                tuples.put(match_key, Arrays.asList(list));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(" ** Calls to be made ** ");
        for (Map.Entry<String,List<String>> entry : tuples.entrySet())
            System.out.println(entry.getKey()+ ": " + entry.getValue());
        System.out.println();

        queue = new ArrayBlockingQueue(totalNumberOfExchanges);
        int totalNoOfFriendThreads = tuples.size();
        int totalNumberOfFriendThreadsFinished = 0;

        List<Calling> listOfCallings = new ArrayList<>();

        for (Map.Entry<String, List<String>> friendThread : tuples.entrySet()) {
            listOfCallings.add(new Calling(friendThread.getKey(), friendThread.getValue()));
        }

        for (Calling calling : listOfCallings) {
            calling.start();
        }


            while (!(totalNumberOfExchanges == totalNumberOfReplies)) {

                //Keep listening on the queue until the queue is empty
                String introMessage = (String) queue.take();

                startTime = System.currentTimeMillis();
                String[] messageAry = introMessage.split(":");

                String typeOfMsg = messageAry[0];
                String sender = messageAry[1];
                String receiver = messageAry[2];
                String messageTime = messageAry[3];
                totalNumberOfReplies++;
                if (typeOfMsg.equals("I")) {

                    //This is an intro message
                    System.out.println(receiver + " received intro message from " + sender + " [" + messageTime + "]");

                    Thread.sleep((long)(Math.random() * 100));

                } else if (typeOfMsg.equals("R")) {

                    //This is a reply
                    System.out.println(receiver + " received reply message from " + sender + " [" + messageTime + "]");

                }

            }

        Thread.sleep(6000);
        System.out.println("Master has received no replies for 1.5 seconds, ending...");
    }
}