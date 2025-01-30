import java.io.*;
import java.net.*;

public class Server_string {

    private ServerSocket providerSocket;
    private Socket connection = null;
    private OutputStream out;
    private InputStream in;
    private String message;

    Server_string() {
    }

    void run() {
        try {
            //1. creating a server socket
            providerSocket = new ServerSocket(9999, 10);
            //2. Wait for connection
            System.out.println("Waiting for connection");
            connection = providerSocket.accept();
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());
            //3. get Input and Output streams
            out = connection.getOutputStream();
            out.flush();
            in = connection.getInputStream();
            sendMessage("Connection successful");
            //4. The two parts communicate via the input and output streams
            do {
                try {

					
					BufferedReader bis = new BufferedReader(new InputStreamReader(in));
					String message=bis.readLine();
                    System.out.println("client>" + message);
                    if (message.equals("bye")) {
                        sendMessage("bye");
                    }
                } catch (Exception classnot) {
                    System.err.println("Data received in unknown format");
                }
            } while (message!=null&&!message.equals("bye"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            //4: Closing connection
            try {
                in.close();
                out.close();
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    void sendMessage(String msg) {
        try {
            PrintWriter pw = new PrintWriter(out);
			pw.println(msg);
            pw.flush();
            System.out.println("server>" + msg);
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Server_string server = new Server_string();
        while (true) {
            server.run();
        }
    }
}
