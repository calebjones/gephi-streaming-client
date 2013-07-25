package org.gephi.streaming.client.model;

import java.util.HashMap;
import java.util.Map;

public class Edge extends AttributeMapModel<Edge> {
	
	public static final String ID_KEY = "id";
	public static final String SOURCE_KEY = "source";
	public static final String TARGET_KEY = "target";
	public static final String DIRECTED_KEY = "directed";
	public static final String WEIGHT_KEY = "weight";
	
	
	private String id;

	private String source;
	
	private String target;
	
	private Boolean directed;
	
	private Long weight;
	
	public Edge() {
		
	}
	
	public Edge(String id, String source, String target) {
		this.id = id;
		this.source = source;
		this.target = target;
	}
	
	public Edge(String id, String source, String target, 
			Map<String, Object> attributes) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.attributes = attributes;
	}
	
	public Edge(String id, String source, String target, 
			Boolean directed, Long weight) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.directed = directed;
		this.weight = weight;
	}
	
	public Edge(String id, String source, String target, 
			Boolean directed, Long weight, Map<String, Object> attributes) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.directed = directed;
		this.weight = weight;
		this.attributes = attributes;
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		putIfNotNull(map, ID_KEY, id);
		putIfNotNull(map, SOURCE_KEY, source);
		putIfNotNull(map, TARGET_KEY, target);
		putIfNotNull(map, DIRECTED_KEY, directed);
		putIfNotNull(map, WEIGHT_KEY, weight);
		
		map.putAll(attributes);
		
		return map;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public Boolean isDirected() {
		return directed;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setDirected(Boolean directed) {
		this.directed = directed;
	}

	public Boolean getDirected() {
		return directed;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}
	
	public Edge withId(String id) {
		this.id = id;
		return this;
	}
	
	public Edge withSource(String source) {
		this.source = source;
		return this;
	}
	
	public Edge withTarget(String target) {
		this.target = target;
		return this;
	}

	public Edge withDirected(Boolean directed) {
		this.directed = directed;
		return this;
	}
	
	public Edge withWeight(Long weight) {
		this.weight = weight;
		return this;
	}
	
}
