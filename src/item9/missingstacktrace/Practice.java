package com.khc.practice.effectivejava.ch02.item9.missingstacktrace;

public class Practice {
    MyResource myRes1;
    MyResource myRes2;

    public void temp1() {

        // open에 대한 exception은 누락되고
        // close()에 대한 exception은 finally에서 전파되어 main메소드에서 던지게됨
        try {
            myRes1 = new MyResource();
            myRes1.open();
        } finally {
            if(myRes1 != null){
                myRes1.close();
            }
        }
    }

    public void temp2() {

        // open에 대한 exception이 catch()되어 e.printStackTrace()에 의해 출력되고
        // close에 대한 exception은 finally에서 전파되어 main메소드에서 던지게됨
        try {
            myRes1 = new MyResource();
            myRes1.open();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(myRes1 != null){
                myRes1.close();
            }
        }
    }

    public void temp3() {

        // try with resources 사용 (JAVA 9에서는 아래와 같이 AutoCloseable을 구현한 객체를 바로 넣을 수 있음)
        // suppressed를 통해 누락없이 예외를 잡을 수 있음
        // myRes1 = new MyResource(); 이렇게 작성하면 effectively final이어야 한다는 에러발생
        MyResource myRes1 = new MyResource();
        try(myRes1) {
            myRes1.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Practice().temp3();
    }
}
