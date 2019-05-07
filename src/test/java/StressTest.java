import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

public class StressTest {

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    // First set up a few threads that execute continuously easy requests to
    // the server. This will trick the load balancer into keeping the instance alive.

    startRequests(0);
    startRequests(0);
    startRequests(0);
    startRequests(0);

    // Now a mix of a hard and very hard requests
    startRequests(5);
    startRequests(10);
    startRequests(15);
    startRequests(30);
  }

  private static Thread startRequests(int size) {
    Thread thread = new Thread(() -> {
      while (true) {
        try {
          fetch(size);
        } catch (IOException e) {
          return;
        }
      }
    });
    thread.start();
    return thread;
  }

  private static boolean fetch(int count) throws IOException {
    URL url = new URL("https://activityinfo-gae-oom-bug.appspot.com/fetch?count=" + count);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    int status = connection.getResponseCode();
    String instance = connection.getHeaderField("X-Instance");
    System.out.printf("%75s FETCH[%3d]: %d%n", instance, count, status);

    return status == 200;
  }
}
