CREATE TABLE IF NOT EXISTS expenses(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
amount BIGINT NOT NULL,
category_id BIGINT NOT NULL,
`date` DATE NOT NULL,
commentary VARCHAR(255),
CONSTRAINT expenses_to_categories_fk
FOREIGN KEY (category_id) REFERENCES categories(id)
);