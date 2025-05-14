import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean sortir;
    private Thread filRebre;

    public void connecta() throws IOException {
        socket = new Socket("localhost", 9999);
        output = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Client connectat a localhost:9999");
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) throws IOException {
        output.writeObject(missatge);
        output.flush();
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() {
        sortir = true;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Flux d'entrada tancat.");
            System.out.println("Flux de sortida tancat.");
        } catch (IOException e) {
            System.err.println("Error al tancar els recursos: " + e.getMessage());
        }
    }

    public void ajuda() {
        System.out.println("---");
        System.out.println("Comandes disponibles:");
        System.out.println("  1.- Conectar al servidor (primer pas obligatori)");
        System.out.println("  2.- Enviar missatge personal");
        System.out.println("  3.- Enviar missatge al grup");
        System.out.println("  4.- (o línia en blanc)-> Sortir del client");
        System.out.println("  5.- Finalitzar tothom");
        System.out.println("---");
    }

    public String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        String linia;
        do {
            System.out.print(missatge);
            linia = scanner.nextLine().trim();
            if (!obligatori && linia.isEmpty()) {
                return linia;
            }
        } while (linia.isEmpty());
        return linia;
    }

    public void iniciarFilRebre() {
        filRebre = new Thread(() -> {
            try {
                input = new ObjectInputStream(socket.getInputStream());
                System.out.println("DEBUG: Iniciant rebuda de missatges...");
                
                while (!sortir) {
                    try {
                        String missatge = (String) input.readObject();
                        
                        // Si el mensaje no contiene código, mostrarlo directamente
                        if (missatge == null || missatge.trim().isEmpty()) {
                            continue;
                        }
                        
                        // Intentar extraer el código
                        String codi = Missatge.getCodiMissatge(missatge);
                        
                        if (codi == null) {
                            // Mostrar mensaje normal si no tiene código
                            System.out.println(missatge);
                            continue;
                        }
                        
                        // Procesar según el código
                        switch (codi) {
                            case Missatge.CODI_SORTIR_TOTS:
                                sortir = true;
                                System.out.println("Rebuda ordre de sortida general");
                                break;
                                
                            case Missatge.CODI_MSG_PERSONAL:
                                String[] partsPersonal = Missatge.getPartsMissatge(missatge);
                                if (partsPersonal != null && partsPersonal.length >= 3) {
                                    System.out.println("Missatge personal de (" + partsPersonal[1] + "): " + partsPersonal[2]);
                                }
                                break;
                                
                            case Missatge.CODI_MSG_GRUP:
                                String[] partsGrup = Missatge.getPartsMissatge(missatge);
                                if (partsGrup != null && partsGrup.length >= 2) {
                                    System.out.println("Missatge de grup [" + partsGrup[1] + "]: " + 
                                        (partsGrup.length > 2 ? partsGrup[2] : ""));
                                }
                                break;
                                
                            default:
                                System.out.println(missatge);
                        }
                    } catch (ClassNotFoundException e) {
                        System.err.println("Error en el format del missatge rebut");
                    }
                }
            } catch (IOException e) {
                if (!sortir) {
                    System.err.println("Error rebent missatge. Sortint...");
                }
            }
        });
        filRebre.start();
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner scanner = new Scanner(System.in);
        
        try {
            client.connecta();
            client.iniciarFilRebre();
            client.ajuda();
            
            while (!client.sortir) {
                String linia = client.getLinea(scanner, "", false);
                
                if (linia.isEmpty()) {
                    client.sortir = true;
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                } else {
                    try {
                        int opcio = Integer.parseInt(linia);
                        
                        switch (opcio) {
                            case 1:
                                String nom = client.getLinea(scanner, "Introdueix el nom: ", true);
                                client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                                break;
                                
                            case 2:
                                String destinatari = client.getLinea(scanner, "Destinatari: ", true);
                                String missatgePersonal = client.getLinea(scanner, "Missatge a enviar: ", true);
                                client.enviarMissatge(Missatge.getMissatgePersonal(destinatari, missatgePersonal));
                                break;
                                
                            case 3:
                                String missatgeGrup = client.getLinea(scanner, "Missatge a enviar al grup: ", true);
                                client.enviarMissatge(Missatge.getMissatgeGrup(missatgeGrup));
                                break;
                                
                            case 4:
                                client.sortir = true;
                                client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                                break;
                                
                            case 5:
                                client.sortir = true;
                                client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                                break;
                                
                            default:
                                System.out.println("Opció no vàlida");
                        }
                        client.ajuda();
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada no vàlida. Introdueix un número.");
                    } catch (IOException e) {
                        System.err.println("Error en enviar missatge: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error en la connexió: " + e.getMessage());
        } finally {
            client.tancarClient();
            scanner.close();
            System.out.println("Tancant client...");
        }
    }
}