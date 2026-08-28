package technology.nrkk.demo.front.webclient;

public class CatalogueResponseParseException extends CatalogueClientException {
    public CatalogueResponseParseException(String message, Exception e) {
        super(message, e);
    }
}
