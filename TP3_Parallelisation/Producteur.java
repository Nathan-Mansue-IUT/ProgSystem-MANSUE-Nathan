import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ProducteurConsommateur {
    public void run() {
        for (int i = 0; i < 10; i++) {
            int produit = genererProduit();

            // TODO: Attendre une place libre
            // TODO: Acquérir l'accès exclusif au buffer  
            // TODO: Ajouter l'élément au buffer
            // TODO: Libérer l'accès au buffer
            // TODO: Signaler qu'un élément est disponible
        }
    }
    
    private static int genererProduit() {
        return random.nextInt(10, 50);
    }
}