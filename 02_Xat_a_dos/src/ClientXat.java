
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void enviarMissatge(String missatge) throws Exception {
        out.writeObject(missatge);
        out.flush();
    }

    public void tancarClient() throws Exception {
        out.close();
        in.close();
        socket.close();
    }

    public static void main(String[] args) {
        try {
            ClientXat client = new ClientXat();
            client.connect("localhost", 9999);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Escriu el teu nom:");
            String nom = scanner.nextLine();
            client.enviarMissatge(nom);

            FilLectorCX filLector = new FilLectorCX(client.out);
            filLector.start();
            System.out.println("Escriu els teus missatges (escriu 'sortir' per acabar):");
            
            String missatge;
            while (true) {
                missatge = scanner.nextLine();
                if (missatge.equalsIgnoreCase("sortir")) {
                    break;
                }
                client.enviarMissatge(missatge);
            }            
            scanner.close();
            client.tancarClient();
        } catch (Exception e) {
        }
    }
}