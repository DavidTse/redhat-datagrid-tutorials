package org.infinispan.tutorial.simple.spring.session;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

/**
 * Singleton handle for Red Hat Data Grid RemoteCacheManager
 * 
 * @author David Tse
 */
public class CacheManager 
{
	private static CacheManager cacheManager = new CacheManager();
	
	public static CacheManager getCacheManager()
	{
		return cacheManager;
	}
	
	private RemoteCacheManager remoteCacheManager;
	
	private CacheManager() 
	{
		remoteCacheManager = new RemoteCacheManager();
		remoteCacheManager.start();
	}
	
	public void close()
	{
		remoteCacheManager.close();
	}
	
	public Object getCacheData(String sessionID, String objectKey)
	{
		RemoteCache<CacheKey, Object> remoteCache = remoteCacheManager.getCache("session-cache");
		CacheKey cacheKey = new CacheKey(sessionID, objectKey);
		return remoteCache.get(cacheKey);
	}
	
	public void setCacheData(String sessionID, String objectKey, Object cacheData)
	{
		RemoteCache<CacheKey, Object> remoteCache = remoteCacheManager.getCache("session-cache");
		CacheKey cacheKey = new CacheKey(sessionID, objectKey);
		remoteCache.put(cacheKey, cacheData);
	}

	public void stop()
	{
		remoteCacheManager.stop();
	}
}
