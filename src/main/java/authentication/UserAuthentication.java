package authentication;

import model.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;


@Aspect
public class UserAuthentication {

    private static final Logger LOG = Logger.getLogger(UserAuthentication.class.getName());

    private static final int HTTP_REQUEST = 0;
    private static final int HTTP_RESPONSE = 1;

    @Around("execution(protected void do*(..)) && @annotation(LoginRequired)")
    public void around(ProceedingJoinPoint method) throws Throwable {
        HttpServletRequest request = (HttpServletRequest) method.getArgs()[HTTP_REQUEST];
        User user = getUser(request);

        if (!isValidUser(user))
            sendUnauthorizedResponse(method);
        else
            method.proceed();
    }

    private void sendUnauthorizedResponse(ProceedingJoinPoint method) {
        LOG.info("Unauthorized Method: " + method.getSignature().getName());
        HttpServletResponse response = (HttpServletResponse) method.getArgs()[HTTP_RESPONSE];
        response.setStatus(SC_UNAUTHORIZED);
    }

    private User getUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }

    private boolean isValidUser(User user) {
        return user != null && user.getName().equals("user") && user.getPass().equals("pass");
    }
}

