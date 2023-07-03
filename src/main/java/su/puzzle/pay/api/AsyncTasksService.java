package su.puzzle.pay.api;

import java.util.*;
import java.util.concurrent.*;

public class AsyncTasksService {
    protected ExecutorService threadpool;
    protected LinkedList<AsyncTask> tasks = new LinkedList<AsyncTask>();
    protected LinkedList<AsyncTask> newTasks = new LinkedList<AsyncTask>();

    public AsyncTasksService(ExecutorService threadpool) {
        this.threadpool = threadpool;
    }

    public void addTask(Task task, TaskCallback callback, TaskExceptionCallback exceptionCallback) {
        Future<Object> future = threadpool.submit(task::run);
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
        tasks.removeIf(AsyncTask::isDone);
    }

    public interface Task {
        Object run() throws Exception;
    }

    ;

    public interface TaskCallback {
        void onEnd(Object result);
    }

    public interface TaskExceptionCallback {
        void onEnd(Exception e);
    }

    protected record AsyncTask(Future<Object> task, TaskCallback callback, TaskExceptionCallback exceptionCallback) {
        public boolean isDone() {
            return task.isDone();
        }
    }
}
