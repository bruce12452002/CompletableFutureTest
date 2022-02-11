import java.util.concurrent.*;

public class NextTest {
    private static final ExecutorService ES = Executors.newFixedThreadPool(10);

    /**
     * 一個執行緒完成後，接著往下執行
     * thenRun 無參無回傳值，直接執行，不需要上一個執行緒的結果
     * thenAccept 有參無回傳值，參數為上一個執行緒的結果
     * thenApply 有參有回傳值，可讓下一個執行緒知道返回的結果
     * thenCompose 和 thenApply 很像，差在 thenCompose 要繼續回傳 CompletableFuture，類似 map 和 flatMap
     * 以上都有相應的 xxxAsync 方法，然後再分成有沒有 Executor 參數
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NextTest nt = new NextTest();
//        nt.thenRunTest();
//        nt.thenAcceptTest();
//        nt.thenApplyTest();
//        nt.thenApplyAcceptRunTest();
        nt.thenComposeTest();

        if (!ES.isShutdown()) ES.shutdown();
    }

    private void thenRunTest() {
        Util.printStart();
        CompletableFuture.supplyAsync(() -> {
            long id = Thread.currentThread().getId();
            System.out.println("thread id=" + id);
            return id;
        }, ES).thenRun(() -> {
            System.out.println("thenRun thread id=" + Thread.currentThread().getId());
            sleep();
        });
        // 以上都執行完才會往下執行
        Util.printEnd();
    }

    private void thenAcceptTest() {
        Util.printStart();
        CompletableFuture.supplyAsync(() -> {
            long id = Thread.currentThread().getId();
            System.out.println("thread id=" + id);
            return id;
        }, ES).thenAccept(lastResult -> {
            System.out.println("lastResult=" + lastResult);
            System.out.println("thenAccept thread id=" + Thread.currentThread().getId());
            sleep();
        });
        // 以上都執行完才會往下執行
        Util.printEnd();
    }

    private void thenApplyTest() throws ExecutionException, InterruptedException {
        Util.printStart();
        CompletableFuture<Integer> result = CompletableFuture.supplyAsync(() -> {
            long id = Thread.currentThread().getId();
            System.out.println("thread id=" + id);
            return id;
        }, ES).thenApply(lastResult -> {
            System.out.println("lastResult=" + lastResult);
            System.out.println("thenApply thread id=" + Thread.currentThread().getId());
            sleep();
            return 333;
        });
        // 以上都執行完才會往下執行
        System.out.println(result.get());
        Util.printEnd();
    }

    private void thenApplyAcceptRunTest() {
        Util.printStart();
        CompletableFuture.supplyAsync(() -> {
            long id = Thread.currentThread().getId();
            System.out.println("thread id=" + id);
            return id;
        }, ES).thenApply(lastResult -> {
            System.out.println("lastResult1=" + lastResult);
            System.out.println("thenApply thread id=" + Thread.currentThread().getId());
            sleep();
            return 333;
        }).thenAccept(lastResult -> {
            System.out.println("lastResult2=" + lastResult);
            System.out.println("thenAccept thread id=" + Thread.currentThread().getId());
            sleep();
        }).thenRun(() -> {
            System.out.println("thenRun thread id=" + Thread.currentThread().getId());
            sleep();
        });
        // 每個 thenXxx 方法都做完才會到下一個 thenXxx 方法，以上都執行完才會往下執行
        Util.printEnd();
    }

    private void thenComposeTest() throws ExecutionException, InterruptedException {
        Util.printStart();
        CompletableFuture<Integer> result = CompletableFuture.supplyAsync(() -> {
            long id = Thread.currentThread().getId();
            System.out.println("thread id=" + id);
            return id;
        }, ES).thenCompose(lastResult -> {
            System.out.println("lastResult=" + lastResult);
            System.out.println("thenCompose thread id=" + Thread.currentThread().getId());
            sleep();
            return CompletableFuture.supplyAsync(() -> 333);
        });
        // 以上都執行完才會往下執行
        System.out.println(result.get());
        Util.printEnd();
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
