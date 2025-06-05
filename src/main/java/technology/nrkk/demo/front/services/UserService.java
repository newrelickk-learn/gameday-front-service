package technology.nrkk.demo.front.services;

import com.newrelic.api.agent.NewRelic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import technology.nrkk.demo.front.entities.Role;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.repositories.UserRepository;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service("userDetailsRepository")
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    RankService rankService;

    protected final static Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserDetails createUser(String name, String username, String email, String password) {
        Role role = new Role("ROLE_USER");
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = new User(name, username, email, password, roles, "9c7ce9df-7a46-4001-b5ba-7792e27f3615");
        userRepo.save(user);
        String rank = rankService.getRank(user);
        logger.info("User rank: " + rank);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),authorities);
    }

    public User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        Optional<User> user = userRepo.findByUsernameOrEmail(username, username);
        logUserInfomation(user);
        if (user.isPresent()) {
            NewRelic.addCustomParameter("user", user.get().getId());
            String rank = rankService.getRank(user.get());
            logger.info("User rank: " + rank);
        }
        return user.get();
    }

    private void logUserInfomation(Optional<User> user) {
        User userData = user.get();
        String userId = String.valueOf(userData.getId());
        String companyId = userData.getCompanyId();
        NewRelic.setUserId(userId);
        NewRelic.addCustomParameter("companyId", companyId);
        logger.info(String.format("User: %s, CompanyId: %s logged in", userId, companyId));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByUsernameOrEmail(username, username);
        NewRelic.ignoreTransaction();
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not exists by Username");
        }
        logUserInfomation(user);
        User userData = user.get();
        String rank = rankService.getRank(user.get());
        logger.info("User rank: " + rank);
        Set<GrantedAuthority> authorities = userData.getRoles().stream()
                .map((role) -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
        org.springframework.security.core.userdetails.User userDetail = new org.springframework.security.core.userdetails.User(username,user.get().getPassword(),authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, authorities);
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(authentication);
        UserDetails userDetails = (UserDetails)ctx.getAuthentication().getPrincipal();
        logger.info("User principal loaded ");
        return org.springframework.security.core.userdetails.User.withUserDetails(userDetails).build();
    }
}
