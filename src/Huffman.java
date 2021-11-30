import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class Node {

    public char symbol;
    public double probability = 0;
    public Node left;
    public Node right;

}

public class Huffman {

    static Map<Character, String> codes;
    static Map<Character, Double> stats;

    // файл с кодами
    static String codesFile = "codes.txt";

    static char[] symbols;
    static Node root;

    static {
        stats = new HashMap<>();
        codes = new HashMap<>();
        root = new Node();
    }

    public static void encode(String prev, Node root) {

        if (root.left != null) {
            encode(prev.concat("0"), root.left);
        }
        if (root.right != null) {
            encode(prev.concat("1"), root.right);
        }
        if (root.left == null && root.right == null) {
            codes.put(root.symbol, prev);
            codes.put(root.symbol, prev);
        }

    }

    public static void readMessage(String messageFilePath) throws IOException {
        Path path = Paths.get(messageFilePath);

        byte[] data = Files.readAllBytes(path);
        symbols = new char[data.length];
        // O(n)
        for (int i = 0; i < symbols.length; i++) symbols[i] = (char)data[i];
    }

    public static void stats(char[] symbols) {
        // Подсчет частот для символов
        // O(n)
        Map<Character, Integer> counter = new HashMap<>();
        for (char symbol : symbols) {
            if (!counter.containsKey(symbol)) {
                counter.put(symbol, 1);
            } else {
                counter.put(symbol, counter.get(symbol) + 1);
            }
        }

        // Перевод частот в вероятности
        // O(n)
        for (Character symbol : counter.keySet()) {
            stats.put(symbol, (double) counter.get(symbol) / symbols.length);
        }
    }

    public static void coder() {
        // Создание красно-черного дерево <вероятность - список символов>
        // O(nlog(n))
        TreeMap<Double, List<Node>> nodes = new TreeMap<>();
        for (Character symbol : stats.keySet()) {
            double probability = stats.get(symbol);
            Node node = new Node();
            node.symbol = symbol;
            node.probability = probability;
            if (nodes.containsKey(probability)) { // O(log(n))
                nodes.get(probability).add(node); // O(const)
            } else {
                nodes.put(probability, new ArrayList<>(Arrays.asList(node))); // O(const)
            }
        }

        int cnt = 0;
        int cur = 0;
        Node temp = new Node();
        Double lowest = nodes.firstKey();
        // O(nlog(n))
        // Построение дерева кодов из красно черного
        while (nodes.higherKey(lowest) != null) {
            if (cnt % 2 == 0) {
                temp = new Node();
                temp.left = nodes.get(lowest).get(cur);
                temp.symbol = nodes.get(lowest).get(cur++).symbol;
                temp.probability += temp.left.probability;
            } else {
                temp.right = nodes.get(lowest).get(cur);
                temp.symbol = nodes.get(lowest).get(cur++).symbol;
                temp.probability += temp.right.probability;
                if (nodes.containsKey(temp.probability)) { // O(log(n))
                    nodes.get(temp.probability).add(temp); // O(log(n))
                } else {
                    nodes.put(temp.probability, new ArrayList<>(Arrays.asList(temp))); // O(log(n))
                }
            }
            if (cur == nodes.get(lowest).size()) {
                nodes.remove(lowest); // O(log(n))
                lowest = nodes.firstKey(); // O(const)
                cur = 0;
            }
            cnt++;
        }

        // создание корня
        {
            root.left = temp.left;
            root.right = nodes.lastEntry().getValue().get(0);
        }

        // Обход в глубину с построением кодов
        encode("", root);
    }

    private static void encodeFile(char[] symbols) throws IOException {
        File encodedFile = new File("encoded.txt");
        FileWriter writer = new FileWriter(encodedFile);

        for (char symbol : symbols) {
            writer.write(codes.get(symbol));
            writer.flush();
        }

        writer.close();
    }

    private static void writeCodes() throws IOException {
        File codesFile = new File(Huffman.codesFile);
        FileWriter writer = new FileWriter(codesFile);

        for (char symbol : codes.keySet()) {
            writer.write(symbol + " " + codes.get(symbol) + "\n");
            writer.flush();
        }

        writer.close();
    }

    public static void probabilitiesFromFile(String fileName) throws IOException {
        File statistics = new File(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(statistics));

        String line;
        while((line = reader.readLine()) != null) {
            stats.put(line.split(" ")[0].toCharArray()[0], Double.parseDouble(line.split(" ")[1]));
        }
    }

    public static void decoder(String fileEncoded, String fileProbabilities, String fileDecoded, boolean flag) throws IOException {
        readMessage(fileEncoded);
        if (flag) probabilitiesFromFile(fileProbabilities);
        coder();

        File codesFile = new File(fileDecoded);
        FileWriter writer = new FileWriter(codesFile);

        Node temp = root;
        int cnt = 0;
        while (cnt != symbols.length) {
            if (temp.left == null && temp.right == null) {
                writer.write(temp.symbol);
                writer.flush();
                temp = root;
            } else if (symbols[cnt++] == '0') temp = temp.left;
            else temp = temp.right;
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {

        if (args[0].compareTo("f") == 0) {
            // Считывание файла
            readMessage(args[1]);
            // Подсчет статистики
            stats(symbols);
            // Построение дерева кодов
            coder();
            // Кодирование файла
            encodeFile(symbols);
            // Запись таблицы <символ - код> в отдельный файл
            writeCodes();
            // Декодирование
            decoder("encoded.txt", "", "decoded.txt", false);
        } else {
            decoder(args[0], args[1], "decoded.txt", true);
            writeCodes();
        }


    }

}
