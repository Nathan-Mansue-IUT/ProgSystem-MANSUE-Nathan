import java.io.*;

public class MemoryManager {

    public static final int BLOCK_SIZE = 512;
    public static final int TOTAL_MEMORY = 1024 * 1024;
    public static final int NUM_BLOCKS =
            TOTAL_MEMORY / BLOCK_SIZE; // N = 2028

    public static final int SUPERBLOCK_OFFSET = 0; // La ou commence la zone mémoire
    public static final int BITMAP_OFFSET = BLOCK_SIZE;
    public static final int INODE_TABLE_OFFSET =
            2 * BLOCK_SIZE;
    public static final int DATA_OFFSET =
            129 * BLOCK_SIZE;

    public static final int INODE_SIZE = 128;

    public static final int INODE_TABLE_SIZE =
            DATA_OFFSET - INODE_TABLE_OFFSET;

    public static final int MAX_INODES =
            INODE_TABLE_SIZE / INODE_SIZE;

    private byte[] memory;

    public MemoryManager() {
        this.memory = new byte[TOTAL_MEMORY];
        initializeFilesystem();
    }

    private void initializeFilesystem() {
        writeSuperblock();
        for(int i = 0 ; i <= 15 ; i++) {
		    memory[BITMAP_OFFSET + i] = (byte) 0xFF;	
		}
    }

    private void writeSuperblock() {

        Utils.writeString(
                memory,
                SUPERBLOCK_OFFSET,
                "MYFS1.0",
                16);

        Utils.writeInt(
                memory,
                SUPERBLOCK_OFFSET + 16,
                BLOCK_SIZE);

        Utils.writeInt(
                memory,
                SUPERBLOCK_OFFSET + 20,
                TOTAL_MEMORY);

        Utils.writeInt(
                memory,
                SUPERBLOCK_OFFSET + 24,
                NUM_BLOCKS);

        Utils.writeInt(
                memory,
                SUPERBLOCK_OFFSET + 28,
                MAX_INODES);
    }

    public byte[] getFilesystemMemory() {
        return memory;
    }
	
	public boolean setBlockUsed(int blockNumber, boolean used) {
		if (blockNumber < 0 ||
			blockNumber >= NUM_BLOCKS) {
			return false;
        }

		int byteIndex = blockNumber / 8;
		int bitPosition = blockNumber % 8;
		int offset = BITMAP_OFFSET + byteIndex;

		if (used) {
			// Positionner le bit à 1.
			memory[offset] |= 0x01 << bitPosition;
		} else {
			// Positionner le bit à 0.
			memory[offset] &= ~(0x01 << bitPosition); /* ~ inverse du bit */
		}

		return true;
	}

	public int isBlockUsed(int blockNumber) {

		if (blockNumber < 0 ||
			blockNumber >= NUM_BLOCKS) {
			return -1;
		}

		// Calculer byteIndex.
		int byteIndex = blockNumber / 8;
		// Calculer bitPosition.
		int bitPosition = blockNumber % 8;
		int offset = BITMAP_OFFSET + byteIndex;

		// Lire le bit. 
		return (memory[offset] >> bitPosition) & 0x01;
	}

	public int allocateBlock() {

		// TODO:
		// Parcourir les blocs de données :
		// 129 .. NUM_BLOCKS - 1.
		int blocNumero = 129;
		while(isBlockUsed(blocNumero) == 1 && blocNumero < NUM_BLOCKS) {
			blocNumero++;
		}
		// Retourner le premier bloc libre.
		
		// Le marquer immédiatement comme utilisé.
        setBlockUsed(blocNumero, 1);
		return blocNumero;
	}

}
