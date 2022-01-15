package pl.kacperwleklak.srds.model;

public enum Discount {
    NORMAL(0),
    STUDENT(51),
    DISABLED(60),
    EMPLOYEE(90),
    SPECIAL(100);

    public final int percentage;

    Discount(int percentage) {
        this.percentage = percentage;
    }
}
