package com.khc.practice.effectivejava.ch02.item12.calloverridemethod;

public class BaseClass implements Cloneable {
    private int value;

    public BaseClass(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // 오버라이딩될 수 있는 메서드를 사용
    @Override
    protected BaseClass clone() {

        try{
            BaseClass cloned = (BaseClass) super.clone();
            cloned.reset();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void reset() {
        System.out.println("BaseClass reset called");
        this.value = 0;
    }
}

class SubClass extends BaseClass {
    private String state;

    public SubClass(int value, String state) {
        super(value);
        this.state = state;
    }

    @Override
    public void reset() {
        System.out.println("SubClass reset called");
        // reset을 통해 상태를 변경
        this.state = "reset";
    }

    public void printState() {
        System.out.println("Value: " + getValue() + ", State: " + state);
    }

    protected SubClass clone () {
        return (SubClass) super.clone();
    }
}

