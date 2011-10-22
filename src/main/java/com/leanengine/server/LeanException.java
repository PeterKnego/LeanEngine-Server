package com.leanengine.server;

public class LeanException extends Throwable {

    public enum Error {
        // inout errors have codes above 100
        // they happen when client sends wrong requests
        IllegalEntityName(101, "Illegal LeanEntity name."),
        EmptyEntity(102, "LeanEntity contains no properties."),
        IllegalEntityFormat(103, "Illegal LeanEntity format."),
        EntityNotFound(104, "Entity not found."),
        EntityToJSON(105, "Entity missing "),

        // server errors have codes below 100
        // they happen when server has problems fulfilling request
        FacebookAuthError(12, "Facebook authorization error."),
        FacebookAuthParseError(13, "Facebook authorization error."),
        FacebookAuthConnectError(14, "Could not connect to Facebook authorization server."),
        FacebookAuthResponseError(4, "Facebook OAuth server error."),
        FacebookAuthMissingParamError(5, "OAuth error: missing parameters in server reply."),
        FacebookAuthNoConnectionError(8, "Could not connect to Facebook authorization server."),
        ScriptExecutionError(15, "Error executing script: "),
        ScriptOutputError(16, "Illegal result error: custom scripts must produce a Javascript object. Script: ");

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
