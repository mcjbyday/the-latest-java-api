package com.thelatest.controller;

import com.thelatest.model.Comment;
import com.thelatest.model.Post;
import com.thelatest.model.User;
import com.thelatest.repository.CommentRepository;
import com.thelatest.repository.PostRepository;
import com.thelatest.repository.UserRepository;
import com.thelatest.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    // endpoint used to display homepage, must retrieve past Post data.
    @GetMapping("/")
    public String homepageSetup(Model model, HttpServletRequest request) {
        User sessionUser = new User();
        // check that the user is logged in
        if (request.getSession(false) != null) {
            sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
        } else {
            model.addAttribute("loggedIn", false);
        }

        // declare postList of type List<Post>
        List<Post> postList = postRepository.findAll();
        // get all past posts and populate them into the postList variable
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
            User user = userRepository.getReferenceById(p.getUserId());
            p.setUserName(user.getUsername());
        }

        // model.addAttributes() enables addition of these details to the User model.
        // Now this route is able to populate and display all data relevant the homepage.html template (returned by route)

        model.addAttribute("postList", postList);
        model.addAttribute("loggedIn", sessionUser.isLoggedIn());

        // "point" and "points" attributes refer to upvotes.
        model.addAttribute("point", "point");
        model.addAttribute("points", "points");

        return "homepage";
    }

    @GetMapping("/dashboard")
    public String dashboardPageSetup(Model model, HttpServletRequest request) throws Exception {

        if (request.getSession(false) != null) {
            setupDashboardPage(model, request);
            return "dashboard";
        } else {
            model.addAttribute("user", new User());
            return "login";
        }
    }

    @GetMapping("/dashboardEmptyTitleAndLink")
    public String dashboardEmptyTitleAndLinkHandler(Model model, HttpServletRequest request) throws Exception {
        setupDashboardPage(model, request);
        model.addAttribute("notice", "Posts require a Title and a Link!");
        return "dashboard";
    }


    @GetMapping("/singlePostEmptyComment/{id}")
    public String singlePostEmptyCommentHandler(@PathVariable int id, Model model, HttpServletRequest request) {
        setupSinglePostPage(id, model, request);
        model.addAttribute("notice", "Comments require content for submission!");
        return "single-post";
    }


    @GetMapping("/post/{id}")
    public String singlePostPageSetup(@PathVariable int id, Model model, HttpServletRequest request) {
        setupSinglePostPage(id, model, request);
        return "single-post";
    }


    @GetMapping("/editPostEmptyComment/{id}")
    public String editPostEmptyCommentHandler(@PathVariable int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            setupEditPostPage(id, model, request);
            model.addAttribute("notice", "Comments require content for submission!");
            return "edit-post";
        } else {
            model.addAttribute("user", new User());
            return "login";
        }
    }


    @GetMapping("/dashboard/edit/{id}")
    public String editPostPageSetup(@PathVariable int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            setupEditPostPage(id, model, request);
            return "edit-post";
        } else {
            model.addAttribute("user", new User());
            return "login";
        }
    }



    public Model setupDashboardPage(Model model, HttpServletRequest request) throws Exception {
        // assign the values of the current user and id, via SESSION_USER
        User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

        Integer userId = sessionUser.getId();
        // gather all posts associated with the id
        List<Post> postList = postRepository.findAllPostsByUserId(userId);
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
            User user = userRepository.getReferenceById(p.getUserId());
            p.setUserName(user.getUsername());
        }
        // pass template-relevant information into the Thymeleaf pages when each respective template is called
        model.addAttribute("user", sessionUser);
        model.addAttribute("postList", postList);
        model.addAttribute("loggedIn", sessionUser.isLoggedIn());
        model.addAttribute("post", new Post());

        return model;
    }


    public Model setupSinglePostPage(int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
        }

        Post post = postRepository.getReferenceById(id);
        post.setVoteCount(voteRepository.countVotesByPostId(post.getId()));

        User postUser = userRepository.getReferenceById(post.getUserId());
        post.setUserName(postUser.getUsername());

        List<Comment> commentList = commentRepository.findAllCommentsByPostId(post.getId());

        model.addAttribute("post", post);

        model.addAttribute("commentList", commentList);
        model.addAttribute("comment", new Comment());

        return model;
    }


    public Model setupEditPostPage(int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

            Post returnPost = postRepository.getReferenceById(id);
            User tempUser = userRepository.getReferenceById(returnPost.getUserId());
            returnPost.setUserName(tempUser.getUsername());
            returnPost.setVoteCount(voteRepository.countVotesByPostId(returnPost.getId()));

            List<Comment> commentList = commentRepository.findAllCommentsByPostId(returnPost.getId());

            model.addAttribute("post", returnPost);
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
            model.addAttribute("commentList", commentList);
            model.addAttribute("comment", new Comment());
        }

        return model;
    }

}
