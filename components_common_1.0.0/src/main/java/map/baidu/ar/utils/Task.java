package map.baidu.ar.utils;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * 异步框架
 */
public class Task {

    private static HandlerThread mBackThread = null;

    static synchronized HandlerThread getBackThread() {
        if (mBackThread == null) {
//            synchronized (Task.class) {
                if (mBackThread == null) {
                    mBackThread = new HandlerThread("BackgroundTask");
                    mBackThread.start();
                }
//            }
        }
        return mBackThread;
    }

    /**
     * 后台执行
     *
     * @param runnable 用来执行的Runnable
     *
     * @return 返回Task
     */
    public static InnerTask back(Runnable runnable) {
        return new InnerTask(runnable);
    }

    public static class InnerTask {
        private final ArrayList<BaseTask> mTasks = new ArrayList<>();

        private final Executor mExecutor = new Executor(mTasks);

        public InnerTask( Runnable runnable) {
            back(runnable);
        }

        /**
         * 前台执行
         *
         * @param runnable 执行的Runnable
         *
         * @return 返回的Task
         */

        public InnerTask ui( Runnable runnable) {
            mTasks.add(new UiTask(runnable, mExecutor));
            return this;
        }

        /**
         * 后台执行
         *
         * @param runnable 执行的Runnable
         *
         * @return 返回的Task
         */

        public InnerTask back( Runnable runnable) {
            mTasks.add(new BackTask(runnable, mExecutor));
            return this;
        }

        /**
         * 开始执行
         */
        public void run() {
            mExecutor.run();
        }
    }

    private static final class Executor {
        private final List<BaseTask> mTasks;

        private Executor(List<BaseTask> tasks) {
            mTasks = tasks;
        }

        public void run() {
            BaseTask task = ListUtils.getItem(mTasks, 0);
            if (task != null) {
                task.run();
            }
        }

        public void runNext() {
            ListUtils.remove(mTasks, 0);
            run();
        }
    }

    private abstract static class BaseTask {

        private final Runnable mRunnable;

        private final Executor mExecutor;

        protected BaseTask( Runnable runnable,  Executor executor) {
            mRunnable = runnable;
            mExecutor = executor;
        }

        protected abstract Looper getLooper();

        public void run() {
            Handler handler = new Handler(getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        mRunnable.run();
                        mExecutor.runNext();
                    } catch (Throwable e) {
                        // 这个地方将所有Task内部执行的同步异步方法都catch了，然后用Assert对外抛出信息。
                        e.printStackTrace();
                    }
                }
            };
            Message message = handler.obtainMessage();
            if (message != null) {
                message.sendToTarget();
            }
        }
    }

    private static final class BackTask extends BaseTask {
        private BackTask( Runnable runnable,  Executor executor) {
            super(runnable, executor);
        }

        @Override
        protected Looper getLooper() {
            return Task.getBackThread().getLooper();
        }
    }

    private static final class UiTask extends BaseTask {
        private UiTask( Runnable runnable,  Executor executor) {
            super(runnable, executor);
        }

        @Override
        protected Looper getLooper() {
            return Looper.getMainLooper();
        }
    }
}
