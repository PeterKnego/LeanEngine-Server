package com.leanengine.server.auth;

import com.leanengine.server.LeanException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class FacebookMockLogin {

    protected static void showForm(HttpServletRequest request, HttpServletResponse response, boolean isMobile) throws ServletException, IOException {

        PrintWriter writer = response.getWriter();

        writer.append("<html>\n").append("<body>\n")
                .append("<form method='get' style='text-align:center; font:13px sans-serif'>\n")
                .append("<div style='width: 20em; margin: 1em auto; text-align: left; padding: 0 2em 1.25em 2em; background-color: #d6e9f8; border: 2px solid #67a7e3'>\n")
                .append("<h3>Facebook mock login</h3>\n")
                .append("<p style='padding: 0; margin: 0'>\n")
                .append("<label for='email' style='width: 3em'>Email:</label>\n")
                .append(" <input type='text' name='email' id='email' value='test@example.com'>\n")
                .append("</p>\n").append("<p style='margin: .5em 0 0 3em; font-size:12px'>\n")
                .append("</p>\n").append("<input type='hidden' name='continue' value='error'>\n")
                .append("<p style='margin-left: 3em;'>\n");

        if (isMobile) writer.append("<input name='type' type='hidden' value='mobile'>\n");

        writer.append("<input name='action' type='submit' value='Log In'>\n")
                .append("<input name='action' type='submit' value='Log Out'>\n")
                .append("</p>\n")
                .append("</div>\n")
                .append("</form>\n")
                .append("</body>\n")
                .append("</html>");

        writer.close();
    }

    protected static void login(HttpServletRequest request, HttpServletResponse response,
                                String email, Scheme scheme, String redirectUrl, String errorUrl) throws IOException {
        // authenticate with Facebook Graph OAuth API
        AuthToken lean_token = AuthService.createMockFacebookAccount(email);

        // save token in session
        HttpSession session = request.getSession();
        session.setAttribute("lean_token", lean_token.token);

        //send lean_token back to browser
        try {
            response.sendRedirect(scheme.getUrl(lean_token.token, redirectUrl));
        } catch (LeanException e) {
            response.sendRedirect(scheme.getErrorUrl(e, errorUrl));
        }

    }
}
