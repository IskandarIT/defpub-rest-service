package gk.defpub.restservice.service.impl;

import gk.defpub.restservice.model.LoginUser;
import gk.defpub.restservice.model.Role;
import gk.defpub.restservice.repository.UserRepository;
import gk.defpub.restservice.model.User;
import gk.defpub.restservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * UserServiceImpl class.
 * <p>
 * Date: Nov 2, 2018
 * <p>
 *
 * @author Gleb Kosteiko
 */
@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getEncryptedPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return authorities;
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        userRepository.findAll().forEach(list::add);
        return list;
    }

    @Override
    public void delete(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findOne(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User save(LoginUser user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEncryptedPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setRole(Role.USER);
        return userRepository.save(newUser);
    }
}
