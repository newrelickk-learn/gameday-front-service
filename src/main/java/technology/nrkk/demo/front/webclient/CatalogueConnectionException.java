package technology.nrkk.demo.front.webclient;

public class CatalogueConnectionException extends CatalogueClientException {
    public CatalogueConnectionException(String message, Exception e) {
        super(message, e);
    }
}
