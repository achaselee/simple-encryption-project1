import java.nio.charset.StandardCharsets;

/**
 *  Project 1
 *  @author Adison Lee
 *  ID#:    913028238
 *
 */
public class Crypto
{
    private int plaintextBlockSize = 64;

    int[] DES(int[] plaintext, int[] key)
    {
        int[] cipherText = new int[plaintextBlockSize];
        if (checkValidBlockSize(plaintext, key))
        {
            cipherText = encodePlainText(permutation(plaintext, permutedChoiceOne, plaintextBlockSize), key);
            cipherText = reverseLRBlocks(cipherText);
            cipherText = permutation(cipherText, inverseIP, plaintextBlockSize);
        }
        else
        {
            System.out.println("DES: Invalid plaintext or key block size.");
        }

        return cipherText;
    }

    int[] ECB(String plaintext, String key)
    {
        byte[] message = plaintext.getBytes(StandardCharsets.US_ASCII);
        byte[] theKey = key.getBytes(StandardCharsets.US_ASCII);
        int[] asciiBits, keyBits, sixtyFourBitKey = new int[64];

        asciiBits = convertToBits(message);
        keyBits = convertToBits(theKey);

        if (checkValidBlockSize(asciiBits, keyBits))
        {
            System.arraycopy(keyBits, 0, sixtyFourBitKey, 0, 64);
            asciiBits = checkIfPadNeeded(asciiBits);
            asciiBits = encryptECB(asciiBits, sixtyFourBitKey);
            asciiBits = bitsToAsciiCode(asciiBits);
        }
        else
        {
            System.out.println("ECB: Invalid plaintext or key block size.");
        }

        return asciiBits;
    }

    int[] CBC(String plaintext, String key, String IV)
    {
        byte[] cbcPT = plaintext.getBytes(StandardCharsets.US_ASCII);
        byte[] cbcKey = key.getBytes(StandardCharsets.US_ASCII);
        byte[] initVector = IV.getBytes(StandardCharsets.US_ASCII);
        int[] ptBits, cbcKeyBits, iVec, sixtyFourBitKey = new int[64], sixtyFourBitIV = new int[64];

        ptBits = convertToBits(cbcPT);
        cbcKeyBits = convertToBits(cbcKey);
        iVec = convertToBits(initVector);

        if((checkValidBlockSize(ptBits, cbcKeyBits)) && iVec.length == 64)
        {
            System.arraycopy(cbcKeyBits, 0, sixtyFourBitKey, 0, 64);
            System.arraycopy(iVec, 0, sixtyFourBitIV, 0, 64);
            ptBits = checkIfPadNeeded(ptBits);
            ptBits = encryptCBC(ptBits,sixtyFourBitKey, sixtyFourBitIV);
            ptBits = bitsToAsciiCode(ptBits);
        }
        else
        {
            System.out.println("CBC: Invalid plaintext or key block size.");
        }

        return ptBits;
    }

    int[] encryptCBC(int[] plaintext, int[] key, int[] iv)
    {
        int[] desEncryptedBlock = new int[64];
        int[] encryptedCipher = new int[plaintext.length];

        for(int i = 0; i < plaintext.length; i += 64)
        {
            System.arraycopy(plaintext, i, desEncryptedBlock, 0, 64);
            desEncryptedBlock = xor(desEncryptedBlock, iv);
            desEncryptedBlock = DES(desEncryptedBlock, key);
            System.arraycopy(desEncryptedBlock, 0, iv, 0, 64); // New IV will be previous cipher text
            System.arraycopy(desEncryptedBlock, 0, encryptedCipher, i, 64);
        }

        return encryptedCipher;
    }

    int[] bitsToAsciiCode(int[] bits)
    {
        int[] asciiCode = new int[bits.length / 8];
        int k = 0;

        for(int i = 0; i < asciiCode.length; i++)
        {
            String code = "";
            for(int j = 0; j < 8; j++)
            {
                code += bits[k++];
            }
            asciiCode[i] = Integer.parseInt(code, 2);
        }

        return asciiCode;
    }

    int[] checkIfPadNeeded(int[] block)
    {
        int remainder = block.length % 64;
        int numberOfZeroes = 64 - remainder;

        if(remainder != 0)
        {
            int[] paddedBlock = new int[block.length + numberOfZeroes];
            System.arraycopy(block, 0, paddedBlock, 0, block.length);
            return paddedBlock;
        }

        return block;
    }

    int[] encryptECB(int[] plaintext, int[] key)
    {
        int[] desEncryptedBlock = new int[64];
        int[] encryptedCipher = new int[plaintext.length];
        for(int i = 0; i < plaintext.length; i += 64)
        {
            System.arraycopy(plaintext, i, desEncryptedBlock, 0, 64);
            desEncryptedBlock = DES(desEncryptedBlock, key);
            System.arraycopy(desEncryptedBlock, 0, encryptedCipher, i, 64);
        }

        return encryptedCipher;
    }

