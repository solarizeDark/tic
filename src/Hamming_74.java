import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Hamming_74 {

    public static int[][]
    H =
    {
            {1, 1, 1, 0, 1, 0, 0},
            {0, 1, 1, 1, 0, 1, 0},
            {1, 1, 0, 1, 0, 0, 1}
    };

    public static byte syndrome(byte encoded) {
        byte syndrome = 0b0;
        for (int i = 0; i < 3; i++) {
            int tempSum = 0;
            for (int j = 6; j >= 0; j--) {
                tempSum += H[i][j] & (((encoded & 1 << (6 - j)) > 0 ? 0b1 : 0b0));
            }
            syndrome += (tempSum % 2) << (2 - i);

        }
        // System.out.println(syndrome);
        return syndrome;
    }

    public static char decodeSymbol(byte f, byte s) {
        f >>= 3;
        s >>= 3;
        f <<= 4;
        f |= s;
        return (char) f;
    }

    public static byte[] decode (String filePath) throws IOException {

        Path from = Paths.get(filePath);

        byte[] encoded = Files.readAllBytes(from);
        char[] res = new char[encoded.length / 2];
        int i = 0;
        int j = 0;
        for (; i < encoded.length; i++) {

            if (i % 2 == 0 && i > 0) {
                res[j++] = decodeSymbol(encoded[i - 2], encoded[i - 1]);
            }

            byte syndrome = syndrome(encoded[i]);
            switch (syndrome) {
                case 3: encoded[i] ^= 1 << 3; break;
                case 5: encoded[i] ^= 1 << 6; break;
                case 6: encoded[i] ^= 1 << 4; break;
                case 7: encoded[i] ^= 1 << 5; break;
            }

        }
        res[j] = decodeSymbol(encoded[i - 2], encoded[i - 1]);

        File encodedFile = new File("decoded.txt");
        FileWriter writer = new FileWriter(encodedFile);
        writer.write(res);

        writer.close();

        return encoded;
    }

    public static void encode(String filePathFrom) throws IOException {

        Path from = Paths.get(filePathFrom);

        byte[] input = Files.readAllBytes(from);

        int i = 0;
        byte[] encoded = new byte[2 * input.length];

        for (byte symbol : input) {
            byte[] res = encodeSymbol((char)symbol);
            encoded[i++] = res[0];
            encoded[i++] = res[1];
        }

        File encodedFile = new File("encoded.txt");
        FileWriter writer = new FileWriter(encodedFile);

        for (byte en : encoded) writer.write(en);

        writer.close();

    }

    public static byte[] encodeSymbol(char symbol) {
        byte[] res = new byte[2];
        byte encoded = 0b0;
        int cnt = 8 - Integer.toBinaryString(symbol).toCharArray().length;
        int i = 0;
        for (char t : Integer.toBinaryString(symbol).toCharArray()) {
            encoded |= t == '1' ? 0b1 : 0b0;
            encoded <<= 1;
            cnt++;
            if (cnt == 4) {
                encoded >>= 1;
                byte b0 = (byte) (encoded >> 3 & 0b1);
                byte b1 = (byte) (encoded >> 2 & 0b1);
                byte b2 = (byte) (encoded >> 1 & 0b1);
                byte b3 = (byte) (encoded      & 0b1);

                encoded <<= 1;
                encoded |= (b0 + b1 + b2) % 2;
                encoded <<= 1;
                encoded |= (b1 + b2 + b3) % 2;
                encoded <<= 1;
                encoded |= (b0 + b1 + b3) % 2;
                cnt = 0;
                res[i++] = encoded;
                encoded = 0b0;
            }
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        encode("message.txt");
        decode("encoded.txt");
    }

}
