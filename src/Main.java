import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        class ProcessText implements Callable {
            String text;

            public ProcessText(String text) {
                this.text = text;
            }

            public Object call() throws Exception {
                int maxSize = 0;

                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return this;
            }
        }

        long startTs = System.currentTimeMillis(); // start time

        List<Future> threads = new ArrayList<>();

        ExecutorService threadPool = Executors.newFixedThreadPool(4);

        for(int i=0; i<25; i++) {
            Callable callable = new ProcessText(texts[i]);
            threads.add( threadPool.submit(callable));
        }

        for(Future thr : threads) {
            Future task = thr;
            task.get();
        }
        threadPool.shutdown();

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}