public class CompteurSecurise {
    private static int compteurGlobal = 0;  // Variable partagée MODIFIABLE
    
    private static final Object verrou = new Object();

    static class Incrementeur implements Runnable {
        private final String nom;
        private final int nombreIncrements;

        public Incrementeur(String nom, int nombreIncrements) {
            this.nom = nom;
            this.nombreIncrements = nombreIncrements;
        }

        @Override
        public void run() {
            for (int i = 0; i < nombreIncrements; i++) {
                // TODO: Expliquer pourquoi cette ligne pose problème
                /*
                 * La variable compteurGlobal est partagée entre tous les threads. 
                 * Contrairement au tableau DONNEES de l’exercice 1 qui était en lecture seule, 
                 * ici chaque thread modifie cette variable.
                 * Ce qui peut engendrer des erreurs
                 */
                synchronized(verrou) {
                    // TODO: Placer ici l'opération critique
                    compteurGlobal++;
                    // RACE CONDITION ICI !
                }
            }
            System.out.println(nom + " terminé. Compteur vu : " + compteurGlobal);
        }
    }

    public static int getCompteur() {
        synchronized(verrou) {
            return compteurGlobal;
        }
    }

    public static void resetCompteur() {
        synchronized(verrou) {
            compteurGlobal = 0;
        }
    }
}