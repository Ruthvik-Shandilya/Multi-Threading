
import java.util.List;

public class Calling extends Thread {

    String callerName;
    List<String> callees;

    public Calling(String callerName, List<String> friends) {

        this.callerName = callerName;
        this.callees = friends;
    }

    @Override
    public void run() {

        sendIntroMessage();
        try {
            Thread.currentThread().join(5500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Process " + callerName + " has received no calls for 1 second, ending...");
    }

    public void sendIntroMessage() {

        //Add intro message to queue
        try {
            Thread.sleep((long)(Math.random() * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(String callee : callees) {

            String time = "" + System.currentTimeMillis();

            synchronized (exchange.queue) {
                exchange.queue.add("I" + ":" + callerName + ":" + callee + ":" + time);
            }

            try {
                Thread.sleep((long)(Math.random() * 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.sendReply("R" + ":" + callee + ":" + callerName + ":" + time);
        }
    }

    public void sendReply(String replyMessage) {

        try {
            Thread.sleep((long)(Math.random() * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (exchange.queue) {
            exchange.queue.add(replyMessage);
        }
    }

}
