import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
    }

    public byte[] getContingut() {
        File file = new File(nom);
        if (!file.exists() || !file.canRead()) { return null; }
        
        try {
            contingut = Files.readAllBytes(file.toPath());
            return contingut;
        } catch (IOException e) { return null; }
    }
}
