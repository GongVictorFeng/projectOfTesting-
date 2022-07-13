package com.techyourchance.unittesting;

public class Demo {
    private Addition addition;

    public Demo(Addition addition) {
        this.addition=addition;
    }

    public int getNumber(int a, int b) {
        return addition.getNumber(a,b);
    }
}
