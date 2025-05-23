package technology.nrkk.demo.front.services;

import com.newrelic.api.agent.NewRelic;
import org.springframework.stereotype.Service;
import technology.nrkk.demo.front.entities.User;

@Service
public class RankService {
    public String getRank(User user) {
        if (user.getId()%5  == 0) {
            NewRelic.addCustomParameter("memberRank", "GoldMember");
            return "GoldMember";
        }
        NewRelic.addCustomParameter("memberRank", "NormalMember");
        return "Normal Member";
    }
}
