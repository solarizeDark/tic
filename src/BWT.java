import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

class Pair {

    public String s;
    public Integer x;

    public Pair() {}

    public Pair(String s, Integer x) {
        this.s = s;
        this.x = x;
    }

}

public class BWT {

    public static int binSearch(char[] arr, char key) {
        int l = 0, r = arr.length - 1;

        while (l <= r) {
            int mid = (l + r) / 2;
            int midVal = arr[mid];
            if (key > midVal) {
                l = mid + 1;
            } else if (key < midVal) {
                r = mid - 1;
            } else if (l != r) {
                r = mid - 1;
            } else return l;
        }

        return l;
    }

    public static Pair transform(String initial) {
        // сдвиг - изначальная позиция
        TreeMap<String, Integer> sorted = new TreeMap<>();
        sorted.put(initial, 0);
        for (int i = 1; i < initial.length(); i++) {
            String shifted = initial.substring(i) + initial.substring(0, i);
            sorted.put(shifted, i);
        }

        StringBuilder transformed = new StringBuilder();
        int pos = -1;
        int i = 0;
        for (String s = sorted.firstKey(); !s.equals(sorted.lastKey()); s = sorted.higherKey(s)) {
            if (s.equals(initial)) pos = i;
            i++;
            transformed.append(s.substring(s.length() - 1));
        }
        transformed.append(sorted.lastKey().substring(sorted.lastKey().length() - 1));
        if (pos == -1) pos = i;

        Pair p = new Pair();
        p.s = transformed.toString();
        p.x = pos;
        return p;
    }

    public static String decode(Pair pair) {
        char[] symbols = pair.s.toCharArray();
        Arrays.sort(symbols);

        // куда ведет - индексы
        HashMap<Integer, Integer> mi = new HashMap<>();

        // куда ведет - буквы
        HashMap<Integer, Character> mc = new HashMap<>();

        HashMap<Character, Integer> hm = new HashMap<>();
        int i = 0;
        for (char c : pair.s.toCharArray()) {
            int path;
            if (hm.containsKey(c)) {
                path = binSearch(symbols, c) + hm.get(c);
                mi.put(path, i++);
                hm.put(c, hm.get(c) + 1);
            } else {
                path = binSearch(symbols, c);
                mi.put(path, i++);
                hm.put(c, 1);
            }
            mc.put(path, c);
        }

        StringBuilder decoded = new StringBuilder();
        int next = pair.x;
        while (true) {
            decoded.append(mc.get(next));
            next = mi.get(next);
            if (next == pair.x) return decoded.toString();
        }

    }

    public static void main(String[] args) throws IOException {

        File message = new File("BWT_MTF_resources/message.txt");
        BufferedReader reader = new BufferedReader(new FileReader(message));
        StringBuilder mes = new StringBuilder("");
        String line;
        while((line = reader.readLine()) != null) {
            mes.append(line);
        }

        Pair transformed = transform(String.valueOf(mes));
        System.out.println(transformed.s);
        MoveToFront.setAlphabet();
        String encoded = MoveToFront.encode(transformed.s);
        System.out.println(encoded);
        MoveToFront.setAlphabet();
        String decoded = MoveToFront.decode(encoded);

        Pair p = new Pair();
        p.x = transformed.x;
        p.s = decoded;

        decoded = decode(p);
        System.out.println(decoded);

        File decodedFile = new File("BWT_MTF_resources/decoded.txt");
        FileWriter writer = new FileWriter(decodedFile);
        writer.write(decoded);
        writer.flush();
        writer.close();

    }

}
