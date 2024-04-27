package fu.hbs.constant;

public enum TransactionMessage {
    PRE_PAY("Trả trước đặt phòng"),
    PAY("Thanh toán đặt phòng"),
    REFUND("Hoàn tiền đặt phòng"),
    CANCEL("Huỷ đặt phòng");

    private final String message;

    TransactionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}