    int[] convertToBits(byte[] bytes)
    {
        int counter = 0;
        int[] eightBitLetter = new int[8];
        int[] bitsArray = new int[bytes.length * 8];

        for (int b = 0; b < bytes.length; b++)
        {
            for (int i = 7; i >= 0; i--)
            {
                eightBitLetter[i] = bytes[b] % 2;
                bytes[b] /= 2;
            }
            for (int j = 0; j < eightBitLetter.length; j++)
            {
                bitsArray[counter++] = eightBitLetter[j];
            }
        }

        return bitsArray;
    }

    boolean checkValidBlockSize(int[] ptBlockSize, int[] keyBlockSize)
    {
        return !((ptBlockSize.length < plaintextBlockSize) || (keyBlockSize.length < plaintextBlockSize));
    }

    // Returns final encrypted data
    int[] encodePlainText(int[] IP, int[] key)
    {
        int[] mangledResult;
        int[] mangled;
        int[] subkey;
        int[] leftBlock = new int[32];
        int[] rightBlock = new int[32];
        int[] permutedKey = permutation(key, initialPermutationOfKey, 56);
        int cFirstBit;
        int dFirstBit;
        int cSecondBit;
        int dSecondBit;
        int[] concatenatedCnDnBlock = new int[56]; // Concatenates CnDn
        int[] cBlock = new int[28];
        int[] dBlock = new int[28];
        System.arraycopy(permutedKey, 0, cBlock, 0, 28);
        System.arraycopy(permutedKey, 28, dBlock, 0, 28);

        for (int iterationNum = 1; iterationNum <= 16; iterationNum++)
        {
            cFirstBit = cBlock[0];
            dFirstBit = dBlock[0];
            cSecondBit = cBlock[1];
            dSecondBit = dBlock[1];
            if (iterationNum == 1 || iterationNum == 2 || iterationNum == 9 || iterationNum == 16)
            {
                for (int bitPos = 1; bitPos < 28; bitPos++)
                {
                    cBlock[bitPos - 1] = cBlock[bitPos];
                    dBlock[bitPos - 1] = dBlock[bitPos];

                    if (bitPos == 27)
                    {
                        cBlock[bitPos] = cFirstBit;
                        dBlock[bitPos] = dFirstBit;
                    }
                }
            }
            else
            {
                for (int bitPos = 2; bitPos < 28; bitPos++)
                {
                    cBlock[bitPos - 2] = cBlock[bitPos];
                    dBlock[bitPos - 2] = dBlock[bitPos];

                    if (bitPos == 26)
                    {
                        cBlock[26] = cFirstBit;
                        dBlock[26] = dFirstBit;
                    }
                    else if (bitPos == 27)
                    {
                        cBlock[27] = cSecondBit;
                        dBlock[27] = dSecondBit;
                    }
                }
            }
            System.arraycopy(cBlock, 0, concatenatedCnDnBlock, 0, 28);
            System.arraycopy(dBlock, 0, concatenatedCnDnBlock, 28, 28);

            subkey = permutation(concatenatedCnDnBlock, permutedChoiceTwo, 48);

            System.arraycopy(IP, 0, leftBlock, 0, 32);
            System.arraycopy(IP, 32, rightBlock, 0, 32);

            mangled = manglerFunction(rightBlock, subkey);

            // Do Rn = Ln-1 xor F(Rn-1, Kn)
            mangledResult = xor(mangled, leftBlock);

            System.arraycopy(rightBlock, 0, IP, 0, 32);
            System.arraycopy(mangledResult, 0, IP, 32, 32);
        }

        return IP;
    }

    int[] manglerFunction(int[] rightBlock, int[] subKey)
    {
        int[] functionF;
        int[] newF;
        int[] expandedRightBlock;

        // Expand right block
        expandedRightBlock = permutation(rightBlock, eBitSelectionTable, 48);

        // XOR right block with subkey
        functionF = xor(expandedRightBlock, subKey);

        newF = permutation(sBox(functionF), fPermutation, 32);

        return newF;
    }

    int[] reverseLRBlocks(int[] lrBlock)
    {
        int[] rl = new int[lrBlock.length];

        System.arraycopy(lrBlock, 0, rl, 32, 32);
        System.arraycopy(lrBlock, 32, rl, 0, 32);

        return rl;
    }

    int[] xor(int[] thoseBits, int[] theseBits)
    {
        int[] xorResult = new int[thoseBits.length];

        for (int i = 0; i < thoseBits.length; i++)
        {
            xorResult[i] = (thoseBits[i] ^ theseBits[i]);
        }

        return xorResult;
    }

    int[] sBox(int[] expandedXorKn)
    {
        int counter = 0;
        int count = 0;
        int[] newBits = new int[32];
        int[][] blockB = new int[8][6];

        // 2d array of 8 rows with 6 columns of 6bits
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 6; j++)
            {
                blockB[i][j] = expandedXorKn[counter++];
            }

            int[] sValueBits = sBoxValue(blockB[i], i);

