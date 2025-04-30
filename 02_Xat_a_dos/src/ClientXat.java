
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
        System.out.println("Client connectat a " + host + ":" + port);
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) throws Exception {
        out.writeObject(missatge);
        out.flush();
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() throws Exception {
        out.close();
        in.close();
        socket.close();
        System.out.println("Client tancat.");
    }

    public static void main(String[] args) {
        try {
            ClientXat client = new ClientXat();
            client.connect("localhost", 9999);
            new Thread(() -> {
                try {
                    while (true) {
                        String message = (String) client.in.readObject();
                        System.out.println("Rebut: " + message);
                        if (message.equalsIgnoreCase("sortir")) {
                            System.out.println("El servidor ha tancat la connexió");
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error en rebre missatges: " + e.getMessage());
                }
            }).start();

            Scanner scanner = new Scanner(System.in);
            System.out.println("Escriu el teu nom:");
            String nom = scanner.nextLine();
            client.enviarMissatge(nom);

            System.out.println("Missatge ('sortir' per tancar):");
            String missatge;
            while (true) {
                missatge = scanner.nextLine();
                client.enviarMissatge(missatge);
                if (missatge.equalsIgnoreCase("sortir")) break;
            }
            
            scanner.close();
            client.tancarClient();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}