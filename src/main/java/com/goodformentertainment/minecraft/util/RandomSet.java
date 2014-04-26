package com.goodformentertainment.minecraft.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomSet<E> extends AbstractSet<E> {
	private final List<E> list;
	private final Map<E, Integer> map;
	private final Random random;
	
	public RandomSet(final Random random) {
		list = new ArrayList<E>();
		map = new HashMap<E, Integer>();
		this.random = random;
	}
	
	public RandomSet() {
		this(new Random());
	}
	
	public RandomSet(final Collection<E> items, final Random random) {
		this(random);
		for (final E item : items) {
			map.put(item, list.size());
			list.add(item);
		}
	}
	
	public RandomSet(final Collection<E> items) {
		this(items, new Random());
	}
	
	@Override
	public boolean add(final E item) {
		if (map.containsKey(item)) {
			return false;
		}
		map.put(item, list.size());
		list.add(item);
		return true;
	}
	
	@Override
	public boolean remove(final Object item) {
		final Integer id = map.get(item);
		if (id == null) {
			return false;
		}
		removeAt(id);
		return true;
	}
	
	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}
	
	public E removeAt(final int id) {
		if (id >= list.size()) {
			return null;
		}
		final E res = list.get(id);
		map.remove(res);
		final E last = list.remove(list.size() - 1);
		// Skip filling the hole in map indices if last is removed
		if (id < list.size()) {
			map.put(last, id);
			list.set(id, last);
		}
		return res;
	}
	
	public E get(final int i) {
		return list.get(i);
	}
	
	public E getRandom(final Random rnd) {
		if (list.isEmpty()) {
			return null;
		}
		final int id = rnd.nextInt(list.size());
		return list.get(id);
	}
	
	public E getRandom() {
		return getRandom(random);
	}
	
	public E popRandom(final Random rnd) {
		if (list.isEmpty()) {
			return null;
		}
		final int id = rnd.nextInt(list.size());
		return removeAt(id);
	}
	
	public E popRandom() {
		return popRandom(random);
	}
}
