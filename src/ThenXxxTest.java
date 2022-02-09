import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThenXxxTest {
    private static final ExecutorService ES = Executors.newFixedThreadPool(10);

    /**
     * thenRun 無參無回傳值，直接執行，不需要上一個執行緒的結果
     * thenAccept 有參無回傳值，參數為上一個執行緒的結果
     * thenApply 有參有回傳值，可讓下一個執行緒知道返回的結果
     * 以上都有相應的 xxxAsync 方法
     */
    public static void main(String[] args) {
        ThenXxxTest tx = new ThenXxxTest();
        tx.tenRunTest();

        if (!ES.isShutdown()) ES.shutdown();
    }

    private void tenRunTest() {

    }

    private void tenAcceptTest() {

    }

    private void tenApplyTest() {
    }

}
