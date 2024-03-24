# Bookstore Inventory Management System

## Overview

The Bookstore Inventory Management System is a robust SpringBoot application tailored for efficient management of a bookstore's inventory. This backend system empowers users with comprehensive CRUD operations, including adding, updating, deleting, and retrieving book records, facilitated through a gRPC interface with Protobuf for streamlined communication.

### Features

- **CRUD Operations:** Comprehensive Create, Read, Update, and Delete functionalities for bookstore inventory management:
  - **AddBook:** Seamlessly create a new book record.
  - **GetBook:** Efficiently retrieve a book by its ID.
  - **UpdateBook:** Update existing book records with ease.
  - **DeleteBook:** Delete a book record by its ID.
  - **ListBooks:** List all books in the inventory, providing a complete overview.
- **Database Integration:** Utilizes PostgreSQL for reliable data persistence and management.
- **gRPC and Protobuf:** Leverages gRPC and Protobuf for efficient, type-safe data serialization and robust communication.
- **Testcontainers & Citrus Integration:** Ensures high reliability and stability through integration with testcontainers and Citrus framework for comprehensive integration testing.
- **MapStruct:** Employs MapStruct for efficient and accurate object mapping between the entity and DTO layers.
- **Docker Integration:** Utilizes Docker for containerization, ensuring consistent and seamless deployment environments.

### Upcoming Features

- **Reactive Version Development:** Ongoing work on a reactive version of the application, incorporating Spring WebFlux and Project Reactor, for enhanced performance and scalability.

### Quality Assurance

- **High Test Coverage:** Achieved an impressive 96% line coverage with a combination of unit and integration tests, demonstrating the commitment to reliability and quality.
- **Best Practices & SOLID Principles:** The development of the application strictly adheres to AGILE methodologies and SOLID principles, ensuring a maintainable, scalable, and robust codebase.


## Prerequisites

Before you begin, ensure you have met the following requirements:
- Docker installed on your machine.
- Gradle for building the project.
- Optionally, Postman for testing the gRPC API.

## Getting Started

These instructions will get your copy of the project up and running on your local machine for development and testing purposes.

### Installation

1. Clone the repository:
```git clone https://github.com/AntonBabychP1T/bookstore-inventory-managment-system```

2. Ensure Docker is running on your machine.

3. Build the project:
```./gradlew clean build```

4. Create a `.env` file in the root directory of the project and add the following environment variables:
```env
POSTGRESQL_USER=username
POSTGRESQL_ROOT_PASSWORD=password
POSTGRESQL_PASSWORD=password
POSTGRESQL_DATABASE=database_name
POSTGRESQL_LOCAL_PORT=5433
POSTGRESQL_DOCKER_PORT=5432
SPRING_LOCAL_PORT=8081
SPRING_DOCKER_PORT=8080
DEBUG_PORT=5005
GRPC_SERVER_PORT=9090
```

### Running the Application

1. Run the application using Docker Compose:
```docker-compose up --build```

The gRPC server will be accessible on port 9090.

### Testing the Application

1. Install Postman on your computer.

2. Import the provided [.proto](https://github.com/AntonBabychP1T/bookstore-inventory-managment-system/blob/master/src/main/proto/.proto) file and use existing form to testing application).

## Video guide 
Here you can see video presentation for this project [link](https://www.loom.com/share/51fc6e3c0aba4565aa7654a827b3e4b6)

## Contact

Your Name - [antonbabi13@gmail.com](mailto:antonbabi13@gmail.com)

Project Link: [https://github.com/AntonBabychP1T/bookstore-inventory-managment-system](https://github.com/AntonBabychP1T/bookstore-inventory-managment-system)

