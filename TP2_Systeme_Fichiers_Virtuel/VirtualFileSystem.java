import java.util.*;

public class VirtualFileSystem {

    private MemoryManager memoryManager;
	
	public static final int INODE_OCCUPE = 1; // Valeur arbitraire 

    public VirtualFileSystem() {
        this.memoryManager =
                new MemoryManager();
    }

    private int allocateInode() {

        byte[] memory =
                memoryManager.getFilesystemMemory();
				
        // Parcourir les inodes de 0 à MAX_INODES - 1.
        // Identifier le premier inode libre.
        // Retourner son numéro.
		int parcours;

        for(parcours = 0 ; parcours < MemoryManager.MAX_INODES ; parcours++) {
			Inode inodeActuel = new Inode(memoryManager, parcours);
			
			if(inodeActuel.getFileType() == 0) {
				break; // On sort de la boucle quand on trouve un inode libre
		    }
		}
		
		if(parcours == MemoryManager.MAX_INODES) {
		    return -1;	
		}

        return parcours;
    }

    public boolean createFile(
            String directory,
            String filename) {

        int inodeNum = allocateInode();

        if (inodeNum == -1) {
            return false;
        }

        // TODO:
        // Construire l'inode.
		Inode inodeActuel = new Inode(memoryManager, inodeNum);
		long dateActuelle = System.currentTimeMillis();
		inodeActuel.writeToMemory(INODE_OCCUPE, // Type de fichier
		                          0, // Taille du fichier
								  dateActuelle, // Date de création
								  dateActuelle, // Date de modification
								  new int[10], // 10 pointeurs directs
								  0, // le pointeur indirect
								  (short) 0x01ED, // Permission 755
								  1); // Link count)
								  
        // L'initialiser comme fichier vide.

        return true;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }
	
	public boolean writeFile(
        int inodeNum,
        byte[] data) {

		int blocksNeeded =
            (data.length
            + MemoryManager.BLOCK_SIZE - 1)
            / MemoryManager.BLOCK_SIZE;

		if (blocksNeeded > Inode.DIRECT_POINTERS) {
			return false;
		}

		int[] blockPointers =
            new int[Inode.DIRECT_POINTERS];

		// Allouer blocksNeeded blocs.
		for(int allocation = 0 ; allocation < blocksNeeded ; allocation++) {
		    blockPointers[allocation] = memoryManager.allocateBlock();	
		}

		byte[] memory =
            memoryManager.getFilesystemMemory();

		int bytesRemaining = data.length;

		int dataSrcOffset = 0;

		// Pour chaque bloc :
		// - calculer la quantité à copier ;
		// - récupérer le numéro du bloc ;
		// - calculer son offset physique ;
		// - copier les données.
		int numeroBloc = 0;
		int offsetPhysique;
		while(bytesRemaining != 0) {
			int quantiteAEcrire = Math.min(bytesRemaining, 
			                               MemoryManager.BLOCK_SIZE);
            offsetPhysique = blockPointers[numeroBloc] 
			                     * MemoryManager.BLOCK_SIZE;
								 
			System.arraycopy(data, dataSrcOffset, 
			                 memory, 
							 offsetPhysique, 
							 quantiteAEcrire);
							 
			dataSrcOffset += quantiteAEcrire;
			bytesRemaining -= quantiteAEcrire;
			numeroBloc++;
		}

		// Mettre à jour l'inode.
		long dateActuelle = System.currentTimeMillis();
        Inode inodeActuel = new Inode(memoryManager, inodeNum);
		inodeActuel.writeToMemory(inodeActuel.getFileType(), // Type de fichier
		                          data.length, // Taille du fichier
								  inodeActuel.getFileCreationDate(), // Date de création
								  dateActuelle, // Date de modification
								  blockPointers, // 10 pointeurs directs
								  inodeActuel.getFileIndirectPointer(), // le pointeur indirect
								  inodeActuel.getFilePermissions(), // Permission 755
								  inodeActuel.getFileNombreLiens()); // Link count)
		return true;
	}
	
	public byte[] readFile(int inodeNum) {

		Inode inode =
				new Inode(memoryManager, inodeNum);

		int fileSize =
				inode.getFileSize();

		if (fileSize == 0) {
			return new byte[0];
		}

		byte[] fileData =
				new byte[fileSize];

		byte[] memory =
				memoryManager.getFilesystemMemory();

		int[] blockPointers =
				inode.getDirectPointers();

		// Parcourir les blocs utilisés.
		// Copier chaque fragment vers fileData.
		int dataDstOffset = 0;	
		int bytesRemaining = fileSize;
			
		int offsetPhysique;
		int numeroBloc = 0;
		while(bytesRemaining != 0) {
			int quantiteAEcrire = Math.min(bytesRemaining, 
			                               MemoryManager.BLOCK_SIZE);
            offsetPhysique = blockPointers[numeroBloc] 
			                     * MemoryManager.BLOCK_SIZE;
								 
			System.arraycopy(memory, offsetPhysique, 
			                 fileData, 
							 dataDstOffset, 
							 quantiteAEcrire);
							 
			dataDstOffset += quantiteAEcrire;
			bytesRemaining -= quantiteAEcrire;
			numeroBloc++;
		}

		return fileData;
	}
}