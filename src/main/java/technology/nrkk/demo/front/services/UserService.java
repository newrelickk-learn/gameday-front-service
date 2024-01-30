package technology.nrkk.demo.front.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.entities.Role;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.repositories.UserRepository;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service("userDetailsRepository")
public class UserService implements ReactiveUserDetailsService {

    @Autowired
    UserRepository userRepo;

    public Mono<UserDetails> createUser(String name, String username, String email, String password) {
        Role role = new Role("ROLE_USER");
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = new User(name, username, email, password, roles);
        userRepo.save(user);
        return Mono.just(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),authorities));
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByUsernameOrEmail(username, username);
        if(user.isEmpty()){
            new UsernameNotFoundException("User not exists by Username");
        }
        Set<GrantedAuthority> authorities = user.get().getRoles().stream()
            .map((role) -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toSet());
        org.springframework.security.core.userdetails.User userDetail = new org.springframework.security.core.userdetails.User(username,user.get().getPassword(),authorities);
        /*
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, authorities);
        Mono<org.springframework.security.core.userdetails.UserDetails> userDetailRes = ReactiveSecurityContextHolder.getContext().map(ctx -> {
            ctx.setAuthentication(authentication);
            return (org.springframework.security.core.userdetails.UserDetails) ctx.getAuthentication().getPrincipal();
        });
         */
        return Mono.just(org.springframework.security.core.userdetails.User.withUserDetails(userDetail).build());
    }

    public User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userRepo.findByUsernameOrEmail(username, username);
        return user.get();
    }
}
