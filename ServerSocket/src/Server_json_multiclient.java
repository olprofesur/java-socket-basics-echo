import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_json_multiclient {

    private ServerSocket providerSocket;


    public Server_json_multiclient() {
    }

    void run() {
        try {
            //1. creating a server socket
            providerSocket = new ServerSocket(9999, 10);
            //2. Wait for connection
            System.out.println("Server mjson ulticlient, waiting for connection");
            Socket connection = providerSocket.accept();
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());
            System.out.println("Starting a new thread for serving it");
            ClientManager cm=new ClientManager(connection);
            cm.start();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            //4: Closing connection
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    public static void main(String args[]) {
        Server_json server = new Server_json();
        while (true) {
            server.run();
        }
    }

}

class ClientManager extends Thread {
    Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String message;
    ObjectMapper mapper = new ObjectMapper();

    public ClientManager(Socket clientSocket) {
        this.clientSocket=clientSocket;
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

    protected void clientManage() {
        try {

            out = new PrintWriter(clientSocket.getOutputStream());
            out.flush();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Command objServer=new Command();
            objServer.setCommandName("connection confirm");
            sendMessage(mapper.writeValueAsString(objServer));
            Command objClient=null;
            //4. The two parts communicate via the input and output stream
            do {
                try {
                    String message=in.readLine();
                    objClient = mapper.readValue(message, Command.class);
                    System.out.println("client>" + objClient.getCommandName());
                    if (objClient.getCommandName().equals("bye")) {
                        sendMessage(mapper.writeValueAsString(objClient));
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
            } while (objClient!=null&&!objClient.getCommandName().equals("bye"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            //4: Closing connection
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void run() {
        clientManage();
    }


}
