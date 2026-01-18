package com.example.oauth2lab.authserver.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SessionController {

    private final SessionRegistry sessionRegistry;

    public SessionController(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping("/sessions")
    public String viewSessions(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpSession currentSession) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Get all sessions for the current user
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
        
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("sessions", sessions);
        model.addAttribute("currentSessionId", currentSession.getId());
        model.addAttribute("sessionCount", sessions.size());
        
        return "sessions";
    }

    @PostMapping("/sessions/delete")
    public String deleteSession(@RequestParam("sessionId") String sessionId, HttpSession currentSession) {
        SessionInformation sessionInfo = sessionRegistry.getSessionInformation(sessionId);
        
        if (sessionInfo != null) {
            sessionInfo.expireNow();
            
            // If deleting current session, invalidate it
            if (sessionId.equals(currentSession.getId())) {
                currentSession.invalidate();
                SecurityContextHolder.clearContext();
                return "redirect:/login?logout";
            }
        }
        
        return "redirect:/sessions";
    }

    @PostMapping("/sessions/delete-all")
    public String deleteAllSessions(@AuthenticationPrincipal UserDetails userDetails, HttpSession currentSession) {
        if (userDetails != null) {
            List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
            
            for (SessionInformation session : sessions) {
                session.expireNow();
            }
            
            currentSession.invalidate();
            SecurityContextHolder.clearContext();
        }
        
        return "redirect:/login?logout";
    }
}
