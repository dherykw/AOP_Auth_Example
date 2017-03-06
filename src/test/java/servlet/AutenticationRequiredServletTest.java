package servlet;

import model.User;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.*;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.mockito.Mockito.*;

/**
 * Created by dher on 4/03/17.
 */
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
