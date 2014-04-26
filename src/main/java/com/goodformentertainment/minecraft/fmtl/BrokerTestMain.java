package com.goodformentertainment.minecraft.fmtl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.goodformentertainment.minecraft.fmtl.event.FmtlEventHandler;

public class BrokerTestMain {
	public static void main(final String... args) {
		final Broker broker = new Broker();
		broker.add(new Component());
		
		broker.publish("Hello");
		broker.publish(new Date());
		broker.publish(3.1415);
	}
}

class Component {
	@FmtlEventHandler
	public void onString(final String s) {
		System.out.println("String - " + s);
	}
	
	@FmtlEventHandler
	public void onDate(final Date d) {
		System.out.println("Date - " + d);
	}
	
	@FmtlEventHandler
	public void onDouble(final Double d) {
		System.out.println("Double - " + d);
	}
}

class Broker {
	private final Map<Class, List<SubscriberInfo>> map = new LinkedHashMap<Class, List<SubscriberInfo>>();
	
	public void add(final Object o) {
		for (final Method method : o.getClass().getMethods()) {
			final Class<?>[] parameterTypes = method.getParameterTypes();
			if (method.getAnnotation(FmtlEventHandler.class) == null || parameterTypes.length != 1) {
				continue;
			}
			final Class subscribeTo = parameterTypes[0];
			List<SubscriberInfo> subscriberInfos = map.get(subscribeTo);
			if (subscriberInfos == null) {
				map.put(subscribeTo, subscriberInfos = new ArrayList<SubscriberInfo>());
			}
			subscriberInfos.add(new SubscriberInfo(method, o));
		}
	}
	
	public void remove(final Object o) {
		for (final List<SubscriberInfo> subscriberInfos : map.values()) {
			for (int i = subscriberInfos.size() - 1; i >= 0; i--) {
				if (subscriberInfos.get(i).object == o) {
					subscriberInfos.remove(i);
				}
			}
		}
	}
	
	public int publish(final Object o) {
		final List<SubscriberInfo> subscriberInfos = map.get(o.getClass());
		if (subscriberInfos == null) {
			return 0;
		}
		int count = 0;
		for (final SubscriberInfo subscriberInfo : subscriberInfos) {
			subscriberInfo.invoke(o);
			count++;
		}
		return count;
	}
	
	static class SubscriberInfo {
		final Method method;
		final Object object;
		
		SubscriberInfo(final Method method, final Object object) {
			this.method = method;
			this.object = object;
		}
		
		void invoke(final Object o) {
			try {
				method.invoke(object, o);
			} catch (final Exception e) {
				throw new AssertionError(e);
			}
		}
	}
}
