package technology.nrkk.demo.front.advice;

import com.newrelic.api.agent.NewRelic;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import technology.nrkk.demo.front.webclient.CatalogueClient;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CatalogueClient.CatalogueClientException.class)
    public ResponseEntity<String> handleCatalogueClientException(CatalogueClient.CatalogueClientException ex) {
        NewRelic.noticeError(ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}