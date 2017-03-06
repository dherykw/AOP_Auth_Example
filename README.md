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
/usr/lib/jvm/java-8-oracle/bin/java -ea -Didea.launcher.port=7532 -Didea.launcher.bin.path=/home/dher/idea-IU-163.10154.41/bin -Dfile.encoding=UTF-8 -classpath /home/dher/idea-IU-163.10154.41/lib/idea_rt.jar:/home/dher/idea-IU-163.10154.41/plugins/junit/lib/junit-rt.jar:/usr/lib/jvm/java-8-oracle/jre/lib/charsets.jar:/usr/lib/jvm/java-8-oracle/jre/lib/deploy.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/cldrdata.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/dnsns.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/jaccess.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/jfxrt.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/localedata.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/nashorn.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/sunec.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/sunjce_provider.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/sunpkcs11.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/zipfs.jar:/usr/lib/jvm/java-8-oracle/jre/lib/javaws.jar:/usr/lib/jvm/java-8-oracle/jre/lib/jce.jar:/usr/lib/jvm/java-8-oracle/jre/lib/jfr.jar:/usr/lib/jvm/java-8-oracle/jre/lib/jfxswt.jar:/usr/lib/jvm/java-8-oracle/jre/lib/jsse.jar:/usr/lib/jvm/java-8-oracle/jre/lib/management-agent.jar:/usr/lib/jvm/java-8-oracle/jre/lib/plugin.jar:/usr/lib/jvm/java-8-oracle/jre/lib/resources.jar:/usr/lib/jvm/java-8-oracle/jre/lib/rt.jar:/home/dher/IdeaProjects/AOPAuthTest/target/test-classes:/home/dher/IdeaProjects/AOPAuthTest/target/classes:/home/dher/.m2/repository/org/aspectj/aspectjrt/1.8.10/aspectjrt-1.8.10.jar:/home/dher/.m2/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar:/home/dher/.m2/repository/javax/ws/rs/javax.ws.rs-api/2.0/javax.ws.rs-api-2.0.jar:/home/dher/.m2/repository/org/glassfish/jersey/core/jersey-common/2.25/jersey-common-2.25.jar:/home/dher/.m2/repository/javax/annotation/javax.annotation-api/1.2/javax.annotation-api-1.2.jar:/home/dher/.m2/repository/org/glassfish/jersey/bundles/repackaged/jersey-guava/2.25/jersey-guava-2.25.jar:/home/dher/.m2/repository/org/glassfish/hk2/hk2-api/2.5.0-b30/hk2-api-2.5.0-b30.jar:/home/dher/.m2/repository/org/glassfish/hk2/hk2-utils/2.5.0-b30/hk2-utils-2.5.0-b30.jar:/home/dher/.m2/repository/org/glassfish/hk2/external/aopalliance-repackaged/2.5.0-b30/aopalliance-repackaged-2.5.0-b30.jar:/home/dher/.m2/repository/org/glassfish/hk2/external/javax.inject/2.5.0-b30/javax.inject-2.5.0-b30.jar:/home/dher/.m2/repository/org/glassfish/hk2/hk2-locator/2.5.0-b30/hk2-locator-2.5.0-b30.jar:/home/dher/.m2/repository/org/javassist/javassist/3.20.0-GA/javassist-3.20.0-GA.jar:/home/dher/.m2/repository/org/glassfish/hk2/osgi-resource-locator/1.0.1/osgi-resource-locator-1.0.1.jar:/home/dher/.m2/repository/junit/junit/4.12/junit-4.12.jar:/home/dher/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/dher/.m2/repository/org/mockito/mockito-core/2.1.0/mockito-core-2.1.0.jar:/home/dher/.m2/repository/net/bytebuddy/byte-buddy/1.4.26/byte-buddy-1.4.26.jar:/home/dher/.m2/repository/net/bytebuddy/byte-buddy-agent/1.4.26/byte-buddy-agent-1.4.26.jar:/home/dher/.m2/repository/org/objenesis/objenesis/2.4/objenesis-2.4.jar com.intellij.rt.execution.application.AppMain com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 servlet.AutenticationRequiredServletTest

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