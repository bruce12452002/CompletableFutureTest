import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
兩種創建 CompletableFuture 的方式，第二個參數是可選的 Executor
    CompletableFuture.runAsync(Runnable [,Executor]); // 沒回傳值
    CompletableFuture.supplyAsync(Supplier<U> [,Executor]); // 有回傳值
*/
public class CompletableFutureTest {
    private static final ExecutorService ES = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFutureTest cf = new CompletableFutureTest();
//        cf.initAsync();
//        cf.initSupplyAsync();
//        cf.completeTest();
//        cf.completeExceptionallyTest();
//        cf.whenCompleteTest();
//        cf.exceptionallyTest();
        cf.handleTest();

        if (!ES.isShutdown()) ES.shutdown();
    }

    private void initAsync() {
        Util.printStart();
        CompletableFuture.runAsync(() -> {
            System.out.println("thread id=" + Thread.currentThread().getId());
        }, ES);
        Util.printEnd();
        // 包起來的 CompletableFuture.runAsync 是另外一個執行緒，如果沒有調用阻塞方法，通常執會執行 start 後 end，最後才是裡面的方法
    }

    private void initSupplyAsync() throws ExecutionException, InterruptedException {
        Util.printStart();
        CompletableFuture<Long> rtn = CompletableFuture.supplyAsync(() -> {
            long id = Thread.currentThread().getId();
            System.out.println("thread id=" + id);
            return id;
        }, ES);
        System.out.println("rtn=" + rtn.get()); // get() 是阻塞方法，加了這行會等到前面都執行完才會往下執行
        Util.printEnd();
        // 包起來的 CompletableFuture.supplyAsync 是另外一個執行緒，如果沒有調用阻塞方法，通常執會執行 start 後 end，最後才是裡面的方法
    }

    private void completeTest() throws ExecutionException, InterruptedException {
        // complete 為某個執行緒執行完之後，取代執行緒的結果

        Util.printStart();
        CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> "complete test", ES);
        // result.get(); // 這行不可在 complete 之前，會使 complete 失效
        boolean complete = result.complete("finish!");
        System.out.println(result.get()); // get 或 join 都可以
        Util.printEnd();
    }

    private void completeExceptionallyTest() {
        Util.printStart();
        CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> "complete test", ES);
        try {
//            result.get();
            int i = 1 / 0;

            boolean complete = result.complete("finish!");
            System.out.println(result.get()); // get 或 join 都可以
        } catch (Exception e) {
            // 發生例外會執行，如果 result 有取到結果會是 false；沒取到是 true
            System.out.println(result.completeExceptionally(e));
        }
        Util.printEnd();
    }

    private void whenCompleteTest() throws ExecutionException, InterruptedException {
        // whenComplete 為某個執行緒執行完之後才執行，只能知道有沒有異常，不能修改回傳值

        Util.printStart();
        CompletableFuture<Long> rtn = CompletableFuture.supplyAsync(() -> {
            long id = Thread.currentThread().getId();
            System.out.println("thread id=" + id);
//            int i = 1 / 0;
            return id;
        }, ES).whenComplete((res, ex) -> { // whenComplete 使用 main 執行緒；whenCompleteAsync 用新的執行緒
            System.out.println("內部結果為=" + res);
            System.out.println("例外=" + ex);
        });

        // 如果有例外，以下程式碼不會執行
        System.out.println("外部結果為=" + rtn.get()); // get() 是阻塞方法，加了這行會等到前面都執行完才會往下執行
        Util.printEnd();
    }

    private void exceptionallyTest() throws ExecutionException, InterruptedException {
        // 如果有例外就會執行 exceptionally，可修改回傳值

        Util.printStart();
        CompletableFuture<Long> rtn = CompletableFuture.supplyAsync(() -> {
            long id = Thread.currentThread().getId();
            System.out.println("thread id=" + id);
//            int i = 1 / 0;
            return id;
        }, ES).whenCompleteAsync((res, ex) -> { // whenComplete 使用 main 執行緒；whenCompleteAsync 用新的執行緒
            System.out.println("內部結果為=" + res);
            System.out.println("例外=" + ex);
        }).exceptionally(t -> { // 沒有例外不會執行
            System.out.println("exceptionally 例外=" + t);
            return 999L;
        });
        // 因為有 exceptionally，如果有例外，以下程式碼還是會執行
        System.out.println("外部結果為=" + rtn.get()); // get() 是阻塞方法，加了這行會等到前面都執行完才會往下執行
        Util.printEnd();
    }

    private void handleTest() throws ExecutionException, InterruptedException {
        // handle 是 whenComplete + exceptionally，不管有沒有例外，都可修改回傳值

        Util.printStart();
        CompletableFuture<Long> rtn = CompletableFuture.supplyAsync(() -> {
            long id = Thread.currentThread().getId();
            System.out.println("thread id=" + id);
            int i = 1 / 0;
            return id;
        }, ES).handleAsync((res, ex) -> { // handle 使用 main 執行緒；handleAsync 用新的執行緒
            System.out.println("handle thread id=" + Thread.currentThread().getId());
            System.out.println("內部結果為=" + res);
            System.out.println("例外=" + ex);
            return 999L;
        });
        System.out.println("外部結果為=" + rtn.get()); // get() 是阻塞方法，加了這行會等到前面都執行完才會往下執行
        Util.printEnd();
    }
}
