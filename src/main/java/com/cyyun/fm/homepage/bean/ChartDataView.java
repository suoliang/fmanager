package com.cyyun.fm.homepage.bean;

public class ChartDataView<K, V> {
	private K name;
	private V value;

	public K getName() {
		return name;
	}

	public void setName(K name) {
		this.name = name;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
}