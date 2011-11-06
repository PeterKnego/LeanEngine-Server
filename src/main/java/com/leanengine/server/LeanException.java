package com.leanengine.server;

public class LeanException extends Throwable {

    public enum Error {
        // input errors have codes above 100
        // they happen when client sends wrong requests
        IllegalEntityName(101, "Illegal LeanEntity name."),
        EmptyEntity(102, "LeanEntity contains no properties."),
        IllegalEntityFormat(103, "Illegal LeanEntity format."),
        EntityNotFound(104, "Entity not found."),
        EntityToJSON(105, "Entity missing."),
        QueryJSON(105, "Query JSON could not be parsed."),
        UnsupportedQueryFilterOperation(106, "Query contains unsupported filter operation: "),
        UnsupportedQuerySortOperation(107, "Query contains unsupported sort operation: "),
        ValueToJSON(108, "Value node could not be converted to a supported type."),

        // server errors have codes below 100
        // they happen when server has problems fulfilling request
        FacebookAuthError(1, "Facebook authorization error."),
        FacebookAuthParseError(2, "Facebook authorization error."),
        FacebookAuthConnectError(3, "Could not connect to Facebook authorization server."),
        FacebookAuthResponseError(4, "Facebook OAuth server error."),
        FacebookAuthMissingParam(5, "OAuth error: missing parameters in server reply."),
        FacebookAuthNoConnection(6, "Could not connect to Facebook authorization server."),
        FacebookAuthNotEnabled(7, "Server configuration error: Facebook login not enabled."),
        FacebookAuthMissingAppId(8, "Server configuration error: missing Facebook Application ID."),
        FacebookAuthMissingAppSecret(9, "Server configuration error: missing Facebook Application Secret."),
        FacebookAuthMissingCRSF(10, "Facebook OAuth request missing CSRF protection code."),
        OpenIdAuthFailed(11, "OpenID authentication failed."),
        OpenIdAuthNotEnabled(12, "Server configuration error: OpenID login not enabled."),
        ScriptExecutionError(20, "Error executing script: "),
        ScriptOutputError(21, "Illegal script result error: custom scripts must produce a Javascript object. Script: "),
        NotAuthorized(40, "No account active or account not authorized to access this resource."),
        AppEngineMissingIndex(41, "AppEngine query error: missing index.");


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
