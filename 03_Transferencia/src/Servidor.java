import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static ServerSocket serverSocket;
    private Socket clientSocket;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");
        clientSocket = serverSocket.accept();
        System.out.println("Connexio acceptada: " + clientSocket.getInetAddress());
        return clientSocket;
    }

    public void tancarConnexio(Socket socket) throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Tancant connexió amb el client: " + socket.getInetAddress());
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public void enviarFitxers(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

        while (true) { 
            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) input.readObject();
            System.out.println("Nomfitxer rebut: " + nomFitxer);

            if (nomFitxer == null || nomFitxer.isEmpty()) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                return;
            }

            Fitxer fitxer = new Fitxer(nomFitxer);
            byte[] contingut = fitxer.getContingut();

            if (contingut == null) {
                System.out.println("nulo xD");
                break;
            } else {
                System.out.println("Error llegint el fitxer del client: " + nomFitxer);
            }
            
            

           
                System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
                output.writeObject(contingut);
                output.flush();
                System.out.println("Fitxer enviat al client: " + nomFitxer);
            
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Servidor servidor = new Servidor();
        Socket socket = servidor.connectar();
        servidor.enviarFitxers(socket);
        servidor.tancarConnexio(socket);
    }
}