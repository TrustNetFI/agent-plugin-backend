package fi.trustnet.browserextension.errors;


import org.hyperledger.indy.sdk.IndyException;
import fi.trustnet.browserextension.responses.Response;


import static com.sun.xml.internal.ws.api.message.Packet.Status.Response;
import static fi.trustnet.browserextension.App.getJsonValue;
import static fi.trustnet.browserextension.StatusCodes.ERROR;


public class ExceptionMessage {
    public static String createErrorMessageFromException(Exception e) {
        if (e.getCause() != null && IndyException.class.isAssignableFrom(e.getCause().getClass())) {
            int errCode = ((IndyException)(e.getCause())).getSdkErrorCode();
            String description = ((IndyException)(e.getCause())).getMessage();
            return createErrorResponse(errCode, description);
        }
        return createErrorResponse(702, e.getMessage());
    }

    private static String createErrorResponse(int code, String description) {

        ErrorMessage error = new ErrorMessage(ErrorCode.valueOf(code).toString(), description);
        Response response = new Response(ERROR, error);
        return getJsonValue(response);
    }

}
