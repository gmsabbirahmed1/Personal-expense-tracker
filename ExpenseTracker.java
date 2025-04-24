

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseTracker extends JFrame {
    private User currentUser;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JTextField categoryField, amountField, descriptionField;
    private JButton addButton, deleteButton, logoutButton;
    private JSpinner dateSpinner;
    private JComboBox<String> viewModeComboBox;
    private JButton applyFilterButton;
    private JSpinner monthYearSpinner;
    private JSpinner daySpinner;
    private JLabel welcomeLabel;

    public ExpenseTracker(User user) {
        this.currentUser = user;
        setTitle("Expense Tracker - " + user.getFullName());
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadAllExpenses();
    }

    private void initComponents() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Header panel with welcome message and logout
        JPanel headerPanel = new JPanel(new BorderLayout());
        welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("View Options"));
        
        viewModeComboBox = new JComboBox<>(new String[]{"All Expenses", "Daily", "Monthly", "By Category"});
        viewModeComboBox.addActionListener(this::updateFilterUI);
        filterPanel.add(new JLabel("View Mode:"));
        filterPanel.add(viewModeComboBox);

        // Date filter components
        monthYearSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor monthYearEditor = new JSpinner.DateEditor(monthYearSpinner, "MM/yyyy");
        monthYearSpinner.setEditor(monthYearEditor);
        monthYearSpinner.setVisible(false);
        filterPanel.add(new JLabel("Month/Year:"));
        filterPanel.add(monthYearSpinner);

        daySpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dayEditor = new JSpinner.DateEditor(daySpinner, "dd/MM/yyyy");
        daySpinner.setEditor(dayEditor);
        daySpinner.setVisible(false);
        filterPanel.add(new JLabel("Date:"));
        filterPanel.add(daySpinner);

        applyFilterButton = new JButton("Apply Filter");
        applyFilterButton.addActionListener(this::applyFilter);
        filterPanel.add(applyFilterButton);

        // Summary button
        JButton summaryButton = new JButton("Show Summary");
        summaryButton.addActionListener(this::showSummary);
        filterPanel.add(summaryButton);

        mainPanel.add(filterPanel, BorderLayout.CENTER);

        // Form panel for adding new expenses
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Expense"));
        mainPanel.add(formPanel, BorderLayout.WEST);

        // Form fields
        formPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        formPanel.add(amountField);

        formPanel.add(new JLabel("Date:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        formPanel.add(dateSpinner);

        formPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        formPanel.add(descriptionField);

        // Buttons
        addButton = new JButton("Add Expense");
        addButton.addActionListener(this::addExpense);
        formPanel.add(addButton);

        deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(this::deleteExpense);
        formPanel.add(deleteButton);

        // Table for displaying expenses
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Category", "Amount", "Date", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
    }

    private void updateFilterUI(ActionEvent e) {
        String viewMode = (String) viewModeComboBox.getSelectedItem();
        monthYearSpinner.setVisible("Monthly".equals(viewMode));
        daySpinner.setVisible("Daily".equals(viewMode));
    }

    private void applyFilter(ActionEvent e) {
        String viewMode = (String) viewModeComboBox.getSelectedItem();
        
        switch (viewMode) {
            case "All Expenses":
                loadAllExpenses();
                break;
            case "Daily":
                loadDailyExpenses((Date) daySpinner.getValue());
                break;
            case "Monthly":
                loadMonthlyExpenses((Date) monthYearSpinner.getValue());
                break;
            case "By Category":
                showCategorySummary();
                break;
        }
    }

    private void loadAllExpenses() {
        tableModel.setRowCount(0);
        List<Expense> expenses = ExpenseDAO.getExpensesByUser(currentUser.getId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
                expense.getId(),
                expense.getCategory(),
                String.format("$%.2f", expense.getAmount()),
                dateFormat.format(expense.getDate()),
                expense.getDescription()
            });
        }
    }

    private void loadDailyExpenses(Date date) {
        tableModel.setRowCount(0);
        List<Expense> expenses = ExpenseDAO.getExpensesByUserAndDate(currentUser.getId(), date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
                expense.getId(),
                expense.getCategory(),
                String.format("$%.2f", expense.getAmount()),
                dateFormat.format(expense.getDate()),
                expense.getDescription()
            });
        }
    }

    private void loadMonthlyExpenses(Date date) {
        tableModel.setRowCount(0);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        
        List<Expense> expenses = ExpenseDAO.getExpensesByUserAndMonth(currentUser.getId(), year, month);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
                expense.getId(),
                expense.getCategory(),
                String.format("$%.2f", expense.getAmount()),
                dateFormat.format(expense.getDate()),
                expense.getDescription()
            });
        }
    }

    private void showCategorySummary() {
        tableModel.setRowCount(0);
        List<Object[]> summary = ExpenseDAO.getCategorySummary(currentUser.getId());
        
        for (Object[] row : summary) {
            tableModel.addRow(new Object[]{
                null, // No ID for summaries
                row[0], // Category
                String.format("$%.2f", row[1]), // Total amount
                "N/A", // No date
                "Category Total" // Description
            });
        }
    }

    private void showSummary(ActionEvent e) {
        double total = ExpenseDAO.getTotalExpenses(currentUser.getId());
        List<Object[]> summary = ExpenseDAO.getCategorySummary(currentUser.getId());
        
        StringBuilder message = new StringBuilder();
        message.append("Expense Summary\n\n");
        message.append(String.format("Total Expenses: $%.2f\n\n", total));
        message.append("By Category:\n");
        
        for (Object[] row : summary) {
            message.append(String.format("%s: $%.2f\n", row[0], row[1]));
        }
        
        JOptionPane.showMessageDialog(this, 
            message.toString(), 
            "Expense Summary", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void addExpense(ActionEvent e) {
        String category = categoryField.getText().trim();
        String amountStr = amountField.getText().trim();
        Date date = (Date) dateSpinner.getValue();
        String description = descriptionField.getText().trim();

        if (category.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Category and Amount are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            Expense expense = new Expense(category, amount, date, description);
            expense.setUserId(currentUser.getId());

            if (ExpenseDAO.addExpense(expense)) {
                applyFilter(null); // Reload with current filter
                categoryField.setText("");
                amountField.setText("");
                descriptionField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to add expense!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExpense(ActionEvent e) {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an expense to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        if (ExpenseDAO.deleteExpense(id, currentUser.getId())) {
            applyFilter(null); // Reload with current filter
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to delete expense!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginForm().setVisible(true);
        }
    }
}