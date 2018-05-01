import java.nio.charset.StandardCharsets;

/**
 *  Project 1
 *  @author Adison Lee
 *  ID#:    913028238
 *
 */
public class Main {
    public static void main(String[] args) {
        int[] desPlaintext = {0, 0, 0, 0, 0, 0, 0, 1,
                0, 0, 1, 0, 0, 0, 1, 1,
                0, 1, 0, 0, 0, 1, 0, 1,
                0, 1, 1, 0, 0, 1, 1, 1,
                1, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 1, 0, 1, 0, 1, 1,
                1, 1, 0, 0, 1, 1, 0, 1,
                1, 1, 1, 0, 1, 1, 1, 1};

        int[] desSecretKey = {0, 0, 0, 1, 0, 0, 1, 1,
                0, 0, 1, 1, 0, 1, 0, 0,
                0, 1, 0, 1, 0, 1, 1, 1,
                0, 1, 1, 1, 1, 0, 0, 1,
                1, 0, 0, 1, 1, 0, 1, 1,
                1, 0, 1, 1, 1, 1, 0, 0,
                1, 1, 0, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 0, 0, 1};


        String ecbPlaintext = "GO GATORS!";
        String ecbSecretKey = "ABCDEFGH";

        String cbcPlaintext = "SECURITYSECURITY";
        String cbcSecretKey = "ABCDEFGH";
        String IV = "ABCDEFGH";

        Crypto test = new Crypto();
        int[] DES = test.DES(desPlaintext, desSecretKey);
        int[] ECB = test.ECB(ecbPlaintext, ecbSecretKey);
        int[] CBC = test.CBC(cbcPlaintext, cbcSecretKey, IV);

        System.out.println("DES: ");
        for(int bits : DES)
        {
            System.out.print(bits + " ");
        }
        System.out.println("\n\nECB: ");
        System.out.println("Plain text: " + ecbPlaintext);
        System.out.println("Secret key: " + ecbSecretKey);
        System.out.print("Cipher text: ");
        for(int bits : ECB)
        {
            System.out.print(bits + " ");
        }
        System.out.println("\n\nCBC: ");
        System.out.println("Plain text: " + cbcPlaintext);
        System.out.println("Secret key: " + cbcSecretKey);
        System.out.println("Initialization vector: " + cbcSecretKey);
        System.out.print("Cipher text: ");
        for(int bits : CBC)
        {
            System.out.print(bits + " ");
        }
        System.out.println("");
    }

}
