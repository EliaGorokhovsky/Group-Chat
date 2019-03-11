import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * A server program which accepts requests from clients to capitalize strings. When
 * a client connects, a new thread is started to handle it. Receiving client data,
 * capitalizing it, and sending the response back is all done on the thread, allowing
 * much greater throughput because more clients can be handled concurrently.
 */
public class CapitalizeServer {
    static String latest = "";
    static ArrayList<Capitalizer> list = new ArrayList<Capitalizer>();
    /**
     * Runs the server. When a client connects, the server spawns a new thread to do
     * the servicing and immediately returns to listening. The application limits the
     * number of threads via a thread pool (otherwise millions of clients could cause
     * the server to run out of resources by allocating too many threads).
     */
    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(59898)) {
            System.out.println("The capitalization server is running...");
            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                list.add(new Capitalizer(listener.accept()));
                pool.execute(list.get(list.size()-1));
                  //pool.execute(new Capitalizer(listener.accept()));
            }
        }
    }

    private static class Capitalizer implements Runnable {
        private Socket socket;
        Scanner in;
                PrintWriter out;

        Capitalizer(Socket socket) {
            this.socket = socket;
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e){
                System.out.println("Error");
            }
        }




        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                while (in.hasNextLine()) {
                    String input = in.nextLine();
                    //System.out.println(input.toUpperCase());
                    for(int i = 0; i < list.size(); i++){
                        list.get(i).out.println(input);
                    }
                    //latest = input;
                }
            } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }
}
