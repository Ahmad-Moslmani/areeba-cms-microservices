# areeba-cms-microservices

A robust, secure, and scalable microservices ecosystem built with **Java 17** and **Spring Boot v3.5.9.** This system handles account management, encrypted card storage, real-time fraud detection, and transaction orchestration. It is a backend API-based Card Management System (CMS).

---

# 🏗 System Architecture
The solution consists of four independent microservices communicating via **Spring Cloud OpenFeign**:
* **Transaction Service (Orchestrator):** The gateway for financial operations. It orchestrates calls to Card, Account, and Fraud services to process transactions.
* **Account Service:** Manages account balances and states (Active/Inactive).
* **Card Service:** Handles sensitive card data using high-level encryption.
* **Fraud Service:** Implements a sliding-window frequency check and amount limits to prevent fraudulent activities.



# 🚀 Getting Started (Docker Execution)
### Prerequisites
* **Docker & Docker Compose**
* (Optional) **Java 17+** and **Maven** if running locally

* ### Running the System
I utilized **Google Jib** to build and push optimized, daemon-less Docker images to DockerHub. You do not need to build the code locally; simply use the provided `docker-compose.yml`.

1.  **Database Initialization:** The system uses a shared PostgreSQL instance. I provided an `init-db.sql` script that automatically creates the required databases and schemas (including `public` and `test` schemas) to ensure a "plug-and-play" experience.

**Note:** Ensure the init-db.sql file exists in the same directory as the docker-compose.yml file, as it is mounted as a volume to initialize the PostgreSQL container.

2.  **Execute:** Navigate to the root directory and run:
    ```bash
    docker-compose up -d
    ```

3.  **Access APIs (Swagger UI):**
    * **Account Service:** [http://localhost:8084/swagger-ui/index.html](http://localhost:8084/swagger-ui/index.html)
    * **Card Service:** [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)
    * **Transaction Service:** [http://localhost:8086/swagger-ui/index.html](http://localhost:8086/swagger-ui/index.html)
    * **Fraud Service:** [http://localhost:8088/swagger-ui/index.html](http://localhost:8088/swagger-ui/index.html)

---

## 🛡 Security & Technical Excellence

### Data Privacy & Hashing
* **Encryption:** Card numbers are never stored in plain text. I used `springframework.security.crypto.encrypt` **Encryptors** and **TextEncryptor** (AES-256) for data-at-rest.
* **Blind Indexing:** To allow searching for cards without decrypting the entire database, I implemented a **Hashing (HMAC-SHA256)** strategy to create searchable tokens.

### Fraud Detection Optimization
* **Database Indexing:** In the Fraud service, I optimized the sliding-window query using `@Table(indexes = @Index(columnList = "cardId, createdAt"))`. This ensures that frequency checks (8 transactions per hour) remain high-performance even as the audit log grows.
* **FraudPolicyInitializer:** A managed component that ensures the system starts with default limits ($10,000 limit and 8 transactions per hour) automatically.

### Resiliency & Error Handling
* **FeignErrorDecoder:** Implemented in the Transaction orchestrator Catch and wrap downstream exceptions from Account/Card/Fraud services into meaningful business errors.
* **Global Exception Handler:** Centralized `@RestControllerAdvice` across all services providing consistent `ErrorResponseDTO` structures.
* **Validation:** Strict input validation such as `@Pattern`, `@Digits`, and `@NotNull`.

---

## 🧪 Testing Strategy
The project employs a multi-level testing strategy using **JUnit 5** and **Mockito**:

* **Unit Tests:** Utilizing `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@Spy`, and `@InjectMocks`.
* **Slicing Tests:**
    * `@WebMvcTest` for Controller validation logic.
    * `@DataJpaTest` for repository queries.
* **Integration Tests:** Full context testing with `@SpringBootTest`.
* **Profiles:** Managed via `application-test.yml` and `@ActiveProfiles("test")`.
* **Database Isolation:** The `init-db.sql` creates a dedicated **test schema**, allowing for isolated integration tests, that can be easily transitioned to **Testcontainers** in a CI/CD pipeline.

---

## 🔄 Business Logic Flow

1.  **Create Account:** Use the Account Swagger to create an account and receive a UUID.
2.  **Create Card:** Use the Card Swagger, passing the `accountId`. The card number will be encrypted.
3.  **Execute Transaction:** Use the Transaction Swagger.
    * **Step A:** Transaction service calls Card service via **FeignClient** to validate existence, status, and expiry.
    * **Step B:** Transaction service calls Account service via **FeignClient** to validate balance and status.
    * **Step C:** Transaction service calls Fraud service via **FeignClient** to audit the attempt and check frequency/amount limits..
    * **Database Record:** Rejections are tracked via a **One-To-One** relationship between the `transaction` and `transaction_rejection` tables for full auditability.



---

## 🛠 Tech Stack Summary

* **Language:** Java 17
* **Framework:** Spring Boot v3.5.9
* **Persistence:** Hibernate / Spring Data JPA / PostgreSQL
* **Communication:** Spring Cloud OpenFeign
* **Security:** Spring Security Crypto (TextEncryptor), HMAC-SHA256 Hashing
* **Utilities:** Lombok
* **Build & Deployment:** Maven, Google Jib, Docker Compose
* **Documentation:** Swagger (OpenAPI)