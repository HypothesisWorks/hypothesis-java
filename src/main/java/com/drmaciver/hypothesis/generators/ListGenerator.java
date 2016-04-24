package com.drmaciver.hypothesis.generators;

import com.drmaciver.hypothesis.TestData;

import java.util.ArrayList;
import java.util.List;

public class ListGenerator<T> implements DataGenerator<List<T>> {

	private final DataGenerator<T> elementGenerator;

	ListGenerator(DataGenerator<T> elementGenerator) {
		this.elementGenerator = elementGenerator;
	}

    public List<T> doDraw(TestData data) {
		final List<T> result = new ArrayList<T>();
		while (true) {
			data.startExample();
			if (data.drawByte() <= 50) {
				data.stopExample();
				break;
			}
			result.add(this.elementGenerator.doDraw(data));
			data.stopExample();
		}
		return result;
	}
}
