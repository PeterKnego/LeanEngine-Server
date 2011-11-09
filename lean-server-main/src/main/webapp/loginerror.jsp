<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Login error</title>
    <link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js" type="text/javascript"></script>
</head>
<body style="padding-top: 42px;">

<div class="topbar">
    <div class="topbar-inner">
        <div class="container">
            <a class="brand" href="/">LeanEngine Demo</a>
        </div>
    </div>
</div>

<div class="container">
    <div class="row">
        <div class="span16">
            <div id="loginError" class="alert-message block-message error">
                <a class="close" href="javascript: $('#loginError').hide();window.location='/'">&times;</a>

                <p><strong>Error:</strong> there was an error during login process: </p>
                <ul>
                    <li>error code: <%=request.getParameter("errorcode")%>
                    </li>
                    <li>error message: <%=request.getParameter("errormsg")%>
                    </li>
                </ul>
                <div class="alert-actions">
                    <a class="btn small" href="javascript: $('#loginError').hide();window.location='/'">Close</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>