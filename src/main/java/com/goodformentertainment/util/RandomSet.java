package com.goodformentertainment.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A Set implementation which provides for a means of getting a random element from within the Set.
 * Modified from a question response on stackoverflow by fandrew.
 * http://stackoverflow.com/questions/124671/picking-a-random-element-from-a-set
 * 
 * @author Todd Harrison
 * 
 * @param <E>
 *          The type contained by this Set.
 */
public class RandomSet<E> extends AbstractSet<E> {
	private final List<E> list;
	private final Map<E, Integer> map;
	private final Random random;
	
	/**
	 * Constructs a new RandomSet with the specified backing Random generator.
	 * 
	 * @param random
	 *          The Random generator to use when retrieving a random element.
	 */
	public RandomSet(final Random random) {
		list = new ArrayList<E>();
		map = new HashMap<E, Integer>();
		this.random = random;
	}
	
	/**
	 * Constructs a new RandomSet with a new instance of java.util.Random.
	 */
	public RandomSet() {
		this(new Random());
	}
	
	/**
	 * Constructs a new RandomSet with the specified backing Random generator and populates it with
	 * the items in the specified Collection.
	 * 
	 * @param items
	 *          The collection used to initially populate this RandomSet.
	 * @param random
	 *          The Random generator to use when retrieving a random element.
	 */
	public RandomSet(final Collection<E> items, final Random random) {
		this(random);
		for (final E item : items) {
			map.put(item, list.size());
			list.add(item);
		}
	}
	
	/**
	 * Constructs a new RandomSet with a new instance of java.util.Random and populates it with
	 * the items in the specified Collection.
	 * 
	 * @param items
	 *          The collection used to initially populate this RandomSet.
	 */
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
	
	/**
	 * Removes and returns the element at the specified index from the backing List in this RandomSet.
	 * 
	 * @param id
	 *          The index of the element to remove from this RandomSet.
	 * @return The element at the specified index.
	 * @throws IndexOutOfBoundsException
	 *           If the index specified is greater than the number of elements in this RandomSet.
	 */
	public E removeAt(final int id) {
		if (id >= list.size()) {
			throw new IndexOutOfBoundsException("The specified index is out of bounds in this set");
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
	
	/**
	 * Returns the element at the specified index from the backing List in this RandomSet.
	 * 
	 * @param i
	 *          The index of the element to retrieve from this RandomSet.
	 * @return The element at the specified index.
	 * @throws IndexOutOfBoundsException
	 *           If the index specified is greater than the number of elements in this RandomSet.
	 */
	public E get(final int i) {
		return list.get(i);
	}
	
	/**
	 * Returns a random element from this RandomSet using the Random generator specified.
	 * 
	 * @param rnd
	 *          The Random generator to use.
	 * @return A random element from this RandomSet.
	 */
	public E getRandom(final Random rnd) {
		if (list.isEmpty()) {
			return null;
		}
		final int id = rnd.nextInt(list.size());
		return list.get(id);
	}
	
	/**
	 * Returns a random element from this RandomSet using the built-in Random generator.
	 * 
	 * @return A random element from this RandomSet.
	 */
	public E getRandom() {
		return getRandom(random);
	}
	
	/**
	 * Removes and returns a random element from this RandomSet using the Random generator specified.
	 * 
	 * @param rnd
	 *          The Random generator to use.
	 * @return A random element removed from this RandomSet.
	 */
	public E popRandom(final Random rnd) {
		if (list.isEmpty()) {
			return null;
		}
		final int id = rnd.nextInt(list.size());
		return removeAt(id);
	}
	
	/**
	 * Removes and returns a random element from this RandomSet using the built-in Random generator.
	 * 
	 * @return A random element removed from this RandomSet.
	 */
	public E popRandom() {
		return popRandom(random);
	}
}
