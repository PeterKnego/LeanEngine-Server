package com.leanengine.server.auth;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginErrorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter writer = response.getWriter();

        writer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n")
                .append("<head>\n")
                .append("<title>Login error</title>\n")
                .append("<link rel=\"stylesheet\" href=\"http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css\">\n")
                .append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js\" type=\"text/javascript\"></script>\n")
                .append("</head>\n")
                .append("<body style=\"padding-top: 42px;\">");

        writer.append("<div class=\"container\"><div class=\"row\"><div class=\"span16\">")
                .append("<div id=\"loginError\" class=\"alert-message block-message error\">\n")
                .append("<a class=\"close\" href=\"javascript: $('#loginError').hide();window.location='/'\">&times;</a>\n")
                .append("<p><strong>Error:</strong> there was an error during login process: </p>\n")
                .append("<ul><li>error code: " + request.getParameter("errorcode") + "</li>\n")
                .append("<li>error message: " + request.getParameter("errormsg") + "</li></ul>\n")
                .append("<div class=\"alert-actions\">\n")
                .append("<a class=\"btn small\" href=\"javascript: $('#loginError').hide();window.location='/'\">Close</a>\n")
                .append("</div></div></div></div></div>\n")
                .append("</body>\n")
                .append("</html>");

        writer.close();
    }
}
