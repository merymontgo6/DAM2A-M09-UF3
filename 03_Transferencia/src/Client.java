import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = System.getProperty("java.io.tmpdir");
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    public void connectar() throws IOException {
        socket = new Socket("localhost", 9999);
        System.out.println("Connectant a -> localhost:9999");
        System.out.println("Connexio acceptada: " + socket.getInetAddress());
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void tancarConnexio() throws IOException {
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null) socket.close();
        System.out.println("Connexio tancada.");
    }

    public void rebreFitxers() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
            String nomFitxer = scanner.nextLine();
            
            if ("sortir".equalsIgnoreCase(nomFitxer)) {
                System.out.println("Sortint...");
                out.writeObject("");
                break;
            }

            out.writeObject(nomFitxer);
            out.flush();

            System.out.print("Nom del fitxer a guardar: ");
            String desti = scanner.nextLine();
            if (desti.isEmpty()) {
                desti = DIR_ARRIBADA + "/" + new File(nomFitxer).getName();
            }

            byte[] contingut = (byte[]) in.readObject();
            if (contingut != null) {
                try (FileOutputStream fos = new FileOutputStream(desti)) {
                    fos.write(contingut);
                    System.out.println("Fitxer rebut i guardat com: " + desti);
                }
            }

            if (contingut == null) {
                System.out.println("Error: El fitxer no existeix al servidor. Tancant connexió...");
                return;
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
        client.connectar();
        client.rebreFitxers();
        client.tancarConnexio();
    }
}