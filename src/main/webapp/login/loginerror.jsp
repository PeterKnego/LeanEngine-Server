<%@ page import="java.net.URLDecoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Login error</title></head>
<body>
Login was unsuccessful.
<br/>
error code: <%= request.getParameter("errorcode")%>
<br/>
error message: <%= URLDecoder.decode(request.getParameter("errormsg"), "UTF-8")%>
</body>
</html>