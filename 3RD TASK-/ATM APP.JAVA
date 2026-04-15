// Modern JavaFX ATM with PIN, Multiple Accounts, Transaction Storage
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.HashMap;

class BankAccount {
    private String pin;
    private double balance;

    public BankAccount(String pin, double balance) {
        this.pin = pin;
        this.balance = balance;
    }

    public boolean verifyPin(String input) {
        return pin.equals(input);
    }

    public double getBalance() { return balance; }

    public boolean deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            return true;
        }
        return false;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
}

public class ATMApp extends Application {
    private HashMap<String, BankAccount> accounts = new HashMap<>();
    private BankAccount currentAccount;
    private Label message = new Label();
    private TextField accountField = new TextField();
    private PasswordField pinField = new PasswordField();
    private TextField amountField = new TextField();

    @Override
    public void start(Stage stage) {
        accounts.put("1001", new BankAccount("1234", 5000));
        accounts.put("1002", new BankAccount("4321", 2000));

        VBox loginBox = new VBox(10);
        loginBox.setPadding(new Insets(20));
        loginBox.setAlignment(Pos.CENTER);

        Label title = new Label("Secure ATM Login");
        title.setFont(new Font(24));

        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> login(stage));

        loginBox.getChildren().addAll(title,
                new Label("Account Number"), accountField,
                new Label("PIN"), pinField,
                loginBtn, message);

        stage.setScene(new Scene(loginBox, 350, 300));
        stage.setTitle("ATM System");
        stage.show();
    }

    private void login(Stage stage) {
        String acc = accountField.getText();
        String pin = pinField.getText();

        if (accounts.containsKey(acc) && accounts.get(acc).verifyPin(pin)) {
            currentAccount = accounts.get(acc);
            dashboard(stage);
        } else {
            message.setText("Invalid Account or PIN");
        }
    }

    private void dashboard(Stage stage) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: linear-gradient(#2b5876, #4e4376);");

        Label header = new Label("ATM Dashboard");
        header.setFont(new Font(26));
        header.setTextFill(Color.WHITE);

        amountField.setPromptText("Enter Amount");

        Button deposit = new Button("Deposit");
        Button withdraw = new Button("Withdraw");
        Button balance = new Button("Check Balance");

        deposit.setOnAction(e -> {
            double amt = Double.parseDouble(amountField.getText());
            if (currentAccount.deposit(amt)) {
                log("Deposit: +" + amt);
                alert("Deposited Successfully");
            } else alert("Invalid Amount");
        });

        withdraw.setOnAction(e -> {
            double amt = Double.parseDouble(amountField.getText());
            if (currentAccount.withdraw(amt)) {
                log("Withdraw: -" + amt);
                alert("Withdraw Successful");
            } else alert("Insufficient Balance");
        });

        balance.setOnAction(e -> alert("Balance: " + currentAccount.getBalance()));

        box.getChildren().addAll(header, amountField, deposit, withdraw, balance);
        stage.setScene(new Scene(box, 400, 350));
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }

    private void log(String action) {
        try (FileWriter fw = new FileWriter("transactions.txt", true)) {
            fw.write(LocalDateTime.now() + " - " + action + "\n");
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        launch();
    }
}
