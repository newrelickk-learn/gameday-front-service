package technology.nrkk.demo.front.advice;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.TransactionNamePriority;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NewRelicAdvice {

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object start(ProceedingJoinPoint jp) throws Throwable {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, null, jp.getClass().getName(), jp.getSignature().getName());
        System.out.println(NewRelic.getAgent().getTransaction().toString());
        try {
            Object result = jp.proceed();
            return result;

        } catch(Exception e) {
            NewRelic.noticeError(e);
            throw e;
        } finally {
            NewRelic.getAgent().getTransaction().getToken().expire();
        }
    }

}
