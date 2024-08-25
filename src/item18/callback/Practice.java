package com.khc.practice.effectivejava.ch04.item18.callback;


interface SomethingWithCallback {

    void doSomething();

    void call();

}


class WrappedObject implements SomethingWithCallback {

    private final SomeService service;

    WrappedObject(SomeService service) {
        this.service = service;
    }

    @Override
    public void doSomething() {
        service.performAsync(this);
    }

    @Override
    public void call() {
        System.out.println("WrappedObject callback!");
    }
}


class Wrapper implements SomethingWithCallback {

    // 기존의 WrappedObject의 기능을 확장할 목적으로 합성으로서 이를 구현하려 했지만

    private final WrappedObject wrappedObject;

    Wrapper(WrappedObject wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    @Override
    public void doSomething() {
        wrappedObject.doSomething();
    }

    void doSomethingElse() {
        System.out.println("We can do everything the wrapped object can, and more!");
    }

    @Override
    public void call() {
        System.out.println("Wrapper callback!");
        // 이 재정의된 메소드는 호출되지 않는다.
        // 왜냐하면 콜백 프레임워크에서는 this가 중요하기 때문에 합성으로 확장하면 내부 구체클래스에 영향을 줄 수 없다.
    }
}

final class SomeService {

    void performAsync(SomethingWithCallback callback) {
        new Thread(() -> {
            perform();
            callback.call();
        }).start();
    }

    void perform() {
        System.out.println("Service is being performed.");
    }
}


public class Practice {

    public static void main(String[] args) {
        SomeService   service       = new SomeService();
        WrappedObject wrappedObject = new WrappedObject(service);
        Wrapper       wrapper       = new Wrapper(wrappedObject);
        wrapper.doSomething();
    }
}
