
import java.io.ObjectInputStream;

public class FilServidorXat extends Thread {
    private ObjectInputStream ois;

    public FilServidorXat(String nom, ObjectInputStream ois) {
        super(nom);
        this.ois = ois;
    }

    @Override
    public void run() {
        try {
            String missatge;
            
            while (true) {
                missatge = (String) ois.readObject();
                System.out.println("Client: " + missatge);
                if (missatge != null && missatge.equalsIgnoreCase("sortir")) {
                    System.out.println("El client ha tancat la connexió");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error en rebre missatges del client: " + e.getMessage());
        }
    }
}