	package org.gephi.streaming.client.model;

import java.util.HashMap;
import java.util.Map;

public class Node extends AttributeMapModel<Node> {
	
	public static final String ID_KEY = "id";
	public static final String LABEL_KEY = "label";
	public static final String SIZE_KEY = "size";
	
	private String id;
	
	private String label;
	
	private Long size;
	
	public Node() {
		
	}
	
	public Node(String id, String label, Long size) {
		this.id = id;
		this.label = label;
		this.size = size;
	}
	
	public Node(String id, String label, Long size, Map<String, Object> attributes) {
		this.id = id;
		this.label = label;
		this.size = size;
		this.attributes = attributes;
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		putIfNotNull(map, ID_KEY, id);
		putIfNotNull(map, LABEL_KEY, label);
		putIfNotNull(map, SIZE_KEY, size);
		
		map.putAll(attributes);
		
		return map;
	}
	
	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Node withId(String id) {
		this.id = id;
		return this;
	}
	
	public Node withLabel(String label) {
		this.label = label;
		return this;
	}
	
	public Node withSize(Long size) {
		this.size = size;
		return this;
	}
	
}
