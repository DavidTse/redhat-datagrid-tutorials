package org.infinispan.tutorial.simple.spring.session;

import java.util.Iterator;
import java.util.Set;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.springframework.session.Session;

public class HotRodConnector 
{
    public static void main(String[] args)
    {
        try {            
            ConfigurationBuilder builder = new ConfigurationBuilder();
            //builder.withProperties(prop);
            builder.uri("hotrod://admin:password@127.0.0.1:11222");
            System.out.println("Start");
            RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.build());
            String cacheName = "sessions";
            RemoteCache<String, Session> remoteCache = remoteCacheManager.getCache(cacheName);
            //
            Set<String> names = remoteCacheManager.getCacheNames();
            Iterator<String> it = names.iterator();
            while (it.hasNext()) {
                System.out.println(it.next());
            }
            int size = remoteCache.size();
            System.out.println("Sessions cache size: " + size);
            
            /*
            Set<String> set = remoteCache.keySet();
            for (String key: set) {
            	//Session value= remoteCache.get(key);
            	String value = " ";
                System.out.printf(">> key=%s Value=%s%n", key, value);
            }
            */
            remoteCacheManager.close();
            System.out.println("Done");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
