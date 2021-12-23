import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Arithmetic {

    static char[] symbols;
    static Map<Character, BigDecimal> stats;

    static {
        stats = new HashMap<>();
    }

    public static void readMessage(String messageFilePath) throws IOException {
        Path path = Paths.get(messageFilePath);

        byte[] data = Files.readAllBytes(path);
        symbols = new char[data.length];
        // O(n)
        for (int i = 0; i < symbols.length; i++) symbols[i] = (char)data[i];
    }

    public static void statistics() {
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
            stats.put(symbol, BigDecimal.valueOf((double)counter.get(symbol) / symbols.length));
        }
    }

    public static void probabilitiesFromFile(String fileName) throws IOException {
        File statistics = new File(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(statistics));

        String line;
        while((line = reader.readLine()) != null) {
            stats.put(line.split(" ")[0].toCharArray()[0], new BigDecimal(line.split(" ")[1]));
        }
    }

    public static TreeMap<BigDecimal, List<Character>> statsReverse() {
        // Дерево вероятность - символы
        TreeMap<BigDecimal, List<Character>> statsR = new TreeMap<>();
        for (Character symbol : stats.keySet()) {
            if (!statsR.containsKey(stats.get(symbol))) {
                List<Character> temp = new ArrayList<>(Arrays.asList(symbol));
                statsR.put(stats.get(symbol), temp);
            } else {
                statsR.get(stats.get(symbol)).add(symbol);
            }

        }
        return statsR;
    }

    public static List<BigDecimal> bounds(TreeMap<BigDecimal, List<Character>> statsR) {
        List<BigDecimal> bounds = new ArrayList<>();
        bounds.add(BigDecimal.valueOf(0.0));
        BigDecimal current = statsR.lowerKey(BigDecimal.valueOf(1.0));
        BigDecimal cumulative_p = BigDecimal.valueOf(0);

        // Создание изначального списка границ
        // (n)
        for (;;) {

            for (int i = 0; i < statsR.get(current).size(); i++) {
                cumulative_p = cumulative_p.add(current);
                bounds.add(cumulative_p);
            }
            // log(n)
            if (statsR.lowerKey(current) == null) {
                break;
            } else {
                current = statsR.lowerKey(current);
            }

        }
        return bounds;
    }

    public static Map<Character, Integer> positionBySymbol(List<BigDecimal> bounds,
                                                           TreeMap<BigDecimal, List<Character>> statsR) {
        // Создание мапы символ - граница символа
        Map<Character, Integer> symbolPosition = new HashMap<>();
        int position = bounds.size() - 1;
        for (BigDecimal key : statsR.keySet())
            for (int i = 0; i < statsR.get(key).size(); i++)
                symbolPosition.put(statsR.get(key).get(i), --position);
        return symbolPosition;
    }

    public static Map<Integer, Character> symbolByPosition(List<BigDecimal> bounds,
                                                           TreeMap<BigDecimal, List<Character>> statsR) {
        // Создание мапы граница символа - символ
        Map<Integer, Character> symbolPosition = new HashMap<>();
        int position = bounds.size() - 1;
        for (BigDecimal key : statsR.keySet())
            for (int i = 0; i < statsR.get(key).size(); i++)
                symbolPosition.put(--position, statsR.get(key).get(i));
        return symbolPosition;
    }

    public static BigDecimal coder() {

        TreeMap<BigDecimal, List<Character>> statsR = statsReverse();
        List<BigDecimal> bounds = bounds(statsR);
        Map<Character, Integer> symbolPosition = positionBySymbol(bounds, statsR);

        List<BigDecimal> boundsInitial = new ArrayList<>(bounds);

        BigDecimal left, right;
        for (Character symbol : symbols) {

            left = bounds.get(symbolPosition.get(symbol));
            right = bounds.get(symbolPosition.get(symbol) + 1);
            bounds.set(0, left);
            bounds.set(bounds.size() - 1, right);

            BigDecimal distance = right.subtract(left);

            for (int i = 1; i < bounds.size() - 1; i++) {
                bounds.set(i, left.add(boundsInitial.get(i).multiply(distance)));
            }
        }
        return bounds.get(0).add(bounds.get(bounds.size() - 1).subtract(bounds.get(0)).divide(BigDecimal.valueOf(2)));
    }

    public static String decoder(BigDecimal code) {

        String decoded = "";

        TreeMap<BigDecimal, List<Character>> statsR = statsReverse();
        List<BigDecimal> bounds = bounds(statsR);
        Map<Integer, Character> symbolPosition = symbolByPosition(bounds, statsR);

        List<BigDecimal> boundsInitial = new ArrayList<>(bounds);

        BigDecimal left, right;
        while(true) {
            int j = 0;
            for(; code.compareTo(bounds.get(j)) > 0; j++);

            decoded += symbolPosition.get(j - 1);
            if (symbolPosition.get(j - 1) == '!') break;

            left = bounds.get(j - 1);
            right = bounds.get(j);
            bounds.set(0, left);
            bounds.set(bounds.size() - 1, right);

            BigDecimal distance = right.subtract(left);

            for (int i = 1; i < bounds.size() - 1; i++) {
                bounds.set(i, left.add(boundsInitial.get(i).multiply(distance)));
            }
        }

        return decoded;
    }

    public static void writeEncoded(BigDecimal res) throws IOException {
        File encodedFile = new File("arithmetic_encoding_resources/encoded.txt");
        FileWriter writer = new FileWriter(encodedFile);

        System.out.println(res);
        writer.write(String.valueOf(res));

        writer.close();
    }

    public static void main(String[] args) throws IOException {
        readMessage("arithmetic_encoding_resources/input.txt");
        statistics();
        BigDecimal res = coder();
        writeEncoded(res);

        FileWriter writer = new FileWriter("arithmetic_encoding_resources/decoded.txt");
        writer.write(decoder(res));
        writer.flush();
    }
}
