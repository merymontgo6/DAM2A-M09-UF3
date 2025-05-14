import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients implements Runnable {
    private Socket client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir;

    public GestorClients(Socket client, ServidorXat servidor) {
        this.client = client;
        this.servidor = servidor;
        this.sortir = false;
        
        try {
            output = new ObjectOutputStream(client.getOutputStream());
            input = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.err.println("Error al crear streams: " + e.getMessage());
        }
    }

    public String getNom() {
        return nom;
    }

    @Override
    public void run() {
        try {
            while (!sortir) {
                String missatgeRaw = (String) input.readObject();
                processaMissatge(missatgeRaw);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!sortir) {
                System.err.println("Error en la comunicació amb el client " + nom + ": " + e.getMessage());
            }
        } finally {
            try {
                if (client != null && !client.isClosed()) {
                    client.close();
                }
                servidor.eliminarClient(nom);
            } catch (IOException e) {
                System.err.println("Error al tancar el socket: " + e.getMessage());
            }
        }
    }

    public void enviarMissatge(String remitent, String missatge) {
        try {
            output.writeObject(remitent + ": " + missatge);
            output.flush();
        } catch (IOException e) {
            System.err.println("Error al enviar missatge a " + nom + ": " + e.getMessage());
        }
    }

    public void processaMissatge(String missatgeRaw) {
        String codi = Missatge.getCodiMissatge(missatgeRaw);
        String[] parts = Missatge.getPartsMissatge(missatgeRaw);

        if (codi == null || parts == null) {
            System.err.println("Missatge incorrecte rebut: " + missatgeRaw);
            return;
        }

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                if (parts.length >= 2) {
                    nom = parts[1];
                    servidor.afegirClient(this);
                }
                break;
                
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidor.eliminarClient(nom);
                break;
                
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;
                
            case Missatge.CODI_MSG_PERSONAL:
                if (parts.length >= 3) {
                    String destinatari = parts[1];
                    String missatge = parts[2];
                    servidor.enviarMissatgePersonal(destinatari, nom, missatge);
                }
                break;
                
            case Missatge.CODI_MSG_GRUP:
                if (parts.length >= 2) {
                    servidor.enviarMissatgeGrup(nom + ": " + parts[1]);
                }
                break;
                
            default:
                System.err.println("Codi de missatge desconegut: " + codi);
        }
    }
}