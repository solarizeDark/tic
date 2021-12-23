import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

class Pair {

    public String s;
    public Integer x;

    public Pair() {}

}

public class BWT {

    // бинпоиск переписан так, чтобы возвращать индекс самой первой
    // буквы из одинаковых
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

        // оригинальная строка
        sorted.put(initial, 0);

        // сдвиги добавляются в красно черное дерево
        for (int i = 1; i < initial.length(); i++) {
            String shifted = initial.substring(i) + initial.substring(0, i);
            sorted.put(shifted, i);
        }

        StringBuilder transformed = new StringBuilder();
        int pos = -1;
        int i = 0;

        // идем по дереву в лексикографическом порядке по ключам берем последние буквы строк
        // если встретили оригинальную строку, запоминаем ее номер
        for (String s = sorted.firstKey(); !s.equals(sorted.lastKey()); s = sorted.higherKey(s)) {
            if (s.equals(initial)) pos = i;
            i++;
            transformed.append(s.substring(s.length() - 1));
        }
        transformed.append(sorted.lastKey().substring(sorted.lastKey().length() - 1));

        // оригинал оказался последним
        if (pos == -1) pos = i;

        Pair p = new Pair();
        p.s = transformed.toString();
        p.x = pos;
        return p;
    }

    public static String decode(Pair pair) {
        char[] symbols = pair.s.toCharArray();
        Arrays.sort(symbols);

        // индекс символа в алфавите - индексы строки
        HashMap<Integer, Integer> mi = new HashMap<>();

        // индекс символа в алфавите - символ
        HashMap<Integer, Character> mc = new HashMap<>();

        // символ - сколько раз данный символ встретился в строке
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

        // позиция, на которой оказалась оригинальная строка
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
            mes.append('\n');
        }

        Pair transformed = transform(String.valueOf(mes));
        MoveToFront.setAlphabet();
        String encoded = MoveToFront.encode(transformed.s);
        MoveToFront.setAlphabet();
        String decoded = MoveToFront.decode(encoded);

        Pair p = new Pair();
        p.x = transformed.x;
        p.s = decoded;

        decoded = decode(p);

        File decodedFile = new File("BWT_MTF_resources/decoded.txt");
        FileWriter writer = new FileWriter(decodedFile);
        writer.write(decoded);
        writer.flush();
        writer.close();

    }

}
