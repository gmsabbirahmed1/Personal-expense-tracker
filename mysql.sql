CREATE DATABASE expense_tracker;
USE expense_tracker;

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    date DATE NOT NULL,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Insert some default categories
INSERT INTO categories (name) VALUES ('Food'), ('Transportation'), ('Entertainment'), ('Utilities'), ('Other');
