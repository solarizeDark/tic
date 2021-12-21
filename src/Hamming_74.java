public class Hamming_74 {

    public static int[][]
    G =
    {
            {1, 0, 0, 0, 1, 0, 1},
            {0, 1, 0, 0, 1, 1, 1},
            {0, 0, 1, 0, 1, 1, 0},
            {0, 0, 0, 1, 0, 1, 1}
    },
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
            syndrome += tempSum % 2;

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

    public static byte[] decode (byte[] encoded) {
        int i = 0;
        for (byte b : encoded) {

            if (i % 2 == 0 && i > 0) {
                System.out.println(decodeSymbol(encoded[i - 2], encoded[i - 1]));
            }

            byte syndrome = syndrome(b);
            switch (syndrome) {
                case 3: b ^= 1 << 3; break;
                case 5: b ^= 1 << 6; break;
                case 6: b ^= 1 << 4; break;
                case 7: b ^= 1 << 5; break;
            }

            i++;
        }
        System.out.println(decodeSymbol(encoded[i - 2], encoded[i - 1]));

        return encoded;
    }

    public static byte[] encode(String input) {

        int i = 0;
        byte[] encoded = new byte[2 * input.length()];

        for (char symbol : input.toCharArray()) {
            byte[] res = encodeSymbol(symbol);
            encoded[i++] = res[0];
            encoded[i++] = res[1];
        }

        for(byte b : encoded) System.out.println(Integer.toBinaryString(b));

        return encoded;
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

    public static void main(String[] args) {
        decode(encode("hello"));
    }

}