            for (int k = 0; k < 4; k++)
            {
                newBits[count++] = sValueBits[k];
            }
        }

        return newBits;
    }

    int[] sBoxValue(int[] blockB, int sBoxCount)
    {
        int sBoxValue;
        int[] sBoxBits = new int[4];

        int row = blockB[0] * 2 + blockB[5];
        int column = blockB[1] * 8 + blockB[2] * 4 + blockB[3] * 2 + blockB[4];

        sBoxValue = sValue(row, column, sBoxCount);

        for (int i = 3; i >= 0; i--)
        {
            sBoxBits[i] = sBoxValue % 2;
            sBoxValue /= 2;
        }

        return sBoxBits;
    }

    int sValue(int row, int column, int sBoxCount)
    {
        int sValue = 0;

        if (sBoxCount == 0)
        {
            sValue = s1[row][column];
        }
        else if (sBoxCount == 1)
        {
            sValue = s2[row][column];
        }
        else if (sBoxCount == 2)
        {
            sValue = s3[row][column];
        }
        else if (sBoxCount == 3)
        {
            sValue = s4[row][column];
        }
        else if (sBoxCount == 4)
        {
            sValue = s5[row][column];
        }
        else if (sBoxCount == 5)
        {
            sValue = s6[row][column];
        }
        else if (sBoxCount == 6)
        {
            sValue = s7[row][column];
        }
        else if (sBoxCount == 7)
        {
            sValue = s8[row][column];
        }

        return sValue;
    }

    int[] permutation(int[] bits, int[] permutedChoice, int bitSize)
    {
        int[] permutatedBits = new int[bitSize];
        for (int i = 0; i < permutedChoice.length; i++)
        {
            int bitPos = permutedChoice[i];
            permutatedBits[i] = bits[bitPos - 1];
        }

        return permutatedBits;
    }

    private int[] initialPermutationOfKey =
            {
                    57, 49, 41, 33, 25, 17, 9,
                    1, 58, 50, 42, 34, 26, 18,
                    10, 2, 59, 51, 43, 35, 27,
                    19, 11, 3, 60, 52, 44, 36,
                    63, 55, 47, 39, 31, 23, 15,
                    7, 62, 54, 46, 38, 30, 22,
                    14, 6, 61, 53, 45, 37, 29,
                    21, 13, 5, 28, 20, 12, 4
            };

    private int[] permutedChoiceTwo =
            {
                    14, 17, 11, 24, 1, 5,
                    3, 28, 15, 6, 21, 10,
                    23, 19, 12, 4, 26, 8,
                    16, 7, 27, 20, 13, 2,
                    41, 52, 31, 37, 47, 55,
                    30, 40, 51, 45, 33, 48,
                    44, 49, 39, 56, 34, 53,
                    46, 42, 50, 36, 29, 32
            };

    private int[] permutedChoiceOne =
            {
                    58, 50, 42, 34, 26, 18, 10, 2,
                    60, 52, 44, 36, 28, 20, 12, 4,
                    62, 54, 46, 38, 30, 22, 14, 6,
                    64, 56, 48, 40, 32, 24, 16, 8,
                    57, 49, 41, 33, 25, 17, 9, 1,
                    59, 51, 43, 35, 27, 19, 11, 3,
                    61, 53, 45, 37, 29, 21, 13, 5,
                    63, 55, 47, 39, 31, 23, 15, 7
            };

    private int[] eBitSelectionTable =
            {
                    32, 1, 2, 3, 4, 5,
                    4, 5, 6, 7, 8, 9,
                    8, 9, 10, 11, 12, 13,
                    12, 13, 14, 15, 16, 17,
                    16, 17, 18, 19, 20, 21,
                    20, 21, 22, 23, 24, 25,
                    24, 25, 26, 27, 28, 29,
                    28, 29, 30, 31, 32, 1
            };

    private int[] fPermutation =
            {
                    16, 7, 20, 21,
                    29, 12, 28, 17,
                    1, 15, 23, 26,
                    5, 18, 31, 10,
                    2, 8, 24, 14,
                    32, 27, 3, 9,
                    19, 13, 30, 6,
                    22, 11, 4, 25
            };

    private int[] inverseIP =
            {
                    40, 8, 48, 16, 56, 24, 64, 32,
                    39, 7, 47, 15, 55, 23, 63, 31,
                    38, 6, 46, 14, 54, 22, 62, 30,
                    37, 5, 45, 13, 53, 21, 61, 29,
                    36, 4, 44, 12, 52, 20, 60, 28,
                    35, 3, 43, 11, 51, 19, 59, 27,
                    34, 2, 42, 10, 50, 18, 58, 26,
                    33, 1, 41, 9, 49, 17, 57, 25
            };

    private int[][] s1 =
            {
                {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
            };

    private int[][] s2 =
            {
                {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
            };

    private int[][] s3 =
            {
                {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
            };

    private int[][] s4 =
            {
                {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
            };

    private int[][] s5 =
            {
                {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
            };

    private int[][] s6 =
            {
                {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
            };

    private int[][] s7 =
            {
                {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
            };

    private int[][] s8 =
            {
                {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
            };

}
