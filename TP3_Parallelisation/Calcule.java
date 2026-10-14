public class Calcule extends Thread {
    
    private long variableInstance = 0;
    
    private int valeur, multiplicateur, index;
    
    public Calcule(int valeur, int multiplicateur, int index) {
        this.valeur = valeur;
        this.multiplicateur = multiplicateur;
        this.index = index;
    }
    
    @Override
    public void run(){
        System.out.println("Execution du thread numéro " + this.index);
        // Calcul intensif simulé par des opérations mathématiques
        for (int j = 0; j < this.multiplicateur; j++) {
            variableInstance += this.valeur * this.valeur + this.valeur;
            // on pourrais aussi mettre un sleep
        }
    }
    
    public long getResults() {
        return variableInstance;
    }
}