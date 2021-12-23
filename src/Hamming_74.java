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
        if (encoded == 0) return 0;

        byte syndrome = 0b0;
        for (int i = 0; i < 3; i++) {
            int tempSum = 0;
            for (int j = 6; j >= 0; j--) {
                int t = (((encoded & (1 << j)) > 0 ? 1 : 0));
                tempSum += H[i][6 - j] * (((encoded & (1 << j)) > 0 ? 1 : 0));
            }
            syndrome += (tempSum % 2) << (2 - i);

        }
        return syndrome;
    }

    public static char decodeSymbol(byte f, byte s) {
        f >>= 3;
        s >>= 3;
        s <<= 4;
        s |= f;
        return (char) s;
    }

    public static byte[] decode (String filePath) throws IOException {

        Path from = Paths.get(filePath);

        byte[] encoded = Files.readAllBytes(from);
        char[] res = new char[encoded.length / 2]; // br
        int i = 0;
        int j = 0;
        for (; i < encoded.length; i++) {

            byte syndrome = syndrome(encoded[i]);
            switch (syndrome) {
                case 3: encoded[i] ^= 1 << 3; break;
                case 5: encoded[i] ^= 1 << 6; break;
                case 6: encoded[i] ^= 1 << 4; break;
                case 7: encoded[i] ^= 1 << 5; break;
            }

            if (i % 2 == 0 && i > 0) {
                res[j++] = decodeSymbol(encoded[i - 2], encoded[i - 1]);
            }

        }
        res[j] = decodeSymbol(encoded[i - 2], encoded[i - 1]);

        File encodedFile = new File("hamming(7,4)_encoding_resources/decoded.txt");
        FileWriter writer = new FileWriter(encodedFile);
        for (char symbol : res) {
            writer.write(symbol);
        }

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

        File encodedFile = new File("hamming(7,4)_encoding_resources/encoded.txt");
        FileWriter writer = new FileWriter(encodedFile);

        for (byte en : encoded) writer.write(en);

        writer.close();

    }

    public static byte[] encodeSymbol(char symbol) {
        byte[] res = new byte[2];

        if (symbol == 0) return res;

        res[0] = (byte) (symbol & 0b1111);
        res[1] = (byte) (symbol & 0b11110000);
        res[1] >>= 4;

        for (int i = 0; i < 2; i++) {

            if (res[i] == 0) continue;

            byte b3 = (byte) (res[i] & 0b1)     != 0 ? (byte) 1 : 0;
            byte b2 = (byte) (res[i] & 0b10)    != 0 ? (byte) 1 : 0;
            byte b1 = (byte) (res[i] & 0b100)   != 0 ? (byte) 1 : 0;
            byte b0 = (byte) (res[i] & 0b1000)  != 0 ? (byte) 1 : 0;

            res[i] <<= 3;

            res[i] |= ((b0 + b1 + b2) % 2) << 2;
            res[i] |= ((b1 + b2 + b3) % 2) << 1;
            res[i] |= ((b0 + b1 + b3) % 2);
        }

        return res;
    }

    public static void main(String[] args) throws IOException {
        encode("hamming(7,4)_encoding_resources/message.txt");
        decode("hamming(7,4)_encoding_resources/encoded.txt");
    }

}
