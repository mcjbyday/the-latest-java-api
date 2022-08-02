package com.thelatest.controller;

import com.thelatest.model.Post;
import com.thelatest.model.User;
import com.thelatest.repository.UserRepository;
import com.thelatest.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    // a flavor of dependency injection -
    // scan project for objects requiring instantiation to properly execute a given class or method
    @Autowired
    UserRepository repository;

    @Autowired
    VoteRepository voteRepository;


    // findAll on repository object returns list of users assigned to userList var
    // getPosts() function called for every User, assigning to the variable u inside userList
    // then iterate over each post, invoking the setVoteCount() method with countVotesByPostId() method as args
    // finally use getId() to obtain the id of the post
    @GetMapping("/api/users")
    public List<User> getAllUsers() {
        List<User> userList = repository.findAll();
        for (User u : userList) {
            List<Post> postList = u.getPosts();
            for (Post p : postList) {
                p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
            }
        }
        return userList;
    }
    // similar to above, but only returns a particular user not a full list of users
    @GetMapping("/api/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        User returnUser = repository.getReferenceById(id);
        List<Post> postList = returnUser.getPosts();
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
        }

        return returnUser;
    }

    @PostMapping("/api/users")
    public User addUser(@RequestBody User user) {
        // Encrypt password
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        repository.save(user);
        return user;
    }

    @PutMapping("/api/users/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        User tempUser = repository.getReferenceById(id);

        if (!tempUser.equals(null)) {
            user.setId(tempUser.getId());
            repository.save(user);
        }
        return user;
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id) {
        repository.deleteById(id);
    }

}

