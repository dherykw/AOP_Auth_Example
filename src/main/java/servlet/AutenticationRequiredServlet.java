package servlet;

import authentication.LoginRequired;
import org.junit.Before;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * Created by dher on 4/03/17.
 */
public class AutenticationRequiredServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(AutenticationRequiredServlet.class.getName());

    @Override
    @LoginRequired
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Method: doGet");
        resp.setStatus(SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Method: doPost");
        resp.setStatus(SC_OK);
    }
}
