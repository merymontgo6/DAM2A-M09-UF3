import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    
    private Hashtable<String, GestorClients> gestorClients;
    private boolean sortir;
    private ServerSocket serverSocket;

    public ServidorXat() {
        gestorClients = new Hashtable<>();
        sortir = false;
    }

    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
            
            while (!sortir) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());
                
                GestorClients gestor = new GestorClients(clientSocket, this);
                Thread fil = new Thread(gestor);
                fil.start();
            }
        } catch (IOException e) {
            if (!sortir) {
                System.err.println("Error al escoltar connexions: " + e.getMessage());
            }
        }
    }

    public void pararServidor() {
        sortir = true;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al tancar el servidor: " + e.getMessage());
        }
    }

    public void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        gestorClients.clear();
        System.out.println("Tancant tots els clients.");
        pararServidor();
    }

    public synchronized void afegirClient(GestorClients gestor) {
        gestorClients.put(gestor.getNom(), gestor);
        enviarMissatgeGrup("DEBUG: multicast Entra: " + gestor.getNom());
    }

    public synchronized void eliminarClient(String nom) {
        if (gestorClients.containsKey(nom)) {
            gestorClients.remove(nom);
            enviarMissatgeGrup("DEBUG: multicast Surt: " + nom);
        }
    }

    public synchronized void enviarMissatgeGrup(String missatge) {
        for (GestorClients gestor : gestorClients.values()) {
            gestor.enviarMissatge("Servidor", missatge);
        }
    }

    public synchronized void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        if (gestorClients.containsKey(destinatari)) {
            gestorClients.get(destinatari).enviarMissatge(remitent, missatge);
            System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
    }
}