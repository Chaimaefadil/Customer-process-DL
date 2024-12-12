public class Order {
    private int id;
    private double amount;
    private int customerId;
    private long date;

    public Order(int id, long date, double amount, int customerId) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.customerId = customerId;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
