import java.math.BigDecimal;
import java.util.concurrent.*;

public class XxOfTest {
    private static final ExecutorService ES = Executors.newFixedThreadPool(10);

    /**
     * 0~多個執行緒一起執行管理
     * allOf：全部執行緒完成才能繼續執行
     * anyOf：其中一個執行緒完成就可以繼續執行
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        XxOfTest xt = new XxOfTest();
        xt.allOfTest();
//        xt.anyOfTest();

        if (!ES.isShutdown()) ES.shutdown();
    }

    private void allOfTest() throws ExecutionException, InterruptedException {
        Util.printStart();

        CompletableFuture<Integer> thread1 = getThread1();
        CompletableFuture<Void> result = CompletableFuture.allOf(thread1, getThread2(), getThread3());
        System.out.println("a");
        result.get(); // get 或 join 會等所有執行緒執行完才會繼續執行
        System.out.println(thread1.get()); // thread1 的阻塞方法
        Util.printEnd();
    }

    private void anyOfTest() throws ExecutionException, InterruptedException {
        Util.printStart();

        CompletableFuture<Object> result = CompletableFuture.anyOf(getThread1(), getThread2(), getThread3());
        System.out.println("a");
        result.join(); // get 或 join 有其中一個執行緒執行完就會繼續執行
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
            sleep();
            System.out.println("getThread1=" + i);
            return i;
        }, ES);
    }

    private CompletableFuture<String> getThread2() {
        return CompletableFuture.supplyAsync(() -> {
            String str = "xxx";
            sleep();
            sleep();
            System.out.println("getThread2=" + str);
            return str;
        }, ES);
    }

    private CompletableFuture<BigDecimal> getThread3() {
        return CompletableFuture.supplyAsync(() -> {
            BigDecimal bg = BigDecimal.TEN;
            sleep();
            sleep();
            sleep();
            System.out.println("getThread3=" + bg);
            return bg;
        }, ES);
    }
}
