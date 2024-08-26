package com.khc.practice.effectivejava.ch04.item19;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

class Super {
	public Super() {
		// 재정의 가능한 메소드는 잠재적으로 오류를 일으킬 가능성이 높다!!!
		overrideMe();
	}

	public void overrideMe() {
		// overrideMe()는 재정의 가능한 메소드이다.
	}

	private void overrideMeHelper() {

	}
}

final class Sub extends Super {

	private final Instant instant;

	Sub() {
		// 숨겨져 있지만 컴파일러가 super()를 반드시 호출한다.
		instant = Instant.now();
	}

	@Override
	public void overrideMe() {
		// 재정된 메소드에서 하위 클래스의 멤버에 접근하고 있다.
		System.out.println(instant);
	}
}

public class Practice {

	public static void main(String[] args) {
		Sub sub = new Sub();
		sub.overrideMe();

		ArrayList<Integer> list = new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9,10));
		list.clear();
		// 이거는 ArrayList의 clear()를 호출하는 것
		// 이는 재정의 되어있음 -> 딱히 removeRange()를 호출하지 않음

		List<Integer> subList = list.subList(0, 4);
		subList.clear();
		// 이거는 ArrayList의 중첩 private 클래스의 SubList가 됨
		// 여기에는 clear() 메소드가 AbstractList의 clear()를 따라가게 되고, 이는 AbstractList의 removeRange()에 영향을 받게됨
		// 그래서 SubList를 호출할 때에는 removeRange() 성능에 clear()성능이 따라가게됨


	}
}
