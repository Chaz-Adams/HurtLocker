import org.apache.commons.io.IOUtils;

import java.io.FileWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static String readRawDataToString() throws Exception {
        ClassLoader classLoader = Main.class.getClassLoader();
        String result = IOUtils.toString(classLoader.getResourceAsStream("RawData.txt"));
        return result;
    }

    public static void writeRawDataToFile() {
        try {
            FileWriter myWriter = new FileWriter("outputFormat.txt");
            myWriter.write(readRawDataToString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String[] splitRawData() throws Exception {
        String[] strings = readRawDataToString().split("[##]");

        //String[] splitStrings = readRawDataToString().split("[:@^*%;#]");
//        for(String s : splitStrings){
//            System.out.println(s);
//        }
        return strings;

    }

    public static void patternMatch() throws Exception {
//        String[] strings = splitRawData();
//        ArrayList<String> test = new ArrayList<>();
//        for (String s : strings){
//            test.add(s);
//        }


        //for (int i = 0; i < test.length; i++) {
            //Pattern pattern = Pattern.compile(test);
            Pattern pattern = Pattern.compile("name:([^;]+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(readRawDataToString());

            Integer count = 0;
            String group1 = "";
            while (matcher.find()) {
                group1 = matcher.group(0);
                count++;
            }
            System.out.println("Name:\t"+group1 + "\t\tseen: " + count + " times");
            System.out.println("============= \t \t =============");


        //}

    }

//    public static HashMap<String, String> matchRegexPairs(String input, String keyRegex, String valueRegex) {
//        HashMap<String, String> resultMap = new HashMap<>();
//
//        Pattern keyPattern = Pattern.compile(keyRegex);
//        Pattern valuePattern = Pattern.compile(valueRegex);
//
//        Matcher keyMatcher = keyPattern.matcher(input);
//        Matcher valueMatcher = valuePattern.matcher(input);
//
//        while (keyMatcher.find() && valueMatcher.find()) {
//            String key = keyMatcher.group();
//            String value = valueMatcher.group();
//            resultMap.put(key, value);
//        }
//
//        return resultMap;
//    }

    public static void match() throws Exception {
        String input = readRawDataToString();

        Map<String, Integer> wordCounts = new HashMap<>();

        Pattern pattern = Pattern.compile(":\\s*([a-zA-Z]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String word = matcher.group(1);
            wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }

        // Printing the word counts
        for (String word : wordCounts.keySet()) {
            int count = wordCounts.get(word);
            System.out.println(word + ": " + count);
        }
    }

    public static void main(String[] args) throws Exception{
        String data = readRawDataToString();
        Map<String, Integer> itemCounts = new HashMap<>();
        Map<String, Integer> itemSeenCounts = new HashMap<>();
        Map<String, Map<Double, Integer>> itemPrices = new HashMap<>();
        int errorCount = 0;

        Pattern pattern = Pattern.compile("naMe:(Milk|Bread|C[o0][o0]kies|apPles);price:(\\d+\\.\\d+);",Pattern.CASE_INSENSITIVE );
        Matcher matcher = pattern.matcher(data);

        while (matcher.find()) {
            String itemName = matcher.group(1).toLowerCase();
            double price = Double.parseDouble(matcher.group(2));

            // Update item count
            itemCounts.put(itemName, itemCounts.getOrDefault(itemName, 0) + 1);

            // Update seen count for this specific item
            itemSeenCounts.put(itemName, itemSeenCounts.getOrDefault(itemName, 0) + 1);

            // Store the count of each price for each item
            itemPrices.computeIfAbsent(itemName, k -> new HashMap<>())
                    .put(price, itemSeenCounts.get(itemName));
        }

        // Count errors
        errorCount += countErrors(data);

        // Print counts and unique prices
        printItemInfo("Milk", itemCounts.getOrDefault("milk", 0), itemPrices.getOrDefault("milk", new HashMap<>()));
        printItemInfo("Bread", itemCounts.getOrDefault("bread", 0), itemPrices.getOrDefault("bread", new HashMap<>()));
        printItemInfo("Cookies", itemCounts.getOrDefault("cookies", 0), itemPrices.getOrDefault("cookies", new HashMap<>()));
        printItemInfo("Apples", itemCounts.getOrDefault("apples", 0), itemPrices.getOrDefault("apples", new HashMap<>()));

        // Print error count
        System.out.println("Errors\t\t\t\t seen: " + errorCount + " times");
    }

    public static void printItemInfo(String itemName, int totalCount, Map<Double, Integer> prices) {
        System.out.println("name:   " + itemName + "\t\t seen: " + totalCount + " times");
        System.out.println("=============\t\t =============");
        prices.forEach((price, seenCount) -> {
            System.out.println("Price:   " + price + "\t\t seen: " + seenCount + " times");
            System.out.println("-------------\t\t -------------");
        });
    }

    public static int countErrors(String data) {
        int errorCount = 0;
        Pattern errorPattern = Pattern.compile("name:\\s*([^;\\s]+)?\\s*;price:\\s*([^;\\s]+)?\\s*;type:\\s*([^;\\s]+)?\\s*expiration:\\s*([^;\\s]+)?\\s*");
        Matcher errorMatcher = errorPattern.matcher(data);
        while (errorMatcher.find()) {
            if (errorMatcher.group(1) == null || errorMatcher.group(1).isEmpty() ||
                    errorMatcher.group(2) == null || errorMatcher.group(2).isEmpty() ||
                    errorMatcher.group(3) == null || errorMatcher.group(3).isEmpty() ||
                    errorMatcher.group(4) == null || errorMatcher.group(4).isEmpty()) {
                errorCount++;
            }
        }
        return errorCount;
    }




}

