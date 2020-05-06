package io.github.scolytus.data;

public class Counter {

    private int cnt = 0;

    public synchronized void inc() {
        cnt++;
    }

    public int getCount() {
        return cnt;
    }
}
