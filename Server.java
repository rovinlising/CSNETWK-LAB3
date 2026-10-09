/*  
    SERVER - Remiel Lising & David Maglente
    CSNETWK - Lab 3 - S03
*/

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
            // Ask the operating system for a socket and bind it to the specified port
            serverSocket = new ServerSocket(PORT);
            System.out.println(SERVER_NAME + " is running on port " + PORT);

            // The server keeps accepting clients until an out-of-range number tells it to stop
            boolean running = true;
            while (running) {
                Socket clientSocket = null;
                try {
                    // accept() will give up after 10 seconds if no client connects
                    serverSocket.setSoTimeout(10000);
                    System.out.println("Waiting for a client to connect...");
                    clientSocket = serverSocket.accept(); // Blocks until a client connects
                    System.out.println("Accepted connection from "
                            + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                    // Reader to receive text sent by the client
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // Writer to send text back to the client 
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    // Read the client's single-line message
                    String message = in.readLine();
                    if (message == null) {
                        // readLine() returns null when the client closed its side without sending anything
                        System.out.println("Client disconnected without sending a message.");
                        continue; // Go back to waiting for the next client
                    }
                    System.out.println("Received: \"" + message + "\"");

                    // Split the message at the '|' separator into name and number
                    int sep = message.indexOf("|");
                    if (sep < 0) {
                        // No separator means the message is invalid
                        throw new IllegalArgumentException("Invalid message format. Expected: 'name|number'.");
                    }
                    String clientName = message.substring(0, sep);
                    // Throws NumberFormatException if the number part is not an integer
                    int clientNumber = Integer.parseInt(message.substring(sep + 1).trim());

                    // An out-of-range number is the signal to shut down the server
                    if (clientNumber < 1 || clientNumber > 100) {
                        System.out.println("Client number " + clientNumber
                                + " is out of range (1-100). Shutting down server.");
                        running = false; // Ends the while loop
                        continue;
                    }

                    // Valid request - compute the sum and display the details
                    int sum = clientNumber + SERVER_NUMBER;
                    System.out.println("Client Name: " + clientName
                            + ", \nClient Number: " + clientNumber
                            + ", \nServer Number: " + SERVER_NUMBER
                            + ", \nSum: " + sum);

                    // Send the server's name and number back to the client
                    String reply = SERVER_NAME + "|" + SERVER_NUMBER;
                    out.println(reply);
                    System.out.println("Sent reply: \"" + reply + "\"");

                // Error handling
                } catch (NumberFormatException e) {
                    System.out.println("Client sent a non-integer value.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Bad message format: " + e.getMessage());
                } catch (SocketTimeoutException e) {
                    // Thrown by accept() when no client connected within the timeout
                    System.out.println("Timed out waiting for a client. Still listening...");
                } catch (IOException e) {
                    System.out.println("I/O error: " + e.getMessage());
                } finally {
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                            System.out.println("Closed connection with client.");
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

    // Port the server listens on
    private static final int PORT = 6000;
    // Name sent to the client
    private static final String SERVER_NAME = "Server of Remiel Lising & David Maglente";
    // The server's number, added to the client's number
    private static final int SERVER_NUMBER = 50;
}
