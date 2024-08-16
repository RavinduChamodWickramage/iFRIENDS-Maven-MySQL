# iFRIENDS

iFRIENDS is a Contact Management System designed to efficiently manage and organize contact information. It provides a user-friendly interface for adding, searching, viewing, and deleting contacts, ensuring a seamless experience for users managing their personal and professional networks.

## Features

- **Add Contacts**: Add new contacts with details including name, address, email, phone number, and more.
- **Search Contacts**: Search for contacts using various criteria such as name or phone number.
- **View Contacts**: View a comprehensive list of all contacts in a table format with sortable columns.
- **Delete Contacts**: Remove contacts from the system with a confirmation prompt to prevent accidental deletions.
- **Data Validation**: Ensure data integrity with validation for email addresses, phone numbers, and NICs.

## Technologies Used

- **JavaFX**: For building the graphical user interface.
- **Java**: For implementing the backend logic and database interactions.
- **JFOENIX Controls**: Provides enhanced UI components for a modern look and feel.
- **MySQL**: Database system used for storing and managing contact information.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- MySQL database server
- JavaFX SDK

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/RavinduChamodWickramage/iFRIENDS-Maven-MySQL

2. **Set Up the Database**

Create the MySQL database and table by executing the following SQL script:

CREATE DATABASE iFRIENDS;

USE iFRIENDS;

CREATE TABLE contacts (
   id VARCHAR(5) PRIMARY KEY,
   title VARCHAR(10),
   firstName VARCHAR(50),
   lastName VARCHAR(50),
   email VARCHAR(100),
   address VARCHAR(200),
   nic VARCHAR(15),
   dob DATE,
   phoneNumber VARCHAR(15)
);

3. **Configure Database Connection**

Update the database connection details in the Java code to match your MySQL server configuration.

4. **Build and Run**

Open the project in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).
Ensure that the JavaFX SDK is configured correctly in your IDE.
Build and run the application to start managing your contacts.

**Usage**

Add Contacts: Navigate to the 'Add Contact' form to input new contact details and save them to the database.
Search Contacts: Use the search functionality to find specific contacts quickly.
View Contacts: Go to the 'View Contacts' section to see a list of all stored contacts.
Delete Contacts: Select a contact and confirm deletion to remove it from the system.

**Contributing**

Contributions to the iFRIENDS project are welcome. If you have suggestions or improvements, please fork the repository and submit a pull request.

**Contact**

For any questions or feedback, please reach out to ravinduchamod1@gmail.com
