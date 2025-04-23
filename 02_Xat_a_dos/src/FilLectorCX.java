
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class FilLectorCX extends Thread {
    //un objecteoutputstream 
    private ObjectOutputStream oos;
    //un constructor amb el stream
    public FilLectorCX(ObjectOutputStream oos) {
        this.oos = oos;
    }
    //un metode d'execucio que rebi els missatges del Xat
   @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            String missatge;
            while (true) {
                missatge = scanner.nextLine();
                if (missatge.equalsIgnoreCase("sortir")) {
                    break;
                }
                //ClientXat.enviarMissatge(missatge);
            }            
            scanner.close();
        } catch (Exception e) {
        }
    }
}