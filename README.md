# GitHub User Branches

This Spring Boot application retrieves a GitHub user's repositories along with a list of branches (each with its name and the SHA of the latest commit). Forked repositories are filtered out. If the user is not found, the application returns a JSON response with an error message and an HTTP status code of 404.

## Features

- **Repository Retrieval**  
  Fetches repositories for a given GitHub user using the GitHub API.

- **Branch Retrieval**  
  For each repository, retrieves a list of branches along with the SHA of the latest commit.

- **Fork Filtering**  
  Excludes repositories that are forks from the response.

- **Error Handling**  
  Returns a JSON error response with a (`404`) status if the user is not found.

## Technologies

- Java 20 (or higher)
- Spring Boot (WebFlux)
- Mutiny (`io.smallrye.reactive:mutiny:2.8.0`)
- Spring WebClient for GitHub API communication
- Gradle as the build tool


## Installation and Configuration

1. **Prerequisites**
    - Java 20 (or higher) must be installed.
    - Gradle is used as the build tool (or use the provided Gradle Wrapper).

2. **Project Configuration**  
   Ensure your [build.gradle](build.gradle.kts) includes the necessary dependencies. An example `build.gradle` might look like this:

   ```gradle
   plugins {
       java
       id("org.springframework.boot") version "3.4.3"
       id("io.spring.dependency-management") version "1.1.7"
   }

   group = "com.github"
   version = "0.0.1-SNAPSHOT"
   java {
       toolchain {
           languageVersion = JavaLanguageVersion.of(20)
       }
   }

   repositories {
       mavenCentral()
   }

   dependencies {
       implementation("org.springframework.boot:spring-boot-starter-webflux")
       implementation("io.smallrye.reactive:mutiny:2.8.0")
       testImplementation("org.springframework.boot:spring-boot-starter-test")
       testImplementation("io.projectreactor:reactor-test")
       testRuntimeOnly("org.junit.platform:junit-platform-launcher")
   }

   tasks.withType<Test> {
       useJUnitPlatform()
   }
## Running the Application

### From the Command Line
- Use the Gradle Wrapper to run the application:
    ``` bash
    ./gradlew bootRun
    ```
    This command starts the Spring Boot application on the configured port (default is 8080).
### From an IDE
- Open the project in your favorite IDE (e.g., IntelliJ IDEA, VS Code) and run the [main class](src/main/java/com/github/GitHubUserBranchesApplication.java):
## API Usage
### Endpoint
``` bash
GET /github/repos/{username}
```
#### Example request
``` bash
GET http://localhost:8080/github/repos/Majouo
```
### Example Response (User Found)
``` json
[
  {
    "repositoryName": "AirQuality",
    "ownerLogin": "Majouo",
    "branches": [
      {
        "branchName": "master",
        "lastCommitSha": "ecbde23ab7c05d2b935526a6c863649714883294"
      }
    ]
  },
]
```
### Example Response (User Not Found)
``` json
{
  "status": "404",
  "message": "User 'Majouo' not found on GitHub"
}
```
## Testing

### Manual Testing
- Use a tool like Postman or curl to test the endpoint:
    ``` bash
    curl http://localhost:8080/github/repos/Majouo
    ``` 
### Automated Tests
- To run the tests, execute:
    ``` bash
    ./gradlew test
    ```
## Notes
- The application uses Spring WebClient to communicate with the external GitHub API. Make sure you have an active internet connection.
- GitHub API imposes rate limits for unauthenticated requests (60 per hour).

