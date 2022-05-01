package org.infinispan.tutorial.simple.spring.session;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;

/**
 * This is the WebController that uses 3 threads to duplicate 
 * a scenario reported by a Red Hat customer
 * 
 * Thread1(CurrentModificationThread) getDocketList writes to ConcurrentHashMap 
 * Thread2(PopulateSessionInfoThread) getMessage runs longer than thread1
 * Thread3(CheckingThread) opens a Docket from a DocketList, a ConcurrentHashMap
 * ConcurrentHashMap is null
 * 
 * <code>
 * Output:
 * CurrentModificationThread 498 622d3370
 * PopulateSessionInfoThread 498 622d3370
 * CheckingThread {} 498 7ff3226f
 * CurrentModificationThread 499 7ff3226f
 * PopulateSessionInfoThread 499 7ff3226f
 * CheckingThread {1=500, 2=499, 3=500} 499 7ff3226f
 * 
 * Scenario 1 (current scenario for looping 500 times inside the same request)
 * You get 1 session with 500 HttpSessionInfo accessible by 1 session key (user)
 * Session-(user)->HttpSessionInfo->ConcurrentHashMap
 *                 HttpSessionInfo->ConcurrentHashMap
 *                 HttpSessionInfo->ConcurrentHashMap
 *                 
 * In this scenario, the object references are all messed up.
 * Furthermore, there is no practical use case is this scenario.
 *
 * Scenario 2 (the correct scenario)
 * 500 of the following:
 * Session-(user)->HttpSessionInfo->ConcurrentHashMap
 * Session-(user)->HttpSessionInfo->ConcurrentHashMap
 * ...
 * 
 * The following line of code obtains the object reference and is very useful in debugging
 * String objRef = Integer.toHexString(System.identityHashCode(httpSessionInfo));
 * <code/>
 * More details are in the 2 log files (problems.txt & working.txt)
 * 
 * There are nothing wrong with the infinispan lib and its associated spring-session lib
 * 
 * The issues stem from the load test codes.
 * There are 500 HttpSessionInfo objects link by 1 session key (user).
 * The object reference has already been changed when checkingThread fetch it from the session.
 * 
 * This is not the correct way to simulate 500 login/logout.  
 * You end up with 500 objects using the same session key in 1 session
 * instead of 500 sessions each with 1 HttpSessionInfo object using 1 session key.
 * 
 * The log files show that the ConcurrentHashMap was set in one HttpSessionInfo 
 * but was checked in a thread3 with a different object reference.
 * A quick fix is to get a reference to HttpSessionInfo at the constructor instead of run().
 * 
 * A better fix is to use HttpURLConnection and loop 500 times there.
 * This better simulate the effect of 500 login/logout is to use a
 * for loop and HttpURLConnection
 * <p>
 * see https://github.com/DavidTse/datagrid, HttpURLConnectionLoadTest.java
 * <p>
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
        
        session.setAttribute("latest", name);
        session.setAttribute("email", email);
        session.setAttribute("phone", phone);
        
        String sessionID = session.getId();
        
        HttpSessionInfo httpSessionInfo = null;
        
        // 1 works all the time
        for (int i=0; i<1; i++) {  	
            String key = "user";
            httpSessionInfo = new HttpSessionInfo();
            httpSessionInfo.setSessionKey(sessionID);
            session.setAttribute(key, httpSessionInfo);

	        Thread thread2 = new PopulateSessionInfoThread(session, i);
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
        }
        
        model.addAttribute("sessionID", sessionID);
        model.addAttribute("user", httpSessionInfo);

        return "greeting";
    }
}
