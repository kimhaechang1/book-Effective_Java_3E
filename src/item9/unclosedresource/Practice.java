package com.khc.practice.effectivejava.ch02.item9.unclosedresource;

public class Practice {

    // 자원반납이 누락될 수 있음

    MyResource myRes1;
    MyResource myRes2;

    public void temp1() {

        // 하나가 덜 닫히게됨,
        // 왜냐하면 finally에서 myRes1을 닫는도중 exception이 발생되어 실행이 중단되고 main 메소드에 전파되었기 때문

        myRes1 = new MyResource();
        myRes2 = new MyResource();
        try {
            myRes1.open();
            myRes2.open();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(myRes1 != null) myRes1.close();
            if(myRes2 != null) myRes2.close();
        }
    }

    public void temp2() {

        // try-with resources를 사용하면 반드시 자원을 닫을 수 있음

        MyResource myRes1 = new MyResource();
        MyResource myRes2 = new MyResource();

        try(myRes1;myRes2) {
            myRes1.open();
            myRes2.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Practice().temp2();
    }
}
