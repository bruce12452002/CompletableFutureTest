import java.util.concurrent.*;

public class EitherTest {
    private static final ExecutorService ES = Executors.newFixedThreadPool(10);

    /**
     * 兩個執行緒其中一個完成就往下執行
     * CompletableFuture 有實現 CompletionStage，所以這個參數傳第二個執行緒
     * 和上面三個方法一樣，參數都是上兩個執行緒回傳的結果，回傳值是自己回傳的結果
     * 如果有要取得上一個執行緒的結果，兩個執行緒的回傳值和泛型必需相同，
     * 以這三個方法來說，就只有 runAfterEither 可以回傳值不同
     * <p>
     * runAfterEither(CompletionStage, Runnable)，無參無回傳值，無法取得兩個執行緒的結果
     * acceptEither(CompletionStage, BiConsumer)，有兩個參數，無回傳值，可取得兩個執行緒的結果
     * applyToEither(CompletionStage, BiFunction) 有參有回傳值，可取得兩個執行緒的結果，也可回傳自己的結果
     * 以上都有相應的 xxxAsync 方法，然後再分成有沒有 Executor 參數
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EitherTest et = new EitherTest();
//        et.runAfterEitherTest();
//        et.acceptEitherTest();
        et.applyToEitherTest();

        if (!ES.isShutdown()) ES.shutdown();
    }

    private void runAfterEitherTest() {
        Util.printStart();

        getThread1().runAfterEither(getThread2(), () -> {
            System.out.println("yeah");
            sleep();
        });

        // 以上都執行完才會往下執行
        Util.printEnd();
    }

    private void acceptEitherTest() {
        Util.printStart();

        getThread1().acceptEither(getThread2(), t -> { // 因為只要其中一個有結果就往下執行，所以只需一個參數，而且兩個執行緒的回傳值必需相同
            System.out.println("t=" + t);
            sleep();
        });

        // 以上都執行完才會往下執行
        Util.printEnd();
    }

    private void applyToEitherTest() throws ExecutionException, InterruptedException {
        Util.printStart();

        CompletableFuture<Integer> result = getThread1().applyToEither(getThread2(), t -> {
            System.out.println("t=" + t);
            sleep();
            return t + t;
        });

        // 以上都執行完才會往下執行
        System.out.println("result=" + result.get());
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
            sleep();
            int i = 456;
            System.out.println("i=" + i);
            return i;
        }, ES);
    }

    private CompletableFuture<Integer> getThread2() {
        return CompletableFuture.supplyAsync(() -> {
            int j = 789;
            System.out.println("j=" + j);
            return j;
        }, ES);
    }
}
