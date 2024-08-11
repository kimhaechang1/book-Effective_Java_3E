package com.khc.practice.effectivejava.ch02.item8;


import java.lang.ref.Cleaner;

class Room implements AutoCloseable {

    private static final Cleaner cleaner = Cleaner.create();

    private static class State implements Runnable {

        int numJunkPiles;

        // 아래는 Room 을 직접 참조하므로, 순환참조가 발생해서 Room에 대하여 null을 하더라도, State가 Room을 참조하게되어 강참조가 발생한다.
        // 따라서 GC수거가 되지 않는다.

        State(int numJunkPiles){
            this.numJunkPiles = numJunkPiles;
        }
        /*Room room;

        State(int numJunkPiles, Room r) {
            this.room = r;
            this.numJunkPiles = numJunkPiles;
        }*/

        @Override
        public void run() {
            System.out.println("방 청소");
            numJunkPiles = 0;
        }
    }

    private final State state;

    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        // state = new State(numJunkPiles, this);
        // 순환참조가 발생하는 코드

        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }


    @Override
    public void close() {
        cleanable.clean();
    }
}


public class Practice {

    public static void main(String[] args) {
        Room room = new Room(99);
        room = null;

        System.gc();

        try {
            Thread.sleep(3000); // 10초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("아무렴");
    }
}
