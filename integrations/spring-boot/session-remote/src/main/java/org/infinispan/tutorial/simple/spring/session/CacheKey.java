package org.infinispan.tutorial.simple.spring.session;

import java.io.Serializable;

/**
 * Composite key for Red Hat Data Grid
 * 
 * @author David Tse
 */
public class CacheKey implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8916322140351092982L;
	
	private String sessionID;
	
	private String objectKey;
	
	public CacheKey() {}
	
	public CacheKey(String sessionID, String objectKey) 
	{
		this.sessionID = sessionID;
		this.objectKey = objectKey;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objectKey == null) ? 0 : objectKey.hashCode());
		result = prime * result + ((sessionID == null) ? 0 : sessionID.hashCode());
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
		CacheKey other = (CacheKey) obj;
		if (objectKey == null) {
			if (other.objectKey != null)
				return false;
		} else if (!objectKey.equals(other.objectKey))
			return false;
		if (sessionID == null) {
			if (other.sessionID != null)
				return false;
		} else if (!sessionID.equals(other.sessionID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CacheKey [sessionID=" + sessionID + ", objectKey=" + objectKey + "]";
	}
}
