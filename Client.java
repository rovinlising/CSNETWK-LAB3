import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Socket socket = null;
        Scanner keyboard = null;
        try {
            keyboard = new Scanner(System.in);
            System.out.print("Enter a number between 1 and 100: ");
            int clientNumber = keyboard.nextInt(); // Throws InputMismatchException if the input isn't an integer

            // Open a TCP connection to the server
            socket = new Socket(HOST, PORT);
            socket.setSoTimeout(10000); // Set a timeout of 10 seconds

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(CLIENT_NAME + "|" + clientNumber);

            // An out-of-range number shuts down the server
            if (clientNumber < 1 || clientNumber > 100) {
                System.out.println("Client number " + clientNumber + " is out of range (1-100). Closing connection.");
                return;
            }

            // If there is no reply from the server, it means the server has closed the connection unexpectedly
            String reply = in.readLine();
            if (reply == null) {
                throw new IOException("Server closed the connection unexpectedly.");
            }
            // Throws IllegalArgumentException if the reply format is invalid (no '|' separator)
            int sep = reply.lastIndexOf("|");
            if (sep < 0) {
                throw new IllegalArgumentException("Invalid reply format. Expected: 'name|number'.");
            }
            String serverName = reply.substring(0, sep);
            int serverNumber = Integer.parseInt(reply.substring(sep + 1).trim());

            int sum = clientNumber + serverNumber;
            System.out.println("Server Name: " + serverName + ", \nServer Number: " + serverNumber + ", \nClient Number: " + clientNumber + ", \nSum: " + sum);
        
            // Error Handling
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
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Error closing socket: " + e.getMessage());
                    }
                }
                if (keyboard != null) {
                    keyboard.close();
                }
            }
        }
    

    private static final String HOST = "localhost";
    private static final int PORT = 6000;
    private static final String CLIENT_NAME = "Client of Remiel Lising & David Maglente";
}


