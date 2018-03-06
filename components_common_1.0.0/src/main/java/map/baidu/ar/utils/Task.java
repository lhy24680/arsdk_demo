package map.baidu.ar.utils;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * 异步框架
 * Created by 享 on 2016/3/11.
 */
public class Task {

    private static HandlerThread mBackThread;

    static HandlerThread getBackThread() {
        if (mBackThread == null) {
            synchronized (Task.class) {
                if (mBackThread == null) {
                    mBackThread = new HandlerThread("BackgroundTask");
                    mBackThread.start();
                }
            }
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
    @NotNull
    public static InnerTask back(@NotNull Runnable runnable) {
        return new InnerTask(runnable);
    }

    public static class InnerTask {
        @NotNull
        private final ArrayList<BaseTask> mTasks = new ArrayList<>();
        @NotNull
        private final Executor mExecutor = new Executor(mTasks);

        public InnerTask(@NotNull Runnable runnable) {
            back(runnable);
        }

        /**
         * 前台执行
         *
         * @param runnable 执行的Runnable
         *
         * @return 返回的Task
         */
        @NotNull
        public InnerTask ui(@NotNull Runnable runnable) {
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
        @NotNull
        public InnerTask back(@NotNull Runnable runnable) {
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
        @NotNull
        private final Runnable mRunnable;
        @NotNull
        private final Executor mExecutor;

        protected BaseTask(@NotNull Runnable runnable, @NotNull Executor executor) {
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
        private BackTask(@NotNull Runnable runnable, @NotNull Executor executor) {
            super(runnable, executor);
        }

        @Override
        protected Looper getLooper() {
            return Task.getBackThread().getLooper();
        }
    }

    private static final class UiTask extends BaseTask {
        private UiTask(@NotNull Runnable runnable, @NotNull Executor executor) {
            super(runnable, executor);
        }

        @Override
        protected Looper getLooper() {
            return Looper.getMainLooper();
        }
    }
}
