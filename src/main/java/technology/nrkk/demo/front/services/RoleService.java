package technology.nrkk.demo.front.services;

import com.newrelic.api.agent.NewRelic;
import org.springframework.stereotype.Service;
import technology.nrkk.demo.front.entities.User;

@Service
public class RoleService {
    public String getRole(User user) {
        if (user.getId()%5  == 0) {
            NewRelic.addCustomParameter("Role", "GoldMember");
            return "GoldMember";
        }
        NewRelic.addCustomParameter("Role", "NormalMember");
        return "Normal Member";
    }
}
