<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>

    <link rel="stylesheet" href="../bootstrap.css">
    <%--<link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css">--%>
    <%--<link href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.css" rel="stylesheet">--%>
    <%--<link href="http://twitter.github.com/bootstrap/assets/css/docs.css" rel="stylesheet">--%>
    <%--<link href="http://twitter.github.com/bootstrap/assets/js/google-code-prettify/prettify.css" rel="stylesheet">--%>

    <script type="text/javascript">
        $.fn.toggleDisabled = function() {
            return this.each(function() {
                this.disabled = !this.disabled;
            });
        }
    </script>
</head>
<body style="padding-top: 20px;">

<div class="row">
    <div class="span12">
        <form>
            <fieldset>
                <legend>LeanEngine Settings</legend>

                <div class="clearfix">
                    <label>Facebook login</label>

                    <div class="input">
                        <ul class="inputs-list">
                            <li>
                                <label>
                                    <input onchange="$('#fbAppId').toggleDisabled();$('#fbAppSecret').toggleDisabled()"
                                           type="checkbox" name="fbLoginEnable" value="option1"/>
                                    <span>enable Facebook OAuth login</span>
                                </label>
                            </li>
                        </ul>
                    </div>
                    <div class="input">
                        <input class="xlarge" id="fbAppId" name="disabledInput" size="30" type="text"
                               placeholder="Facebook Application ID" disabled="disabled" style="margin-top: 5px"/>
                    </div>
                    <div class="input">
                        <input class="xlarge disabled" id="fbAppSecret" name="disabledInput" size="30" type="text"
                               placeholder="Facebook Application Secret" disabled="disabled" style="margin-top: 10px"/>
                    </div>
                </div>

                <div class="clearfix">
                    <label id="openIdLogin">OpenID login</label>

                    <div class="input">
                        <ul class="inputs-list">
                            <li>
                                <label>
                                    <input type="checkbox" name="openIdLoginEnable" value="option1"/>
                                    <span>enable OpenID login</span>
                                </label>
                            </li>
                        </ul>
                    </div>
                </div>


                <div class="actions">
                    <input type="submit" class="btn primary" value="Save changes">&nbsp;
                    <button type="reset" class="btn">Cancel</button>
                </div>
            </fieldset>
        </form>
    </div>
</div>

</body>
</html>