# AOP Auth Example

![alt tag](http://devstickers.com/assets/img/pro/d1i3.png)



This example shows how to create a dummy user login with AspectJ, we capture the HttpServlet's method that are decorated
 with the login required annotation.


This is the interface for generating the annotation.
```java

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginRequired {
}

```

Now we create the aspect

```java

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

```
<br>

If the user is invalid we send an Unauthorized response, else we continue with the normal proceed.

<br>

Lets test the servlet!!.

<br>

```java
public class AutenticationRequiredServletTest {

    private AutenticationRequiredServlet authenticationRequiredServlet;
    private HttpServletRequest requestMock;
    private HttpServletResponse responseMock;
    private HttpSession sessionMock;

    @Before
    public void setUp() {
        authenticationRequiredServlet = new AutenticationRequiredServlet();
        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);
        sessionMock = mock(HttpSession.class);
        when(requestMock.getSession()).thenReturn(sessionMock);
    }

    @Test
    public void validUserDoGet_returnsOk() throws Exception {
        when(sessionMock.getAttribute("user")).thenReturn(new User("user", "pass"));
        authenticationRequiredServlet.doGet(requestMock, responseMock);
        verify(responseMock, atLeast(1)).setStatus(SC_OK);
    }

    @Test
    public void invalidUserDoGet_returnsUnauthorized() throws Exception {
        when(sessionMock.getAttribute("user")).thenReturn(new User("unAuthorizedUser", "pass"));
        authenticationRequiredServlet.doGet(requestMock, responseMock);
        verify(responseMock, atLeast(1)).setStatus(SC_UNAUTHORIZED);
    }

    @Test
    public void validUserDoNotLoginRequiredPost_returnsOk() throws Exception {
        when(sessionMock.getAttribute("user")).thenReturn(new User("user", "pass"));
        authenticationRequiredServlet.doPost(requestMock, responseMock);
        verify(responseMock, atLeast(1)).setStatus(SC_OK);
    }

    @Test
    public void invalidUserDoNotLoginRequiredPost_returnsOk() throws Exception {
        when(sessionMock.getAttribute("user")).thenReturn(new User("unAuthorizedUser", "pass"));
        authenticationRequiredServlet.doPost(requestMock, responseMock);
        verify(responseMock, atLeast(1)).setStatus(SC_OK);
    }
}
```
<br>

And here are the result

<br>

```
/usr/lib/jvm/java-8-oracle/bin/java

mar 06, 2017 6:24:17 PM servlet.AutenticationRequiredServlet doGet_aroundBody0
INFORMACIÓN: Method: doGet

mar 06, 2017 6:24:17 PM authentication.UserAuthentication sendUnauthorizedResponse
INFORMACIÓN: Unauthorized Method: doGet

mar 06, 2017 6:24:17 PM servlet.AutenticationRequiredServlet doPost
INFORMACIÓN: Method: doPost

mar 06, 2017 6:24:17 PM servlet.AutenticationRequiredServlet doPost
INFORMACIÓN: Method: doPost

Process finished with exit code 0
```

*_I have reordered the test's output for simplicity_*

This is a very simple example, if you are having problems please take a look to the pom.xml the aspectj's plugins made me 
 blow my top a little bit.
 
 Enjoy!!
 
 

You can find more informatin about AspectJ here → https://eclipse.org/aspectj/