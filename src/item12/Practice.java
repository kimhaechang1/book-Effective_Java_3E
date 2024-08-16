package com.khc.practice.effectivejava.ch02.item12;

class Animal {
    String type;

    public Animal(String type) {
        this.type = type;
    }

    // clone() 메서드를 호출하지만 Cloneable 인터페이스를 구현하지 않음
    @Override
    protected Object clone() throws CloneNotSupportedException {
        // Object에 있는 native clone을 호출하려면 Cloneable 마커 인터페이스를 명시해야함을 의미
        return super.clone();
    }
}

public class Practice {

    static class A implements Cloneable{

        // 연쇄적으로 호출되게 구현
        public A clone(){
            try {
                print();
                return (A) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }

        }

        public void print() {

        }

        // 컴파일러는 승인하지만 원하는데로 동작하지 않는다.
        /*public Object clone () throws CloneNotSupportedException {
            return new A();
        }*/
    }

    static class B extends A{

        String str;
        public B clone(){
            str = "김회창";

            return (B) super.clone();
        }

        public void print() {
            System.out.println(this.str);
        }
    }


    public static void method1() throws CloneNotSupportedException {
        Animal animal = new Animal("Cat");
        // CloneNotSupportedException이 발생하는 부분
        Animal clonedAnimal = (Animal) animal.clone();
        System.out.println("Cloned Animal: " + clonedAnimal.type);
    }

    public static void method2() throws CloneNotSupportedException {
        B b = new B();
        System.out.println(b.clone() instanceof B);
        B bClone = b.clone();
        System.out.println(b);
        System.out.println(bClone);
        System.out.println(bClone.equals(b));
    }

    public static void method3() throws CloneNotSupportedException {
        Stack stack = new Stack();
        Stack cloneStack = stack.clone();

        System.out.println(stack.equals(cloneStack));
        // 복사되어 서로의 참조가 다른것을 확인

        stack.push(15);
        cloneStack.push(30);

        System.out.println(stack.peek());
        // 서로의 내부 elements 배열이 공유되어 있다는것을 확인
    }

    public static void main(String[] args) {
        try {
            // method2(); // 클론의 연쇄구현 예시
            method2(); // 필드에 가변객체가 있는순간 재앙이 되는 예시
        }  catch (CloneNotSupportedException e) {
            // 예외가 발생했을 때 처리
            System.out.println("CloneNotSupportedException 발생: 객체가 복제 불가능합니다.");
            e.printStackTrace();
        }
    }
}

