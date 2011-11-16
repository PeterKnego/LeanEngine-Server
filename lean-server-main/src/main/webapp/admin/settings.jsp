<%@ page import="com.leanengine.server.LeanEngineSettings" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ This software is released under the BSD license. For full license see License-library.txt file.
  ~
  ~ Copyright (c) 2011, Peter Knego & Matjaz Tercelj
  ~ All rights reserved.
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js" type="text/javascript"></script>

    <link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css">

    <script type="text/javascript">
        $.fn.disableFunc = function(val) {
            return this.each(function() {
                this.disabled = val;
            });
        };

        checkAndSubmit = function() {
            if ($('#fbLoginEnable').is(':checked') && ($("#fbAppId").val() == "" || $("#fbAppSecret").val() == "")) {
                $("#fbMissingDataError").show("fast").delay(3000).hide("fast");
            } else {
                document.forms["settingsForm"].submit();
            }
        };

        checkFbLoginEnabled = function() {
            var dis = !$('#fbLoginEnable').is(':checked');
            $('#fbAppId').disableFunc(dis);
            $('#fbAppSecret').disableFunc(dis);
        };

        saved = function() {
            $('#savedNotification').show("fast").delay(3000).hide("fast");
        };
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
        fbLoginEnable = settings.get("fbLoginEnable") != null;
        openIdLoginEnable = settings.get("openIdLoginEnable") != null;
        fbAppId = settings.get("fbAppId") != null ? (String) settings.get("fbAppId") : "";
        fbAppSecret = settings.get("fbAppSecret") != null ? (String) settings.get("fbAppSecret") : "";
    }
%>

<body style="padding-top: 20px;" <%=saved ? "onload='saved()'" : ""%>>

<div class="row">
    <div class="span12">
        <form id="settingsForm" action="<%= request.getRequestURI() %>" method="POST">
            <fieldset>
                <legend>LeanEngine Settings</legend>

                <div class="clearfix">
                    <label>Facebook login</label>

                    <div class="input">
                        <ul class="inputs-list">
                            <li>
                                <label>
                                    <input id="fbLoginEnable" onchange="checkFbLoginEnabled()"
                                           type="checkbox" name="fbLoginEnable" <%=fbLoginEnable ? "checked" : ""%>/>
                                    <span>enable Facebook OAuth login</span>
                                </label>
                            </li>
                        </ul>
                    </div>
                    <div class="input">
                        <input class="xlarge" id="fbAppId" name="fbAppId" size="30" type="text"
                               value="<%=fbAppId%>"
                                <%=fbLoginEnable ? "" : "disabled='disabled'" %>
                               placeholder="Facebook Application ID" style="margin-top: 5px"/>
                    </div>
                    <div class="input">
                        <input class="xlarge disabled" id="fbAppSecret" name="fbAppSecret" size="30" type="text"
                               value="<%=fbAppSecret%>"
                                <%=fbLoginEnable ? "" : "disabled='disabled'"%>
                               placeholder="Facebook Application Secret" style="margin-top: 10px"/>
                    </div>
                </div>

                <div class="clearfix">
                    <label id="openIdLogin">OpenID login</label>

                    <div class="input">
                        <ul class="inputs-list">
                            <li>
                                <label>
                                    <input type="checkbox" name="openIdLoginEnable"
                                            <%=openIdLoginEnable ? "checked" : ""%>/>
                                    <span>enable OpenID login</span>
                                </label>
                            </li>
                        </ul>
                    </div>
                </div>
                <input type="HIDDEN" name="process" value="true">

            </fieldset>
        </form>
        <div class="actions">
            <button onclick="checkAndSubmit()" class="btn primary">Save changes</button>
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