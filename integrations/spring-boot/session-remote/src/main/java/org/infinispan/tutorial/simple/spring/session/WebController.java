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
        httpSessionInfo = (HttpSessionInfo) session.getAttribute("user");
        if (httpSessionInfo == null) {
        	httpSessionInfo = new HttpSessionInfo();
	        httpSessionInfo.setSessionKey(sessionID);
	        session.setAttribute("user", httpSessionInfo);
        }
        
        for (int i=0; i<500; i++) {
	        Thread thread1 = new CurrentModificationThread(session, i);
	        thread1.start();
	        Thread thread2 = new PopulateSessionInfoThread(session);
	        thread2.start();
        }
        
        model.addAttribute("sessionID", sessionID);
        model.addAttribute("user", httpSessionInfo);

        return "greeting";
    }
}
