package technology.nrkk.demo.front.webclient;

public class CatalogueServerErrorException extends CatalogueClientException {
    public CatalogueServerErrorException(String message, Exception e) {
        super(message, e);
    }
}
