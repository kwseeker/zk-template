package top.kwseeker.zk.abc.official;


import org.testng.annotations.Test;

/**
 * 守护者线程不要放到 @Test 方法测试，否则会看到让人疑惑的效果
 *  单元测试中主线程退出，非守护者子线程也会退出（初学时曾经被这个测出的结果误导了好久）
 *  ！！！junit / testng 测试时@test方法执行完就会终止所有用户线程。
 *  不清楚其他单元测试框架是不是也是这样，估计是一样的，测试线程测完本就应该释放掉
 *
 * 主线程等所有非守护子线程退出才会退出，
 * 主线线程退出守护者线程也会退出
 */
public class DaemonThreadTest {

    //testng框架 也是“@test方法执行完就会终止所有用户线程”
    @Test
    public void test() throws InterruptedException {
        new Thread(() -> {
            while (true) {
                System.out.println("common thread ...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(1000);
        System.out.println("main thread existing ...");
    }

    public static void main(String[] args) throws InterruptedException {
        //new Thread(() -> {
        //    while (true) {
        //        System.out.println("common thread ...");
        //        try {
        //            Thread.sleep(1000);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
        //    }
        //}).start();

        Thread daemonThread = new Thread(() -> {
            while (true) {
                System.out.println("daemon thread ...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        daemonThread.setDaemon(true);
        daemonThread.start();

        Thread.sleep(1000);
        System.out.println("main thread existing ...");
    }
}
