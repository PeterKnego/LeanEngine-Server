<%@ page import="com.leanengine.server.LeanEngineSettings" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ This software is released under the BSD license. For full license see License-library.txt file.
  ~
  ~ Copyright (c) 2011, 2012, Peter Knego
  ~ All rights reserved.
  --%>

<!DOCTYPE html>
<html lang="en">
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js" type="text/javascript"></script>
    <script src="https://netdna.bootstrapcdn.com/twitter-bootstrap/2.1.1/js/bootstrap.min.js" type="text/javascript"></script>
    <link rel="stylesheet" href="https://netdna.bootstrapcdn.com/twitter-bootstrap/2.1.1/css/bootstrap-combined.min.css">

    <script type="text/javascript">


    </script>
</head>

<%
    boolean fbLoginEnable, openIdLoginEnable;
    String fbAppId, fbAppSecret;
    boolean saved = false;

    // Was the form submitted?
    if (request.getParameter("process") != null) {
        fbLoginEnable = request.getParameter("fbLoginEnable") != null;
        openIdLoginEnable = request.getParameter("openIdLoginEnable") != null;
        fbAppId = request.getParameter("fbAppId");
        fbAppSecret = request.getParameter("fbAppSecret");

        LeanEngineSettings.Builder settingsBuilder = new LeanEngineSettings.Builder();
        if (fbLoginEnable) {
            settingsBuilder.add("fbLoginEnable", fbLoginEnable);
            settingsBuilder.add("fbAppId", fbAppId);
            settingsBuilder.add("fbAppSecret", fbAppSecret);
        }
        if (openIdLoginEnable) {
            settingsBuilder.add("openIdLoginEnable", openIdLoginEnable);
        }
        settingsBuilder.save();
        saved = true;

    } else {
        // load settings
        Map<String, Object> settings = LeanEngineSettings.getSettings();
        fbLoginEnable = settings.get("fbLoginEnable") != null && settings.get("fbLoginEnable").equals(true);
        openIdLoginEnable = settings.get("openIdLoginEnable") != null && settings.get("openIdLoginEnable").equals(true);
        fbAppId = settings.get("fbAppId") != null ? (String) settings.get("fbAppId") : "";
        fbAppSecret = settings.get("fbAppSecret") != null ? (String) settings.get("fbAppSecret") : "";
    }
%>

<body style="padding-top: 20px;" <%=saved ? "onload='saved()'" : ""%>>

<div class="row">
    <div class="span12">
        <form class="bs-docs-example form-horizontal">
            <legend>Google</legend>
            <div class="control-group">
                <label class="control-label" for="googleKey">Key</label>

                <div class="controls">
                    <input type="text" id="googleKey" placeholder="Key">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="googleSecret">Secret</label>

                <div class="controls">
                    <input type="password" id="googleSecret" placeholder="Secret">
                </div>
            </div>

            <legend>Twitter</legend>
            <div class="control-group">
                <label class="control-label" for="twitterKey">Key</label>

                <div class="controls">
                    <input type="text" id="twitterKey" placeholder="Key">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="twitterSecret">Secret</label>

                <div class="controls">
                    <input type="password" id="twitterSecret" placeholder="Secret">
                </div>
            </div>

            <legend>Facebook</legend>
            <div class="control-group">
                <label class="control-label" for="facebookKey">Key</label>

                <div class="controls">
                    <input type="text" id="facebookKey" placeholder="Key">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="facebookSecret">Secret</label>

                <div class="controls">
                    <input type="password" id="facebookSecret" placeholder="Secret">
                </div>
            </div>

            <input type="HIDDEN" name="process" value="true">
        </form>

        <div class="form-actions">
            <button onclick="checkAndSubmit()" class="btn btn-primary">Save changes</button>
            <button type="reset" class="btn">Cancel</button>
            &nbsp;
        </div>
        <div id="fbMissingDataError" style="display: none" class="alert-message error">
            <a class="close" href="#">&times;</a>

            <p><strong>Error: </strong> Missing Facebook Application ID & Secret.</p>
        </div>
        <div id="savedNotification" style="display: none" class="alert-message warning">
            <p>Settings saved..</p>
        </div>

    </div>
</div>

</body>
</html>
