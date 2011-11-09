package com.leanengine.server.auth;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("lean_token");
        if (token != null) AuthService.resetCurrentAuthData();
        session.removeAttribute("lean_token");

        String redirectUrl;
        if (request.getParameter("redirect") != null) {
            redirectUrl = request.getParameter("redirect");
        } else if (request.getHeader("Referer") != null) {
            redirectUrl = request.getHeader("Referer");
        } else {
            redirectUrl = "/";
        }

        response.sendRedirect(redirectUrl);
    }
}
