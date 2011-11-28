package com.leanengine.server;

public class LeanException extends Throwable {

    public enum Error {
        // reply errors are produced by wrong client parameters
        IllegalEntityName(1, "Illegal LeanEntity name."),
        EmptyEntity(2, "LeanEntity contains no properties."),
        EntityNotFound(3, "Entity not found."),
        ErrorSerializingToJson(4, "Object could not be serialized to JSON"),
        QueryJSON(5, "Query JSON could not be parsed."),
        UnsupportedQueryFilterOperation(6, "Query contains unsupported filter operation: "),
        UnsupportedQuerySortOperation(7, "Query contains unsupported sort operation: "),
        ValueToJSON(8, "Value node could not be converted to a supported type."),

        // user is not authorized to access resource
        // or error during authorization process
        // error codes 100-199
        FacebookAuthError(101, "Facebook authorization error."),
        FacebookAuthParseError(102, "Facebook authorization error."),
        FacebookAuthConnectError(103, "Could not connect to Facebook authorization server."),
        FacebookAuthResponseError(104, "Facebook OAuth server error."),
        FacebookAuthMissingParam(105, "OAuth error: missing parameters in server reply."),
        FacebookAuthNoConnection(106, "Could not connect to Facebook authorization server."),
        FacebookAuthNotEnabled(107, "Server configuration error: Facebook login not enabled."),
        FacebookAuthMissingAppId(108, "Server configuration error: missing Facebook Application ID."),
        FacebookAuthMissingAppSecret(109, "Server configuration error: missing Facebook Application Secret."),
        FacebookAuthMissingCRSF(110, "Facebook OAuth request missing CSRF protection code."),
        OpenIdAuthFailed(111, "OpenID authentication failed."),
        OpenIdAuthNotEnabled(112, "Server configuration error: OpenID login not enabled."),
        NotAuthorized(113, "No account active or account not authorized to access this resource."),
        MissingRedirectUrl(114, "Login request must have URL parameter 'onlogin' used for redirect on successful login."),

        // server errors have codes between 200-299
        // they happen when server has problems fulfilling request
        ScriptExecutionError(201, "Error executing script: "),
        ScriptOutputError(202, "Illegal script result error: custom scripts must produce a Javascript object. Script: "),
        AppEngineMissingIndex(203, "AppEngine query error: missing index. Try running this query on dev server to " +
                "automatically create needed indexes and then upload to production."),
         // this is only produced on client, when server sends malformed error message
         LeanExceptionToJSON(204, "Error parsing error JSON data.");

        public int errorCode;
        public String errorMessage;

        Error(int errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    private int errorCode;

    public LeanException(Error errorType) {
        super(errorType.errorMessage);
        this.errorCode = errorType.errorCode;
    }

    public LeanException(Error errorType, Throwable cause) {
        super(errorType.errorMessage, cause);
        this.errorCode = errorType.errorCode;
    }

    public LeanException(Error errorType, String additionalErrorMessage) {
        super(errorType.errorMessage + additionalErrorMessage);
        this.errorCode = errorType.errorCode;
    }

    public LeanException(Error errorType, String additionalErrorMessage, Throwable cause) {
        super(errorType.errorMessage + additionalErrorMessage, cause);
        this.errorCode = errorType.errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
