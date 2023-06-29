package su.puzzle.pay.api;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AsyncTasksService {
    protected ExecutorService threadpool;
    protected LinkedList<AsyncTask> tasks = new LinkedList<AsyncTask>();
    protected LinkedList<AsyncTask> newTasks = new LinkedList<AsyncTask>();

    public AsyncTasksService(ExecutorService threadpool) {
        this.threadpool = threadpool;
    }

    public void addTask(Task task, TaskCallback callback, TaskExceptionCallback exceptionCallback) {
        Future<Object> future = threadpool.submit(() -> { return task.run();} );
        newTasks.add(new AsyncTask(future, callback, exceptionCallback));
    }

    public void updateTasks() {
        tasks.addAll(newTasks);
        newTasks.clear();

        for (AsyncTask task : tasks) {
            if (!task.isDone()) continue;

            try {
                task.callback().onEnd(task.task().get());
            } catch (ExecutionException e) {
                e.printStackTrace();
                task.exceptionCallback().onEnd((Exception) e.getCause());
            } catch (Exception e) {
                e.printStackTrace();
                task.exceptionCallback().onEnd(e);
            }
        }
    }

    public void removeDone() {
        tasks.removeIf((task) -> task.isDone());
    }

    protected record AsyncTask(Future<Object> task, TaskCallback callback, TaskExceptionCallback exceptionCallback) {
        public boolean isDone() {
            return task.isDone();
        }
    };

    public interface Task {
        public Object run() throws Exception;
    }

    public interface TaskCallback {
        public void onEnd(Object result);
    }

    public interface TaskExceptionCallback {
        public void onEnd(Exception e);
    }
}
