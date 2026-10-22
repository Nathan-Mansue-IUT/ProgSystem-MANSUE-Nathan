import java.util.ArrayList;
import java.util.List;

public class TestSemaphore {
    private static final int PRODUITS_PAR_PRODUCTEUR = 10;

    public static void main(String[] args) throws InterruptedException {
        // Scénario 1 : équilibré (2 producteurs, 3 consommateurs)
        // Producteurs et consommateurs à vitesse comparable
        lancerScenario("Scénario équilibré", 2, 3, 20, 20);

        System.out.println();

        // Scénario 2 : déséquilibré (5 producteurs, 2 consommateurs)
        // Producteurs rapides, consommateurs lents : le buffer se remplit
        lancerScenario("Scénario déséquilibré", 5, 2, 5, 60);
    }

    /**
     * @param delaiProd  temps (ms) pour fabriquer un produit
     * @param delaiCons  temps (ms) pour traiter un produit
     */
    private static void lancerScenario(String nom, int nbProd, int nbCons,
                                       long delaiProd, long delaiCons)
            throws InterruptedException {
        System.out.println("=== " + nom + " : " + nbProd + " producteurs, "
                         + nbCons + " consommateurs ===");

        ProducteurConsommateur pc = new ProducteurConsommateur();
        int totalAProduire = nbProd * PRODUITS_PAR_PRODUCTEUR;

        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= nbProd; i++) {
            threads.add(new Thread(
                pc.new Producteur(i, PRODUITS_PAR_PRODUCTEUR, delaiProd), "P" + i));
        }

        // Répartir les consommations : le dernier consommateur prend le reste,
        // sinon un thread resterait bloqué à l'infini sur acquire()
        int part = totalAProduire / nbCons;
        int reste = totalAProduire - part * nbCons;
        for (int i = 1; i <= nbCons; i++) {
            int nb = (i == nbCons) ? part + reste : part;
            threads.add(new Thread(pc.new Consommateur(i, nb, delaiCons), "C" + i));
        }

        // Thread de monitoring (daemon : ne bloque pas l'arrêt de la JVM)
        Thread moniteur = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.printf(
                        "%s | produits=%2d consommés=%2d | prod. bloqués=%d cons. bloqués=%d%n",
                        pc.etatBuffer(), pc.getTotalProduit(), pc.getTotalConsomme(),
                        pc.producteursEnAttente(), pc.consommateursEnAttente());
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                // arrêt demandé
            }
        }, "Moniteur");
        moniteur.setDaemon(true);

        long debut = System.nanoTime();
        moniteur.start();
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        moniteur.interrupt();
        moniteur.join();
        long duree = (System.nanoTime() - debut) / 1_000_000;

        // Vérification de cohérence
        System.out.println("--- Bilan ---");
        System.out.println("Produits  : " + pc.getTotalProduit() + " / " + totalAProduire);
        System.out.println("Consommés : " + pc.getTotalConsomme() + " / " + totalAProduire);
        System.out.println("Places libres + éléments dispo = "
            + (pc.getPlacesLibres() + pc.getElementsDisponibles()) + " (attendu : 5)");
        System.out.println("Cohérent : "
            + (pc.getTotalProduit() == pc.getTotalConsomme()
               && pc.getTotalConsomme() == totalAProduire));
        System.out.println("Durée : " + duree + " ms");
    }
}