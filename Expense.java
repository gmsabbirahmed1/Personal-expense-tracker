

import java.util.Date;

public class Expense {
    private int id;
    private String category;
    private double amount;
    private Date date;
    private String description;
    private int userId;  // To associate expense with user

    // Constructors
    public Expense() {
        // Default constructor
    }

    public Expense(String category, double amount, Date date, String description) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Utility method to display expense information
    @Override
    public String toString() {
        return String.format("Expense [ID: %d, Category: %s, Amount: $%.2f, Date: %s, Description: %s, UserID: %d]",
                id, category, amount, date.toString(), description, userId);
    }

    // Additional business logic methods can be added here
    public boolean isValid() {
        return category != null && !category.isEmpty() && amount > 0 && date != null;
    }
}