package technology.nrkk.demo.front.webclient;

public class CatalogueClientException extends Exception {
    public CatalogueClientException(String message, Exception e) {
        super(message, e);
    }
}
