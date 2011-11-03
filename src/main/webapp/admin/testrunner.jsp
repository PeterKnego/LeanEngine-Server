<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js" type="text/javascript"></script>
    <link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css">
</head>
<body style="padding-top: 20px;">
<div class="row">
    <div class="span12">
        <h3>Note: unit tests should only be run on development or staging servers.</h3>
        <br/>
        <table>
            <thead>
            <tr>
                <th>Test description</th>
                <th>success</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>Create test user<br/>another line</td>
                <td>Start</td>
            </tr>
            <tr>
                <td>Sixpack</td>
                <td>English</td>
            </tr>
            <tr>
                <td>Dent</td>
                <td>Code</td>
            </tr>
            </tbody>
        </table>
        <div class="actions">
            <button onclick="checkAndSubmit()" class="btn primary">Start</button>
            <button type="reset" class="btn">Cancel</button>
            &nbsp;
        </div>
    </div>
</div>

</body>
</html>