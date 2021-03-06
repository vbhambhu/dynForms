package kennedy.ox.ac.uk.Services;

import kennedy.ox.ac.uk.Models.ActivityLog;
import kennedy.ox.ac.uk.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    MongoOperations mongoOperation;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        ActivityLog log = new ActivityLog(username,"Login attempt");
        mongoOperation.save(log);

        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        User user =  mongoOperation.findOne(query, User.class);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        //do check if account expired / suspended / deleted
        Boolean isAcconutExpired = false;

        Date today = new Date();
        if(user.getAcconutExpireDate() != null && today.before(user.getAcconutExpireDate()) ){
            isAcconutExpired = true;
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), !user.isDeleted(), !isAcconutExpired ,
                true, !user.isLocked(), getAuthorities(user));

    }

    private List<GrantedAuthority> getAuthorities(User user) {
        List<String> roles = user.getRoles();
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
