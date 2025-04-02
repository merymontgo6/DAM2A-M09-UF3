import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final int port = 7777;
    public static final String host = "localhost";
    private Socket clientSocket;
    private PrintWriter out;

    public void conecta() {
        try {
            clientSocket = new Socket(host, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("Connectat a servidor en localhost:" + port);
        } catch (Exception e) { }
    }
    public void tanca() {
        try {
            out.close();
            clientSocket.close();
            System.out.println("Client tancat");
        } catch (Exception e) {
        }
    }

    public void envia (String missatge) {
        out.println(missatge);
        System.out.println("Enviat al servidor: " + missatge);
    }

    public static void main(String[] args) {
        Client client  = new Client();
        client.conecta();
        client.envia("Prova d'enviament 1");
        client.envia("Prova d'enviament 2");
        client.envia("Adeu!");
        System.out.println("Prem Enter per tancar el client...");
        new Scanner(System.in).nextLine();
        client.tanca();
    }
}
