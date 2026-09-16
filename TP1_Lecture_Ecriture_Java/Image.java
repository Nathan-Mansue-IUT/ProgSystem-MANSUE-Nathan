import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Image {
    private int width;
    private int height;
    private int[][][] pixels; // pixels[y][x][0=R,1=G,2=B]

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /**
     * Constructeur : initialise une image vide.
     */
    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[height][width][3];
    }

    /**
     * Définit la couleur d'un pixel à la position (x, y)
     */
    public void setPixel(int x, int y, int r, int g, int b) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y][x][0] = r;
            pixels[y][x][1] = g;
            pixels[y][x][2] = b;
        }
    }

    /**
     * Sauvegarde l'image au format texte PPM (P3)
     */
    public void save_txt(String filename) throws IOException {
        try {
            FileWriter writer = new FileWriter(filename);

            writer.write("P3\n");
            // Écriture des dimensions
			writer.write(getWidth()+ " " + getHeight() +"\n");
            // Écriture de la valeur maximal
			writer.write("255\n");
            // Écriture des pixels
			
			for(int i = 0 ; i < getHeight() ; i++) {
				
				for(int j = 0 ; j < getWidth() ; j++) {
					writer.write(pixels[i][j][0] + " " + pixels[i][j][1] + " " + pixels[i][j][2] + " ");
				}
				writer.write("\n");
            }
            writer.close(); // Fermeture du fichier

            System.out.println("Image PPM créée avec succès !");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }
	
	/*public static read_txt(String filename) throws IOException {
        // TODO
    } */
	
	/**
	 * Sauvegarde de l'image au format binaire
	 */
	public void write_bin(String filename) throws IOException {
		
		byte[] aRetourner = new byte[this.getWidth() * this.getHeight() * 3];
		
		int curseur = 0;
		
		int passage;
		
		try {
			
            FileOutputStream fos = new FileOutputStream(filename);

            fos.write(("P6\n"+this.getWidth()+ " " + this.getHeight() +"\n"+"255\n").getBytes());
  
			
			for(int i = 0 ; i < getHeight() ; i++) {
				
				for(int j = 0 ; j < getWidth() ; j++) {
					for(int k=0 ; k < 3 ; k++) {
						passage = this.pixels[i][j][k];
						aRetourner[curseur] = (byte) (passage & 0xFF);
						curseur++;
					}
				}
            }
			fos.write(aRetourner);
            fos.close(); // Fermeture du fichier

            System.out.println("Image PPM créée avec succès !");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
	}
}