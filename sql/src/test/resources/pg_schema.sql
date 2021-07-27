CREATE TABLE authors
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(70)  NOT NULL,
    country VARCHAR(100) NOT NULL
);

CREATE TABLE books
(
    id    SERIAL PRIMARY KEY,
    title VARCHAR(50) NOT NULL
);

CREATE TABLE books_authors
(
    id        SERIAL PRIMARY KEY,
    author_id INT NOT NULL,
    book_id   INT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors (id),
    FOREIGN KEY (book_id) REFERENCES books (id)
);
