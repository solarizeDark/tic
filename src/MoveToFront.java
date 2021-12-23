public class MoveToFront {

    static int[] alphabet;
    static char[] a;

    public static void setAlphabet() {
        alphabet = new int[128];
        a = new char[128];
        for (int i = 0; i < alphabet.length; i++) {
            alphabet[i] = i;
            a[i] = (char) (i);
        }
    }

    public static String encode(String fromBWT) {
        StringBuilder code = new StringBuilder();
        char[] bwtcode = fromBWT.toCharArray();

        // сложность nk
        for (int i = 0; i < bwtcode.length; i++) {
            // порядковый номер символа в алфавите
            int pos = bwtcode[i];
            int previous = alphabet[pos];
            code.append(pos);
            code.append('.');
            // символ становится на нулевую позицию
            alphabet[pos] = 0;
            // все символы до текущего сдивагются на один дальше
            for (int j = 0; j < alphabet.length; j++) if (alphabet[j] <= previous && j != pos) alphabet[j] += 1;
        }
        return code.toString();
    }

    public static String decode(String encoded) {
        StringBuilder decoded = new StringBuilder();
        char[] encoded_arr = encoded.toCharArray();

        StringBuilder current = new StringBuilder();

        // nk - сложность, k - размер алфавита
        for (int i = 0; i < encoded_arr.length; i++) {
            if (encoded_arr[i] == '.') {
                int pos = Integer.parseInt(current.toString());
                int previous = alphabet[pos];
                decoded.append(a[pos]);

                alphabet[pos] = 0;
                for (int j = 0; j < alphabet.length; j++) if (alphabet[j] <= previous && j != pos) alphabet[j] += 1;
                current.setLength(0);
            } else {
                current.append(encoded_arr[i]);
            }
        }

        return decoded.toString();
    }

}
