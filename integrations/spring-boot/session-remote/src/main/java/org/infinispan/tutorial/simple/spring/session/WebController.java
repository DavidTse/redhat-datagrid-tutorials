package org.infinispan.tutorial.simple.spring.session;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

/**
 * This is the WebController that uses 3 threads to duplicate 
 * a scenario reported by a Red Hat customer
 * 
 * Thread1(CurrentModificationThread) getDocketList writes to ConcurrentHashMap 
 * Thread2(PopulateAccountThread) getMessage runs longer than thread1
 * Thread3(CheckingThread) opens a Docket from a DocketList, a ConcurrentHashMap
 * ConcurrentHashMap is empty
 * 
 * <code>
 * May 3, 2022, 3:34 PM ---------------------------------------------------------------------------------------------
 * Login initializing httpSessionInfo :
 * INFO  2022-05-03 13:53:38,218 [http-nio-28080-exec-4] [sessionId=26024378809] [] c.t.t.s.l.LogInOutManager#info: xxxxx LogInOutManager.confirmValidHTTPSession sessionInfo objRef : 354ed98
 * INFO  2022-05-03 13:53:38,218 [http-nio-28080-exec-4] [sessionId=26024378809] [] c.t.t.s.l.LogInOutManager#info: xxxxx LogInOutManager get docket list objRef
 * INFO  2022-05-03 13:53:38,218 [http-nio-28080-exec-4] [sessionId=26024378809] [] ObfuscationKeyLookupMaps#info: xxxxx docketListReverseMap objRef : 74fedbe1
 * Get Docket List :
 * INFO  2022-05-03 13:54:10,247 [http-nio-28080-exec-4] [sessionId=26024378809] [] c.t.t.s.BaseTVRemoteServiceServlet#info: xxxxx HttpSessionInfo objRef : ce75ef4
 * INFO  2022-05-03 13:54:11,673 [http-nio-28080-exec-4] [sessionId=26024378809] [] ObfuscationKeyLookupMaps#info: xxxxx docketListReverseMap objRef : 716b3ab2
 * Get Message Count :
 * INFO  2022-05-03 13:54:20,052 [http-nio-28080-exec-9] [sessionId=26024378809] [] c.t.t.s.BaseTVRemoteServiceServlet#info: xxxxx HttpSessionInfo objRef : 64677204
 * Open Docket :
 * INFO  2022-05-03 13:54:47,588 [http-nio-28080-exec-5] [sessionId=26024378809] [] c.t.t.s.BaseTVRemoteServiceServlet#info: xxxxx HttpSessionInfo objRef : 6f01fdcc
 * 
 * May 4, 2022, 3:52 PM ---------------------------------------------------------------------------------------------
 * Login initialize httpSessionInfo
 * INFO  2022-05-04 12:06:01,187 [http-nio-28080-exec-9] [sessionId=26024378814] [] c.t.t.s.l.LogInOutManager#info: xxxxx LogInOutManager.confirmValidHTTPSession sessionInfo objRef : 275e5f78
 * INFO  2022-05-04 12:06:01,187 [http-nio-28080-exec-9] [sessionId=26024378814] [] c.t.t.s.l.LogInOutManager#info: xxxxx LogInOutManager get docket list objRef
 * INFO  2022-05-04 12:06:01,188 [http-nio-28080-exec-9] [sessionId=26024378814] [] ObfuscationKeyLookupMaps#info: xxxxx docketListReverseMap objRef : 1ef34b38
 * Get docket list
 * INFO  2022-05-04 12:06:47,203 [http-nio-28080-exec-4] [sessionId=26024378814] [] c.t.t.s.BaseTVRemoteServiceServlet#info: xxxxx HttpSessionInfo objRef : 275e5f78
 * INFO  2022-05-04 12:06:48,473 [http-nio-28080-exec-4] [sessionId=26024378814] [] ObfuscationKeyLookupMaps#info: xxxxx docketListReverseMap objRef : 1ef34b38
 * Message count
 * INFO  2022-05-04 12:06:48,842 [http-nio-28080-exec-6] [sessionId=26024378814] [] c.t.t.s.BaseTVRemoteServiceServlet#info: xxxxx HttpSessionInfo objRef : 275e5f78
 * Open Docket
 * INFO  2022-05-04 12:07:08,769 [http-nio-28080-exec-3] [sessionId=26024378814] [] c.t.t.s.BaseTVRemoteServiceServlet#info: xxxxx HttpSessionInfo objRef : 275e5f78
 * </code>
 *
 * Based on the above code block, this is a pass by reference vs pass by value issue.
 * Based on the info from datagrid_scenarios.docx, there are plenty of concurrent access to HttpSessionInfo.
 * This is the top level monolithic container with references to large amount of entity objects.  
 * The current design is based on pass by reference.  
 * This is why it need the unsupported lib https://github.com/wildfly-clustering/wildfly-clustering-spring-session.
 * Concurrent asynchronous call is provided by GWT using subclass of a subclass of RemoteServiceServlet.
 * A number of these subclasses are use to make multiple asynchronous http calls within 1 request.
 * These are asynchronous http requests, not threads from the same request.
 * 
 * The use of HttpSessionInfo is the weak link in this design.
 * It unnecessarily bind all the concurrent access to a monolithic HttpSessionInfo object.
 * An obvious solution is to fine grain control for each entity object such as DocketList directly.
 * Each entity object can be set directly into session or the data grid, using a separate key.  
 * The read request or thread can then read it directly from session or the data grid using the new key. 
 * This will isolate the problems associated with of concurrent modification in HttpSessionInfo.
 * 
 * Red Hat further proposed a plan B.  If the above fail, create a new application cache, 
 * session-cache, and read and write to RHDG directly. 
 * Red Hat will provide a singleton handle and an composite key for this implementation.
 * 
 * Implements RHDG EventListener if necessary (probably not need)
 *
 * @author David Tse
 */
@Controller
@SessionAttributes("greetings")
public class WebController {

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
	           @RequestParam(name = "email", required = false, defaultValue = "World") String email,
	           @RequestParam(name = "phone", required = false, defaultValue = "123456789") String phone,
                           Model model, HttpSession session) {
        model.addAttribute("name", name);
        model.addAttribute("latest", session.getAttribute("latest"));
       
        
        String sessionID = session.getId();
        
        // most concurrent processes are kick off after details for a user are retrieved.
        CacheManager cacheManager = CacheManager.getCacheManager();
        User user = (User) cacheManager.getCacheData(sessionID, "user");
        if (user == null) {
        	user = new User();
        }
        long uuid2 = Long.parseLong(phone);
        //session.setAttribute("uuid2", uuid2);
        UUID uuid = new UUID(1l, uuid2);
        user.setUserID(uuid);
        user.setUserName(name);

        String emailAddress = name +"@" + email + ".com";
        List<String> emails = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        emails.add(emailAddress);
        user.setEmails(emails);
        phones.add(phone);
        user.setPhones(phones);
        cacheManager.setCacheData(sessionID, "user", user);
        
        int i = 0;
        Thread thread2 = new PopulateAccountThread(session, i);
        Thread thread1 = new CurrentModificationThread(session, i);
        Thread thread3 = new CheckingThread(session, i);
        
        thread2.start();
        thread1.start();
        
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        thread3.start();
        
        model.addAttribute("sessionID", sessionID);
        model.addAttribute("user", user);
        model.addAttribute("account", cacheManager.getCacheData(sessionID, "account"));
        model.addAttribute("docketList", cacheManager.getCacheData(sessionID, "docketList"));

        return "greeting";
    }
}
