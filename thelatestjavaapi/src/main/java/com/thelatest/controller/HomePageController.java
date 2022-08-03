package com.thelatest.controller;

import com.thelatest.model.User;
import com.thelatest.repository.CommentRepository;
import com.thelatest.repository.PostRepository;
import com.thelatest.repository.UserRepository;
import com.thelatest.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomePageController {

    // 1) include the repositories created elsewhere
    // 2) map them to objects that will be instantiated in this class when necessary
    // 3) enables method creation used to control the front-end flow of the application
    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    CommentRepository commentRepository;

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            return "redirect:/";
        }

        // send a 'newly created' user to the template as the string user, so that it can be displayed within the template
        // once logged in,
        model.addAttribute("user", new User());
        // redirect user to / route, the same as homepage
        return "login";
    }

    @GetMapping("/users/logout")
    public String logout(HttpServletRequest request) {
        // check whether a session exists
        if (request.getSession(false) != null) {
        // if session exists invalidate the session, this logs out the user
            request.getSession().invalidate();
        }
        // redirect user to login page
        return "redirect:/login";
    }

}
