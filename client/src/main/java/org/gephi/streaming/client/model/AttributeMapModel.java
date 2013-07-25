package org.gephi.streaming.client.model;

import java.util.HashMap;
import java.util.Map;

public abstract class AttributeMapModel<T> {

	protected Map<String, Object> attributes;
	
	
	protected void putIfNotNull(Map<String, Object> map, String key, Object value) {
		ensureAttributesInstantiated();
		if (value != null) {
			map.put(key, value);
		}
	}
	
	public void putAllAttributes(Map<String, Object> attributes) {
		ensureAttributesInstantiated();
		this.attributes.putAll(attributes);
	}
	
	public void putAttribute(String key, Object value) {
		ensureAttributesInstantiated();
		this.attributes.put(key, value);
	}
	
	protected void ensureAttributesInstantiated() {
		if (null == this.attributes) {
			this.attributes = new HashMap<String, Object>();
		}
	}
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@SuppressWarnings("unchecked")
	public T withAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
		return (T)this;
	}
	
	@SuppressWarnings("unchecked")
	public T withAttribute(String key, Object value) {
		ensureAttributesInstantiated();
		this.attributes.put(key, value);
		return (T)this;
	}

}
