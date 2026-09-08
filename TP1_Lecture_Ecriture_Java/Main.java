import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String enTete = "P3\n100 100\n255\n";

        try {
            FileOutputStream fos = new FileOutputStream("firstPPM.ppm");
            fos.write(enTete.getBytes()); // écriture directe des octets
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
