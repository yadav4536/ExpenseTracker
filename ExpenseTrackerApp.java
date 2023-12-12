import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;


class Expense {
    private String description;
    private double amount;
    private String category;
    private Date date;

    public Expense(String description, double amount, String category, Date date) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }
}

class Budget {
    private String category;
    private double limit;

    public Budget(String category, double limit) {
        this.category = category;
        this.limit = limit;
    }

    public String getCategory() {
        return category;
    }

    public double getLimit() {
        return limit;
    }
}

class ExpenseTracker {
    private List<Expense> expenses;  // Initialize the expenses list
    private List<Budget> budgets;
    private double totalSpending;

    public ExpenseTracker() {
        this.expenses = new ArrayList<>();  // Initialize the expenses list in the constructor
        this.budgets = new ArrayList<>();
    }

    public void logExpense(String description, double amount, String category, Date date) {
        Expense expense = new Expense(description, amount, category, date);
        expenses.add(expense);
        totalSpending += amount;
    }

    public void setBudget(String category, double limit) {
        Budget budget = new Budget(category, limit);
        budgets.add(budget);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public double getTotalSpending() {
        return totalSpending;
    }

    public List<Budget> getBudgets() {
        return budgets;
    }

    public double getCategorySpending(String category) {
        double spending = expenses.stream()
                .filter(expense -> expense.getCategory().equals(category))
                .mapToDouble(Expense::getAmount)
                .sum();
        return spending;
    }

    public boolean hasExceededBudget(String category) {
        double categorySpending = getCategorySpending(category);
        double budgetLimit = budgets.stream()
                .filter(budget -> budget.getCategory().equals(category))
                .mapToDouble(Budget::getLimit)
                .findFirst()
                .orElse(Double.MAX_VALUE);
        return categorySpending > budgetLimit;
    }
}


class ExpenseTrackerGUI extends JFrame {
    private ExpenseTracker expenseTracker;

    private JTextField descriptionField;
    private JTextField amountField;
    private JTextField categoryField;
    private JTextField dateField;

    private JTextArea expenseTextArea;
    private JLabel totalSpendingLabel;

    private JTextField budgetCategoryField;
    private JTextField budgetLimitField;
    private JButton setBudgetButton;

    public ExpenseTrackerGUI() {
        expenseTracker = new ExpenseTracker();
        setupUI();
    }

    private void setupUI() {
        setTitle("Expense Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 2));

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionField = new JTextField();
        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        categoryField = new JTextField();
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        dateField = new JTextField();

        JButton logExpenseButton = new JButton("Log Expense");
        logExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logExpense();
            }
        });

        expenseTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(expenseTextArea);

        totalSpendingLabel = new JLabel("Total Spending: $0.00");

        JLabel budgetLabel = new JLabel("Set Budget:");
        budgetCategoryField = new JTextField();
        budgetLimitField = new JTextField();
        setBudgetButton = new JButton("Set Budget");
        setBudgetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBudget();
            }
        });

        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(categoryLabel);
        panel.add(categoryField);
        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(logExpenseButton);
        panel.add(new JLabel()); // Placeholder
        panel.add(new JLabel("Expenses:"));
        panel.add(scrollPane);
        panel.add(new JLabel()); // Placeholder
        panel.add(totalSpendingLabel);
        panel.add(budgetLabel);
        panel.add(budgetCategoryField);
        panel.add(budgetLimitField);
        panel.add(setBudgetButton);

        getContentPane().add(panel);
        setVisible(true);
    }

    private void logExpense() {
        try {
            String description = descriptionField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());

            expenseTracker.logExpense(description, amount, category, date);
            updateExpenseTextArea();
            updateTotalSpendingLabel();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Expense logged successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid values.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExpenseTextArea() {
        StringBuilder stringBuilder = new StringBuilder();
        List<Expense> expenses = expenseTracker.getExpenses();
        for (Expense expense : expenses) {
            stringBuilder.append(expense.getDescription()).append(": $").append(expense.getAmount()).append(" (")
                    .append(expense.getCategory()).append(", ").append(new SimpleDateFormat("yyyy-MM-dd").format(expense.getDate()))
                    .append(")\n");
        }
        expenseTextArea.setText(stringBuilder.toString());
    }

    private void updateTotalSpendingLabel() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        totalSpendingLabel.setText("Total Spending: $" + decimalFormat.format(expenseTracker.getTotalSpending()));
    }

    private void setBudget() {
        try {
            String category = budgetCategoryField.getText();
            double limit = Double.parseDouble(budgetLimitField.getText());

            expenseTracker.setBudget(category, limit);
            updateTotalSpendingLabel();
            clearBudgetFields();
            JOptionPane.showMessageDialog(this, "Budget set successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid values.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInputFields() {
        descriptionField.setText("");
        amountField.setText("");
        categoryField.setText("");
        dateField.setText("");
    }

    private void clearBudgetFields() {
        budgetCategoryField.setText("");
        budgetLimitField.setText("");
    }
}

public class ExpenseTrackerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ExpenseTrackerGUI();
            }
        });
    }
}
