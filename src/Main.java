import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static AtomicInteger threeLetters = new AtomicInteger();
    public static AtomicInteger fourLetters = new AtomicInteger();
    public static AtomicInteger fiveLetters = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService =
                Executors.newFixedThreadPool(3);//Runtime.getRuntime().availableProcessors());


        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        Thread sortedLetters = new Thread(() -> {
            for (String text : texts) {
                String[] results = text.split("(?<=\\G.{1})");
                String res = Arrays.stream(results).sorted(Comparator.naturalOrder()).collect(Collectors.joining());

                if (text.equals(res)) {
                    letsAdd(text);
                }
            }
        });

        Thread allLettersEquales = new Thread(() -> {
            for (String text : texts) {
                String[] results = text.split("(?<=\\G.{1})");
                boolean allEquales = true;
                for (String i : results) {
                    if (!i.equals(results[0])) {
                        allEquales = false;
                    }
                }

                if (allEquales) {
                    letsAdd(text);
                }
            }
        });

        Thread mirror = new Thread(() -> {
            for (String text : texts) {
                if (text.equals(new StringBuilder(text).reverse().toString())){
                    letsAdd(text);
                }
            }
        });

        executorService.submit(sortedLetters);
        executorService.submit(allLettersEquales);
        executorService.submit(mirror);

        Collection<Future<?>> futures = new LinkedList<>();
        futures.add(executorService.submit(sortedLetters));
        futures.add(executorService.submit(allLettersEquales));
        futures.add(executorService.submit(mirror));
        for (Future<?> future : futures) {
            future.get();
        }
        executorService.shutdown();

        //sortedLetters.join();
        //allLettersEquales.join();
        //mirror.join();
        System.out.println(threeLetters);
        System.out.println(fourLetters);
        System.out.println(fiveLetters);

    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void letsAdd(String text){
        if (text.length() == 3) {
            threeLetters.addAndGet(1);
        } else if (text.length() == 4) {
            fourLetters.addAndGet(1);
        } else if (text.length() == 5) {
            fiveLetters.addAndGet(1);
        }
    }


}
