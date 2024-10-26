import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HotelManagementApp extends Application {

    private Hotel hotel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        hotel = new Hotel(); // Initialize hotel object

        primaryStage.setTitle("Hotel Management System");
        primaryStage.setResizable(false);

        // Create a GridPane layout for user details
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #ffffff;"); // Background color

        // Create UI elements
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        Label genderLabel = new Label("Gender:");
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        grid.add(genderLabel, 0, 1);
        grid.add(genderComboBox, 1, 1);

        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();
        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 1, 2);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        grid.add(emailLabel, 0, 3);
        grid.add(emailField, 1, 3);

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        grid.add(submitButton, 1, 4);

        // Action for submit button
        submitButton.setOnAction(e -> {
            String name = nameField.getText();
            String gender = genderComboBox.getValue();
            String phone = phoneField.getText();
            String email = emailField.getText();

            if (isValidName(name) && isValidGender(gender) && isValidPhoneNumber(phone) && isValidEmail(email)) {
                // Proceed to the hotel management options
                showHotelDetails(name);
            } else {
                // Show error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Please correct your input.");
                alert.setContentText("Make sure all fields are filled correctly.");
                alert.showAndWait();
            }
        });

        // Set the scene
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showHotelDetails(String name) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Hotel Details");

        VBox detailsLayout = new VBox(10);
        detailsLayout.setPadding(new Insets(20));
        detailsLayout.setStyle("-fx-background-color: #ffffff;"); // Background color

        // Add welcome message
        Label welcomeLabel = new Label("Welcome, " + name + "!");
        welcomeLabel.setFont(new Font("Arial", 20));
        welcomeLabel.setTextFill(Color.DARKBLUE);
        detailsLayout.getChildren().add(welcomeLabel);

        // Create a TableView for rooms
        TableView<Room> roomTableView = new TableView<>();
        roomTableView.setPrefHeight(300);

        // Create columns for the table
        TableColumn<Room, String> roomNumberCol = new TableColumn<>("Room Number");
        roomNumberCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getRoomNumber())));
        roomNumberCol.setPrefWidth(100);

        TableColumn<Room, String> featuresCol = new TableColumn<>("Features");
        featuresCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFeatures()));
        featuresCol.setPrefWidth(300);

        TableColumn<Room, String> priceCol = new TableColumn<>("Price (Ksh)");
        priceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));
        priceCol.setPrefWidth(100);

        TableColumn<Room, String> availabilityCol = new TableColumn<>("Availability");
        availabilityCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().isAvailable() ? "Available" : "Not Available"));
        availabilityCol.setPrefWidth(100);

        // Add columns to the table
        roomTableView.getColumns().addAll(roomNumberCol, featuresCol, priceCol, availabilityCol);

        // Populate TableView with room details
        ObservableList<Room> roomItems = FXCollections.observableArrayList(hotel.getRooms());
        roomTableView.setItems(roomItems);

        detailsLayout.getChildren().add(roomTableView);

        // Booking button
        Button bookButton = new Button("Book Room");
        bookButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        detailsLayout.getChildren().add(bookButton);

        // Button to order food for the selected room
        Button orderFoodButton = new Button("Order Food for Selected Room");
        orderFoodButton.setDisable(true); // Initially disabled
        orderFoodButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        detailsLayout.getChildren().add(orderFoodButton);

        // Add listener to enable or disable the order food button based on room selection
        roomTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                orderFoodButton.setDisable(!newValue.isAvailable()); // Enable if the room is booked
            } else {
                orderFoodButton.setDisable(true); // Disable if no room is selected
            }
        });

        bookButton.setOnAction(e -> {
            Room selectedRoom = roomTableView.getSelectionModel().getSelectedItem();
            if (selectedRoom != null && selectedRoom.isAvailable()) {
                TextInputDialog paymentDialog = new TextInputDialog();
                paymentDialog.setTitle("Payment");
                paymentDialog.setHeaderText("Enter your payment code (M-Pesa or Bank):");
                paymentDialog.setContentText("Payment Code:");

                paymentDialog.showAndWait().ifPresent(paymentCode -> {
                    if (isValidPaymentCode(paymentCode)) {
                        selectedRoom.setAvailable(false); // Book the room
                        showReceipt(selectedRoom, paymentCode); // Show receipt
                        roomTableView.refresh(); // Refresh the TableView to show updated availability
                        orderFoodButton.setDisable(false); // Enable the order food button immediately after booking
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Payment Error");
                        alert.setHeaderText("Invalid payment code.");
                        alert.showAndWait();
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Room Selected");
                alert.setHeaderText("Please select an available room to book.");
                alert.showAndWait();
            }
        });

        orderFoodButton.setOnAction(e -> {
            Room selectedRoom = roomTableView.getSelectionModel().getSelectedItem();
            if (selectedRoom != null) {
                showDishOrdering(selectedRoom); // Open food ordering for the selected room
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Room Selected");
                alert.setHeaderText("Please select a room to order food.");
                alert.showAndWait();
            }
        });

        // Set the scene for the details stage
        Scene detailsScene = new Scene(detailsLayout, 600, 400);
        detailsStage.setScene(detailsScene);
        detailsStage.show();
    }

    private void showReceipt(Room bookedRoom, String paymentCode) {
        Alert receiptAlert = new Alert(Alert.AlertType.INFORMATION);
        receiptAlert.setTitle("Payment Receipt");
        receiptAlert.setHeaderText("Payment Successful");
        
        String receiptText = String.format("Receipt for Room Booking\n"
                + "Room Number: %d\n"
                + "Features: %s\n"
                + "Total Price: Ksh %.2f\n"
                + "Payment Code: %s\n"
                + "Thank you for your booking!", 
                bookedRoom.getRoomNumber(), bookedRoom.getFeatures(), bookedRoom.getPrice(), paymentCode);
        
        receiptAlert.setContentText(receiptText);
        receiptAlert.showAndWait();
    }

    private void showDishOrdering(Room bookedRoom) {
        Stage orderStage = new Stage();
        orderStage.setTitle("Order Local Dishes");

        VBox orderLayout = new VBox(10);
        orderLayout.setPadding(new Insets(20));
        orderLayout.setStyle("-fx-background-color: #ffffff;"); // Background color

        Label orderLabel = new Label("Order Local Kenyan Dishes for Room " + bookedRoom.getRoomNumber());
        orderLabel.setFont(new Font("Arial", 16));
        orderLayout.getChildren().add(orderLabel);

        // Create checkboxes for food items
        CheckBox nyamaChoma = new CheckBox("Nyama Choma - Ksh 800");
        CheckBox ugali = new CheckBox("Ugali - Ksh 200");
        CheckBox sukumaWiki = new CheckBox("Sukuma Wiki - Ksh 150");
        CheckBox chapati = new CheckBox("Chapati - Ksh 100");
        CheckBox samaki = new CheckBox("Samaki Fry - Ksh 600");
        CheckBox kachumbari = new CheckBox("Kachumbari - Ksh 100");
        CheckBox mandazi = new CheckBox("Mandazi - Ksh 50");
        CheckBox githeri = new CheckBox("Githeri - Ksh 300");
        CheckBox matoke = new CheckBox("Matoke - Ksh 400");
        CheckBox kachori = new CheckBox("Kachori - Ksh 250");
        CheckBox pilau = new CheckBox("Pilau - Ksh 350");
        CheckBox biryani = new CheckBox("Biryani - Ksh 500");

        orderLayout.getChildren().addAll(nyamaChoma, ugali, sukumaWiki, chapati, samaki, kachumbari, mandazi, githeri, matoke, kachori, pilau, biryani);

        Button orderButton = new Button("Place Order");
        orderButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        orderLayout.getChildren().add(orderButton);

        orderButton.setOnAction(e -> {
            List<String> orderedDishes = new ArrayList<>();
            if (nyamaChoma.isSelected()) orderedDishes.add("Nyama Choma");
            if (ugali.isSelected()) orderedDishes.add("Ugali");
            if (sukumaWiki.isSelected()) orderedDishes.add("Sukuma Wiki");
            if (chapati.isSelected()) orderedDishes.add("Chapati");
            if (samaki.isSelected()) orderedDishes.add("Samaki Fry");
            if (kachumbari.isSelected()) orderedDishes.add("Kachumbari");
            if (mandazi.isSelected()) orderedDishes.add("Mandazi");
            if (githeri.isSelected()) orderedDishes.add("Githeri");
            if (matoke.isSelected()) orderedDishes.add("Matoke");
            if (kachori.isSelected()) orderedDishes.add("Kachori");
            if (pilau.isSelected()) orderedDishes.add("Pilau");
            if (biryani.isSelected()) orderedDishes.add("Biryani");

            if (!orderedDishes.isEmpty()) {
                TextInputDialog paymentDialog = new TextInputDialog();
                paymentDialog.setTitle("Payment");
                paymentDialog.setHeaderText("Enter your payment code for food:");
                paymentDialog.setContentText("Payment Code:");

                paymentDialog.showAndWait().ifPresent(paymentCode -> {
                    if (isValidPaymentCode(paymentCode)) {
                        StringBuilder orderSummary = new StringBuilder("Your order has been placed for:\n");
                        for (String dish : orderedDishes) {
                            orderSummary.append(dish).append("\n");
                        }
                        orderSummary.append("Thank you for ordering!");

                        Alert orderAlert = new Alert(Alert.AlertType.INFORMATION);
                        orderAlert.setTitle("Order Confirmation");
                        orderAlert.setHeaderText("Order Placed Successfully");
                        orderAlert.setContentText(orderSummary.toString());
                        orderAlert.showAndWait();

                        showFoodReceipt(bookedRoom, orderedDishes, paymentCode); // Show receipt after payment
                        orderStage.close(); // Close the order window
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Payment Error");
                        alert.setHeaderText("Invalid payment code.");
                        alert.showAndWait();
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Dishes Selected");
                alert.setHeaderText("Please select at least one dish to order.");
                alert.showAndWait();
            }
        });

        // Set the scene for the order stage
        Scene orderScene = new Scene(orderLayout, 400, 400);
        orderStage.setScene(orderScene);
        orderStage.show();
    }

    private void showFoodReceipt(Room bookedRoom, List<String> orderedDishes, String paymentCode) {
        StringBuilder dishes = new StringBuilder();
        for (String dish : orderedDishes) {
            dishes.append(dish).append("\n");
        }

        Alert receiptAlert = new Alert(Alert.AlertType.INFORMATION);
        receiptAlert.setTitle("Food Order Receipt");
        receiptAlert.setHeaderText("Order Successful");

        String receiptText = String.format("Receipt for Food Order\n"
                + "Room Number: %d\n"
                + "Ordered Dishes:\n%s"
                + "Payment Code: %s\n"
                + "Thank you for your order!",
                bookedRoom.getRoomNumber(), dishes.toString(), paymentCode);

        receiptAlert.setContentText(receiptText);
        receiptAlert.showAndWait();
    }

    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private boolean isValidGender(String gender) {
        return gender != null && !gender.trim().isEmpty();
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("\\d{10}"); // Assuming 10 digit phone numbers
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }

    private boolean isValidPaymentCode(String paymentCode) {
        return paymentCode != null && paymentCode.matches("^(?=.*[A-Z])(?=.*\\d)[A-Z\\d]{10}$"); // 10 characters of uppercase letters and numbers
    }

    // Simple Room class for demonstration
    public class Room {
        private int roomNumber;
        private String features;
        private double price;
        private boolean available;

        public Room(int roomNumber, String features, double price) {
            this.roomNumber = roomNumber;
            this.features = features;
            this.price = price;
            this.available = true; // Initially all rooms are available
        }

        public int getRoomNumber() {
            return roomNumber;
        }

        public String getFeatures() {
            return features;
        }

        public double getPrice() {
            return price;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }

    // Simple Hotel class for demonstration
    public class Hotel {
        private List<Room> rooms;

        public Hotel() {
            rooms = new ArrayList<>();
            // Create 20 rooms with unique features and prices
            for (int i = 1; i <= 20; i++) {
                String features = "Room " + i + ": " + getRoomFeatures(i);
                double price = 2000 + (i * 150); // Increment price for demonstration
                rooms.add(new Room(i, features, price));
            }
        }

        private String getRoomFeatures(int roomNumber) {
            switch (roomNumber) {
                case 1: return "Ocean View, 1 King Bed, AC";
                case 2: return "Garden View, 2 Twin Beds, AC";
                case 3: return "City View, 1 Queen Bed, AC, Mini Bar";
                case 4: return "Pool View, 1 King Bed, AC, Balcony";
                case 5: return "Mountain View, 2 Queen Beds, AC, Kitchenette";
                case 6: return "Beach Front, 1 King Bed, AC, Jacuzzi";
                case 7: return "Forest View, 1 Queen Bed, AC, Mini Bar";
                case 8: return "City Center, 2 Twin Beds, AC, Free Wi-Fi";
                case 9: return "Luxury Suite, 1 King Bed, AC, Private Pool";
                case 10: return "Standard Room, 1 Double Bed, AC";
                case 11: return "Deluxe Room, 1 King Bed, AC, Ocean View";
                case 12: return "Economy Room, 1 Queen Bed, AC";
                case 13: return "Family Room, 2 Double Beds, AC, Kitchenette";
                case 14: return "Honeymoon Suite, 1 King Bed, AC, Balcony";
                case 15: return "Business Room, 1 King Bed, AC, Desk";
                case 16: return "Budget Room, 1 Double Bed, AC";
                case 17: return "Penthouse Suite, 1 King Bed, AC, Rooftop Access";
                case 18: return "Accessible Room, 1 Queen Bed, AC";
                case 19: return "Artistic Room, 1 King Bed, AC, Unique Decor";
                case 20: return "Historical Room, 2 Twin Beds, AC, Vintage Style";
                default: return "No special features.";
            }
        }

        public List<Room> getRooms() {
            return rooms;
        }
    }
}
