package org.jaram.ds.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jdekim43 on 2016. 1. 7..
 */
public class DelayUtil<Data> {

    public static final int MODE_ALL_EXECUTE = 0;
    public static final int MODE_LAST_EXECUTE = 1;
    public static final int MODE_FIRST_EXECUTE = 2;

    public static final int ONE_SECOND = 1000;
    public static final int TWO_SECOND = 2000;
    public static final int THREE_SECOND = 3000;

    protected static final HashMap<String, DelayUtil> repo = new HashMap<>();

    private Executable<Data> executable;
    private final List<Data> datas = new ArrayList<>();

    private int mode = MODE_ALL_EXECUTE;
    private long delayTime = ONE_SECOND;

    private Timer timer = new Timer();

    public static DelayUtil get() {
        return get(Object.class);
    }

    public static <T> DelayUtil<T> get(Class<T> clazz) {
        return new DelayUtil<T>();
    }

    public static DelayUtil get(String key) {
        return get(key, Object.class);
    }

    public static <T> DelayUtil<T> get(String key, Class<T> clazz) {
        if (repo.containsKey(key)) {
            return (DelayUtil<T>)repo.get(key);
        } else {
            return new DelayUtil<T>().store(key);
        }
    }

    public void resetTime() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new Notifier(), delayTime);
    }

    public DelayUtil<Data> store(String key) {
        repo.put(key, this);
        return this;
    }

    public DelayUtil<Data> setExecutable(Executable<Data> executable) {
        this.executable = executable;
        return this;
    }

    public DelayUtil<Data> executeData(Data data) {
        synchronized (datas) {
            datas.add(data);
            resetTime();
        }
        return this;
    }

    public DelayUtil<Data> setDelayTime(long millisecond) {
        this.delayTime = millisecond;
        return this;
    }

    public DelayUtil<Data> setMode(int mode) {
        this.mode = mode;
        return this;
    }

    private void notifyRun(Data data) {
        executable.run(data);
    }

    public interface Executable<Data> {
        void run(Data data);
    }

    public static class Delayer {

    }

    private class Notifier extends TimerTask {

        @Override
        public void run() {
            synchronized (datas) {
                if (datas.size() == 0) {
                    return;
                }
                switch (mode) {
                    default:
                    case MODE_ALL_EXECUTE:
                        for (Data data : datas) {
                            notifyRun(data);
                        }
                        datas.clear();
                        break;
                    case MODE_LAST_EXECUTE:
                        Data lastData = datas.get(datas.size()-1);
                        datas.clear();
                        notifyRun(lastData);
                        break;
                    case MODE_FIRST_EXECUTE:
                        Data firstData = datas.get(0);
                        datas.clear();
                        notifyRun(firstData);
                        break;
                }
            }
        }
    }
}