package io.github.scolytus.npmvsoss.data;

public class Counter {

    private int cnt = 0;

    public synchronized void inc() {
        cnt++;
    }

    public int getCount() {
        return cnt;
    }

    // just for Jackson
    public void setCount(int newCount) {
        cnt = newCount;
    }
}
