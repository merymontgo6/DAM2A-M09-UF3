import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static final int port = 7777;
    public static final String host = "localhost";
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public void conecta() {
        try {
            srvSocket = new ServerSocket(port);
            System.out.println("Servidor en marxa a localhost:" + port);
            clientSocket = srvSocket.accept();
            System.out.println("Esperant connexion a localhost:" + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.err.println("Error en la connexió: " + e.getMessage());
        }
    }

    public void repDades() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String missatge;
            while ((missatge = br.readLine()) != null) {
                System.out.println("Rebut: " + missatge);
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error en la lectura: " + e.getMessage());
        }
    }

    public void tanca() throws IOException {
        clientSocket.close();
        srvSocket.close();
        System.out.println("Servidor tancat");
    }

    public static void main(String[] args) throws IOException {
        Servidor servidor = new Servidor();
        servidor.conecta();
        servidor.repDades();
        servidor.tanca();
    }
}
