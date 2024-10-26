Hotel Management System
Overview
The Hotel Management System is a JavaFX application designed for managing hotel bookings. This application allows users to book rooms, view room features, and order food. It provides a simple and intuitive interface for hotel guests.

Features
User Input: Collects user details including name, gender, phone number, and email.
Room Management: Displays a list of available rooms with features and pricing.
Booking Functionality: Allows users to book rooms and shows a payment receipt upon successful booking.
Food Ordering: Users can order local dishes for their booked room, with payment validation.
Dynamic UI: The UI updates dynamically based on user interactions, such as enabling the food order button only for booked rooms.
Requirements
Java Development Kit (JDK) 11 or later
JavaFX SDK
Installation
Clone the repository:

bash
Copy code
git clone <repository_url>
Navigate to the project directory:

bash
Copy code
cd hotel-management-system
Compile the Java files:

bash
Copy code
javac -d bin src/*.java
Run the application:

bash
Copy code
java -cp bin HotelManagementApp
Usage
Upon launching the application, enter your details (name, gender, phone number, and email) and click on "Submit".
You will see a list of available rooms with their features and prices.
Select a room and click on "Book Room" to reserve it. You will need to enter a valid payment code.
After booking, you can order food for your booked room by selecting dishes and clicking on "Order Food for Selected Room".
Room Features
Each room has unique features, including views, bed types, and additional amenities. The pricing is dynamic and varies by room.

Contributing
Contributions are welcome! If you have suggestions or improvements, please submit a pull request or open an issue.

License
This project is licensed under the MIT License. See the LICENSE file for more details.

Contact
For any inquiries or feedback, please reach out to:

Name: Ken Sarowiwa
Email: sarowiwaken001@gmail.com
