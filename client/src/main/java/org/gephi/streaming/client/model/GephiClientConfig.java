package org.gephi.streaming.client.model;

public class GephiClientConfig {
	
	private String url;
	
	private String username;
	
	private String password;
	
	public GephiClientConfig() {
		
	}
	
	public GephiClientConfig(String url) {
		setUrl(url);
	}
	
	public GephiClientConfig(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (url.indexOf("operation=updateGraph") == -1) {
			int queryIndex = url.indexOf("?");
			int ampIndex = url.lastIndexOf("&");
			
			if (-1 == queryIndex) {
				url += "?operation=updateGraph";
			}
			else {
				if (ampIndex < url.length() - 1 && url.charAt(url.length() - 1) != '?') {
					url += "&";
				}
				
				url += "operation=updateGraph";
			}
		}
		
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public GephiClientConfig withUrl(String url) {
		setUrl(url);
		return this;
	}
	
	public GephiClientConfig withUsername(String username) {
		this.username = username;
		return this;
	}
	
	public GephiClientConfig withPassword(String password) {
		this.password = password;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GephiClientConfig other = (GephiClientConfig) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	

}
