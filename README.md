# StayNest – ICT711 Assessment 4

Individual extension of the Assessment 3 StayNest Java accommodation booking system.

## Requirements
- JDK 17+
- Maven 3.9+ (for JUnit tests)

## Run the application
From this folder:

```bash
javac -d out src/*.java
java -cp out StayNestApp
```

The application first asks the user to choose **GUI** or **TBI**. Both modes use the same `StayNestSystem` business logic.

## Run JUnit 5 tests
```bash
mvn test
```

The test suite contains 8 tests covering search, sorting, CRUD validation and evaluation.

## Assessment 4 features
- GUI and Text-Based Interface selection
- User, property and booking operations (including booking update/delete)
- Search/query functions
- Linear search by user ID
- Binary search by user ID
- Sorting by ID/name/email, property name/price, booking ID/date
- Swing dialogs and validation feedback
- CSV file loading/saving
- Exception handling
- JUnit 5 unit tests

## Project structure
- `src/` – Java source code
- `test/` – JUnit tests
- `data/users.csv` – user data
- `docs/` – supporting documentation
- `pom.xml` – Maven/JUnit configuration
