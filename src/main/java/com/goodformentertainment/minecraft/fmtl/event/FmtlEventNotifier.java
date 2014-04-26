package com.goodformentertainment.minecraft.fmtl.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FmtlEventNotifier {
	private final Map<Class<?>, List<ListenerInvoker>> map;
	
	public FmtlEventNotifier() {
		// map = new LinkedHashMap<Class<?>, List<ListenerInvoker>>();
		map = new ConcurrentHashMap<Class<?>, List<ListenerInvoker>>();
	}
	
	public void registerEvents(final FmtlListener listener) {
		for (final Method method : listener.getClass().getMethods()) {
			final Class<?>[] parameterTypes = method.getParameterTypes();
			if (method.getAnnotation(FmtlEventHandler.class) != null && parameterTypes.length == 1) {
				final Class<?> subscribeTo = parameterTypes[0];
				List<ListenerInvoker> listenerInvokers = map.get(subscribeTo);
				if (listenerInvokers == null) {
					map.put(subscribeTo, listenerInvokers = new ArrayList<ListenerInvoker>());
				}
				listenerInvokers.add(new ListenerInvoker(method, listener));
			}
		}
	}
	
	public void remove(final FmtlListener listener) {
		for (final List<ListenerInvoker> listenerInvokers : map.values()) {
			for (int i = listenerInvokers.size() - 1; i >= 0; i--) {
				if (listenerInvokers.get(i).object == listener) {
					listenerInvokers.remove(i);
				}
			}
		}
	}
	
	public int callEvent(final FmtlEvent event) {
		final List<ListenerInvoker> listenerInvokers = map.get(event.getClass());
		if (listenerInvokers == null) {
			return 0;
		}
		int count = 0;
		for (final ListenerInvoker listenerInvoker : listenerInvokers) {
			listenerInvoker.invoke(event);
			count++;
		}
		return count;
	}
	
	private static class ListenerInvoker {
		final Method method;
		final Object object;
		
		public ListenerInvoker(final Method method, final Object object) {
			this.method = method;
			this.object = object;
		}
		
		public void invoke(final Object o) {
			try {
				method.invoke(object, o);
			} catch (final Exception e) {
				throw new AssertionError(e);
			}
		}
	}
}
