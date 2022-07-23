package org.infinispan.tutorial.simple.jcache;

import javax.cache.Cache;
import javax.cache.CacheManager;



/**
 *
 * Infinispan Server includes a default property realm that requires
 * authentication. Create some credentials before you run this tutorial.
 *
 */
public class InfinispanRemoteJCache {
	
	public static final String PROVIDER = "org.infinispan.jcache.remote.JCachingProvider";

    public static void main(String[] args) {
	   
    	CacheManager cacheManager = InfinispanRemoteJCacheManager.getInstance().getCacheManager();
	    Cache<String, String> cache = cacheManager.getCache("jcache");
	    cache.put("key", "value");
	    System.out.printf("key = %s\n", cache.get("key"));
	    System.exit(0);
    }
}
