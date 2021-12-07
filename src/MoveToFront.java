public class MoveToFront {

    static int[] alphabet;
    static char[] a;

    public static void setAlphabet() {
        alphabet = new int[94];
        a = new char[94];
        for (int i = 0; i < alphabet.length; i++) {
            alphabet[i] = i;
            a[i] = (char) (i + ' ');
        }
    }

    public static String encode(String fromBWT) {
        StringBuilder code = new StringBuilder();
        char[] bwtcode = fromBWT.toCharArray();

        for (int i = 0; i < bwtcode.length; i++) {
            // bwtcode[i] - ' ' - порядковый номер символа в алфавите
            int pos = bwtcode[i] - ' ';
            int previous = alphabet[pos];
            code.append(pos);
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
        int cnt = 0;
        for (int i = 0; ; i++) {

            try {
                // ascii interval
                if (Integer.parseInt(current.toString()) >= ' '
                        && Integer.parseInt(current.toString()) <= ' ' + 93) {

                    int pos = Integer.parseInt(current.toString());
                    int previous = alphabet[pos];
                    decoded.append((char) (pos + ' '));

                    alphabet[pos] = 0;
                    for (int j = 0; j < alphabet.length; j++) if (alphabet[j] <= previous && j != pos) alphabet[j] += 1;
                    current.setLength(0);
                }
                if (i >= encoded_arr.length) break;
                current.append(encoded_arr[i]);

            } catch (NumberFormatException e) {
                current.append(encoded_arr[i]);
            }

        }
        return decoded.toString();
    }


    public static void main(String[] args) {
        setAlphabet();
        String encoded = encode("rdakraaaabb");
        System.out.println(encoded);
        setAlphabet();
        System.out.println(decode(encoded));
    }

}
