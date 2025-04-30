
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

    public static void iniciarServidor() throws Exception {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public static void pararServidor() throws Exception {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Servidor aturat.");
        }
    }
    
    public static String getNom(ObjectInputStream in) throws Exception {
        String nom = (String) in.readObject();
        return (nom == null || nom.isEmpty()) ? "Client sense nom" : nom;
    }

    //metode main que crei la instancia servidorXat, iniciarServidor, accepti la conexio, crei els Streams, instancii FilServidorXat, inicii el fil, envii missatged que llegeix de la consola fins a la sortida, esperi al fill i tanqui el clientSocket
    public static void main(String[] args) {
        try {
            iniciarServidor();        
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
            String nomClient = getNom(in);
            System.out.println("Nom rebut: " + nomClient);

            FilServidorXat filServidorXat = new FilServidorXat(nomClient, in);
            System.out.println("Fil de xat creat.");
            System.out.println("Fil de " + nomClient + " iniciat");
            filServidorXat.start();
            
            Scanner scanner = new Scanner(System.in);
            System.out.println("Missatge ('sortir' per tancar):");
            
            String missatge;
            while (true) {
                missatge = scanner.nextLine();
                out.writeObject(missatge);
                out.flush();
                System.out.println("Missatge ('sortir' per tancar): Rebut: " + missatge);
                if (missatge.equalsIgnoreCase(MSG_SORTIR)) break;
            }
            
            System.out.println("Fil de xat finalitzat.");
            filServidorXat.join();
            scanner.close();
            in.close();
            out.close();
            clientSocket.close();
            System.out.println("sortir");
            
        } catch (Exception e) { System.out.println("Error: " + e.getMessage());
        } finally {
            try { pararServidor();
            } catch (Exception e) { System.out.println("Error al aturar servidor: " + e.getMessage()); }
        }
    }
}