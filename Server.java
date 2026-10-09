import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT); //Asks the operating system for a socket and binds it to the specified port
            System.out.println(SERVER_NAME + " is running on port " + PORT);

            boolean running = true;
            while (running) {
                Socket clientSocket = null;
                try {
                    serverSocket.setSoTimeout(10000); // Set a timeout of 10 seconds
                    clientSocket = serverSocket.accept();

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Create a reader to receive text (lines) sent by the client
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // Create a writer to send text back to the client (true = auto-flush)

                    String message = in.readLine();
                    if (message == null) {
                        System.out.println("Client disconnected."); //if there is no message, the client has disconnected
                        continue; // Continue to the next iteration of the loop
                    }
                    int sep = message.indexOf("|");
                    if (sep < 0) {
                        throw new IllegalArgumentException("Invalid message format. Expected: 'number|name'."); //If no '|' separator is found, an error occurs
                    }
                    String clientName = message.substring(0, sep);
                    int clientNumber = Integer.parseInt(message.substring(sep + 1).trim());

                    // An out-of-range number shuts down the server 
                    if (clientNumber < 1 || clientNumber > 100) {
                        System.out.println("Client number " + clientNumber + " is out of range (1-100). Closing connection.");
                        running = false;
                        continue;
                    }

                    int sum = clientNumber + SERVER_NUMBER;
                    System.out.println("Client Name: " + clientName + ", \nClient Number: " + clientNumber + ", \nServer Number: " + SERVER_NUMBER + ", \nSum: " + sum);

                    out.println(SERVER_NAME + "|" + SERVER_NUMBER);

                } catch (NumberFormatException e) {
                    System.out.println("Client sent a non-integer value.");

                } catch (IllegalArgumentException e) {
                    System.out.println("Bad message format: " + e.getMessage());
                } catch (SocketTimeoutException e) {
                    System.out.println("Client timed out");
                } catch (IOException e) {
                    System.out.println("I/O error");
                } finally {
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            System.out.println("Error closing client socket: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Server socket error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    System.out.println("Server socket closed.");
                } catch (IOException e) {
                    System.out.println("Error closing server socket: " + e.getMessage());
                }
            }
        }
    }

    private static final int PORT = 6000;
    private static final String SERVER_NAME = "Server - Remiel Orvin Lising & David Kyle Maglente";
    private static final int SERVER_NUMBER = 50;
}
