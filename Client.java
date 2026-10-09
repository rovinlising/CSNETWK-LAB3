/*  
    CLIENT - Remiel Lising & David Maglente
    CSNETWK - Lab 3 - S03
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Socket socket = null;
        Scanner keyboard = null;
        try {
            // Read the client's number from the keyboard
            keyboard = new Scanner(System.in);
            System.out.print("Enter a number between 1 and 100: ");
            int clientNumber = keyboard.nextInt(); // Throws InputMismatchException if the input isn't an integer

            // Open a TCP connection to the server
            socket = new Socket(HOST, PORT);
            socket.setSoTimeout(10000); // Wait at most 10 seconds for the server's reply
            System.out.println("Connected to server at " + HOST + ":" + PORT);

            // Writer to send text to the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // Reader to receive text from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the client's name and number, separated by '|'
            String message = CLIENT_NAME + "|" + clientNumber;
            out.println(message);
            System.out.println("Sent: \"" + message + "\"");

            // An out-of-range number was still sent (the server uses it as a shutdown signal), so the client stops here
            if (clientNumber < 1 || clientNumber > 100) {
                System.out.println("Client number " + clientNumber
                        + " is out of range (1-100). Message was sent to the server; closing connection.");
                return;
            }

            // Wait for the server's reply (throws SocketTimeoutException after 10 seconds)
            System.out.println("Waiting for server reply...");
            String reply = in.readLine();
            if (reply == null) {
                // readLine() returns null if the server closed the connection without replying
                throw new IOException("Server closed the connection unexpectedly.");
            }
            System.out.println("Received: \"" + reply + "\"");

            // Split the reply at the last '|' into the server's name and number
            int sep = reply.lastIndexOf("|");
            if (sep < 0) {
                throw new IllegalArgumentException("Invalid reply format. Expected: 'name|number'.");
            }
            String serverName = reply.substring(0, sep);
            // Throws NumberFormatException if the number part is not an integer
            int serverNumber = Integer.parseInt(reply.substring(sep + 1).trim());

            // Compute and display the result
            int sum = clientNumber + serverNumber;
            System.out.println("Server Name: " + serverName
                    + ", \nServer Number: " + serverNumber
                    + ", \nClient Number: " + clientNumber
                    + ", \nSum: " + sum);

        // Error handling - each error prints a message
        } catch (InputMismatchException e) {
            System.out.println("Input was not an integer. Please enter a whole number between 1 and 100.");
        } catch (NumberFormatException e) {
            System.out.println("Server sent a non-integer value.");
        } catch (IllegalArgumentException e) {
            System.out.println("Bad reply format: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + e.getMessage());
        } catch (ConnectException e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        } catch (SocketTimeoutException e) {
            System.out.println("Server timed out");
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        } finally {
            // Closes the Socket
            if (socket != null) {
                try {
                    socket.close();
                    System.out.println("Connection closed.");
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e.getMessage());
                }
            }
            // Closes the keyboard scanner
            if (keyboard != null) {
                keyboard.close();
            }
        }
    }

    // Address and port of the server
    private static final String HOST = "localhost";
    private static final int PORT = 6000;
    // Name sent to the server with every message
    private static final String CLIENT_NAME = "Client of Remiel Lising & David Maglente";
}
