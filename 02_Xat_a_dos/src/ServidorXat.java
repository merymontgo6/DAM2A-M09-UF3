
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    private static ServerSocket serverSocket;

    public static void iniciarServidor() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (Exception e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    public static void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor aturat.");
            }
        } catch (Exception e) {
            System.out.println("Error al aturar el servidor: " + e.getMessage());
        }
    }
    
    public static String getNom(ObjectInputStream in) {
        try {
            String nom = (String) in.readObject();
            if (nom == null || nom.isEmpty()) {
                return "Client sense nom";
            } else {
                return nom;
            }
        } catch (Exception e) {
            System.out.println("Error al obtenir el nom: " + e.getMessage());
            return "Client sense nom";
        }
    }

    //metode main que crei la instancia servidorXat, iniciarServidor, accepti la conexio, crei els Streams, instancii FilServidorXat, inicii el fil, envii missatged que llegeix de la consola fins a la sortida, esperi al fill i tanqui el clientSocket
    public static void main(String[] args) {
        iniciarServidor();        
        try {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
            String nomClient = (String) in.readObject();
            System.out.println("Nom rebut: " + nomClient);

            // Instancia FilServidorXat amb un nom
            System.out.println("Fil de xat creat.");
            FilServidorXat filServidorXat = new FilServidorXat(nomClient, in);            
            // Inicia el fil
            System.out.println("Fil de " + nomClient + " iniciat");
            filServidorXat.start();
            // Envia missatges que llegeix de la consola fins a la sortida
            Scanner scanner = new Scanner(System.in);
            System.out.println("Missatge ('sortir' per tancar):");           
            
            String missatge;
            while (true) {
                missatge = scanner.nextLine();
                out.writeObject(missatge);
                out.flush();
                System.out.println("Missatge ('sortir' per tancar): Rebut: " + missatge);
                if (missatge.equalsIgnoreCase(MSG_SORTIR)) { break; }
            }
            System.out.println("Fil de xat finalitzat.");
            // Espera al fil
            filServidorXat.join();
            // Tanca el clientSocket i recursos
            scanner.close();
            in.close();
            out.close();
            clientSocket.close();
            System.out.println("sortir");
        } catch (Exception e) {
            System.out.println("Error al acceptar la conexio: " + e.getMessage());
        } finally {
            pararServidor();
        }
    }
}