import java.util.concurrent.*;

public class BothTest {
    private static final ExecutorService ES = Executors.newFixedThreadPool(10);

    /**
     * 兩個執行緒完成才能往下執行
     * CompletableFuture 有實現 CompletionStage，所以這個參數傳第二個執行緒
     * 和上面三個方法一樣，參數都是上兩個執行緒回傳的結果，回傳值是自己回傳的結果
     * <p>
     * runAfterBoth(CompletionStage, Runnable)，無參無回傳值，無法取得兩個執行緒的結果
     * thenAcceptBoth(CompletionStage, BiConsumer)，有兩個參數，無回傳值，可取得兩個執行緒的結果
     * thenCombine(CompletionStage, BiFunction) 有參有回傳值，可取得兩個執行緒的結果，也可回傳自己的結果
     * 以上都有相應的 xxxAsync 方法，然後再分成有沒有 Executor 參數
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        BothTest tx = new BothTest();
//        tx.runAfterBothTest();
//        tx.thenAcceptBothTest();
        tx.thenCombineTest();

        if (!ES.isShutdown()) ES.shutdown();
    }

    private void runAfterBothTest() {
        Util.printStart();

        getThread1().runAfterBoth(getThread2(), () -> {
            System.out.println("yeah");
            sleep();
        });

        // 以上都執行完才會往下執行
        Util.printEnd();
    }

    private void thenAcceptBothTest() {
        Util.printStart();

        getThread1().thenAcceptBoth(getThread2(), (t1, t2) -> {
            System.out.println("t1=" + t1);
            System.out.println("t2=" + t2);
            sleep();
        });

        // 以上都執行完才會往下執行
        Util.printEnd();
    }

    private void thenCombineTest() throws ExecutionException, InterruptedException {
        Util.printStart();

        CompletableFuture<String> result = getThread1().thenCombine(getThread2(), (t1, t2) -> {
            System.out.println("t1=" + t1);
            System.out.println("t2=" + t2);
            sleep();
            return t1 + t2;
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

    private CompletableFuture<Integer> getThread1() {
        return CompletableFuture.supplyAsync(() -> {
            int i = 123;
            System.out.println("i=" + i);
            return i;
        }, ES);
    }

    private CompletableFuture<String> getThread2() {
        return CompletableFuture.supplyAsync(() -> {
            String str = "xxx";
            System.out.println("str=" + str);
            return str;
        }, ES);
    }
}
