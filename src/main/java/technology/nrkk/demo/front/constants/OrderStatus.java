package technology.nrkk.demo.front.constants;

public enum OrderStatus {
    NEW("NEW"), CONFIRM("CONFIRM"), PURCHASE("PURCHASE"), PURCHASED("PURCHASED");
    private final String text;
    private OrderStatus(final String text) {
        this.text = text;
    }
    public String getString() {
        return this.text;
    }
}
