# Smart Campus Monitoring REST API

## Overview

This project is a RESTful API developed for the **5COSC022W Client-Server Architectures** coursework. It is designed to manage smart campus rooms, sensors assigned to those rooms, and the historical readings produced by each sensor.

The implementation uses **JAX-RS** and stores all data using **in-memory collections** only, in line with the coursework restrictions. The API supports discovery information at the root endpoint, room management, sensor registration, filtered sensor retrieval, nested sensor readings, and structured JSON error handling.

Main capabilities of the API include:

- API discovery metadata through the root endpoint
- creating, listing, retrieving, and deleting rooms
- registering sensors against valid rooms
- filtering sensors by type using query parameters
- storing and retrieving readings for a specific sensor
- returning custom JSON error responses for invalid operations
- logging request and response details through JAX-RS filters

This coursework implementation follows the required constraints by using:

- **JAX-RS only**
- **Apache Tomcat 9**
- **HashMap / ArrayList based in-memory storage**
- **no external database**
- **no Spring Boot**

---

## Technology Stack

- Java
- Maven
- JAX-RS (`javax.ws.rs`)
- Apache Tomcat 9
- In-memory data structures (`HashMap`, `ArrayList`)
- Postman for API testing

---

## API Base URL

During local development, the API is accessed using:

```text
http://localhost:8080/SmartCampusAPI/api/v1

## Project Structure

src/main/java/com/westminster/smartcampus/
├── config/
│   └── RestApplication.java
├── exception/
│   ├── LinkedResourceNotFoundException.java
│   ├── RoomNotEmptyException.java
│   └── SensorUnavailableException.java
├── filter/
│   └── ApiLoggingFilter.java
├── mapper/
│   ├── GlobalExceptionMapper.java
│   ├── LinkedResourceNotFoundMapper.java
│   ├── RoomNotEmptyMapper.java
│   └── SensorUnavailableMapper.java
├── model/
│   ├── ApiError.java
│   ├── Room.java
│   ├── Sensor.java
│   └── SensorReading.java
├── resource/
│   ├── DebugResource.java
│   ├── DiscoveryResource.java
│   ├── RoomResource.java
│   ├── SensorReadingResource.java
│   └── SensorResource.java
└── store/
    └── DataStore.java


---

## Data Models

### Room

{
  "id": "ENG-204",
  "name": "Engineering Design Lab",
  "capacity": 45,
  "sensorIds": ["TEMP-204", "AIR-201"]
}

### Sensor

{
  "id": "AIR-201",
  "type": "AirQuality",
  "status": "ACTIVE",
  "currentValue": 612.8,
  "roomId": "ENG-204"
}

### SensorReading

{
  "id": "READ-1001",
  "timestamp": 1714994400000,
  "value": 612.8
}

---

## How to Build and Run

### Option 1 - Run in NetBeans with Tomcat 9

1. Open the project in NetBeans.
2. Make sure **Tomcat 9** is configured as the server.
3. Clean and build the project.
4. Run the project.
5. Open the base URL:

```text
http://localhost:8080/SmartCampusAPI/api/v1
```

### Option 2 - Build with Maven

Run the following in the project directory:

```bash
mvn clean package
```

Then deploy the generated WAR file to Tomcat 9.

---

## Endpoint Summary

### Discovery

* `GET /api/v1`

### Rooms

* `GET /api/v1/rooms`
* `POST /api/v1/rooms`
* `GET /api/v1/rooms/{roomId}`
* `DELETE /api/v1/rooms/{roomId}`

### Sensors

* `GET /api/v1/sensors`
* `GET /api/v1/sensors?type=CO2`
* `POST /api/v1/sensors`
* `GET /api/v1/sensors/{sensorId}`

### Sensor Readings

* `GET /api/v1/sensors/{sensorId}/readings`
* `POST /api/v1/sensors/{sensorId}/readings`

### Debug

* `GET /api/v1/debug/error`

---

## Sample curl Commands

### 1. Discovery endpoint

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1
```

### 2. Get all rooms

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 3. Create a room

curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id": "ENG-204",
    "name": "Engineering Design Lab",
    "capacity": 45
  }'

### 4. Register a sensor for a valid room

curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "AIR-201",
    "type": "AirQuality",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "ENG-204"
  }'

### 5. Filter sensors by type

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```

### 6. Add a reading to a sensor

curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/AIR-201/readings \
  -H "Content-Type: application/json" \
  -d '{
    "value": 612.8
  }'

### 7. Get sensor readings

curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/AIR-201/readings

### 8. Trigger the global error mapper

curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/debug/error

---

## Example Error Responses

### 409 Conflict - Room not empty

{
  "status": 409,
  "error": "Conflict",
  "message": "Room ENG-204 cannot be deleted because sensors are still assigned to it.",
  "path": "/api/v1/rooms/ENG-204",
  "timestamp": 1714994400000
}

### 422 Unprocessable Entity - Invalid room reference

{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Sensor registration failed because the referenced room does not exist.",
  "path": "/api/v1/sensors",
  "timestamp": 1714994400000
}

### 403 Forbidden - Sensor under maintenance

{
  "status": 403,
  "error": "Forbidden",
  "message": "This sensor is currently under maintenance and cannot accept new readings.",
  "path": "/api/v1/sensors/TEMP-204/readings",
  "timestamp": 1714994400000
}

### 500 Internal Server Error - Global exception mapper

{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected server error occurred. Please contact the administrator.",
  "path": "unknown",
  "timestamp": 1714994400000
}

---

## Coursework Report Answers

### Part 1 - Service Architecture and Setup

#### 1. Question: In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures $(maps/lists)$ to prevent data loss or race conditions.

By default, the resource classes of JAX-RS are request-scoped. That is, the runtime typically instantiates a new resource class instance for every request. This is good because it helps to avoid accidentally sharing mutable data between requests within the resource class. But in this coursework the application data is held in shared in-memory collections in a central DataStore, so concurrency is still important. The addition of new rooms, registration of sensors, removal of rooms and storing of readings needs to be carefully handled to avoid race conditions and inconsistent updates to the shared HashMap and ArrayList data structures. 


#### 2. Question: Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

Hypermedia enhances a RESTful design by enabling the API response to guide the client towards resources and actions. The client can look at the response and find out about the links, rather than relying solely on documentation. In this case, the discovery endpoint contains links to the main resources (rooms and sensors). This simplifies the API navigation, its testing, and allows it to evolve over time with additional endpoints.

---

### Part 2 - Room Management

#### 1.  Question: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.

Sending back only the room IDs keeps the response size small and is bandwidth efficient, particularly with many rooms. But it can require the client to make another request to get more information about a room. Sending full room objects is more convenient as all the useful information will be contained in the response. In this project, we return full room objects because it makes the code more readable and useful, while keeping the size of the response reasonable for the size of the project.

#### 2. Question: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

yes,The DELETE method is idempotent in this case. When the client issues a DELETE request for a room (the first time), the room is deleted. The second time, the room has already been removed, so we send a 404 Not Found response. While the second response is different, the crucial thing is that the second request does not continue to alter the system's state, which maintains idempotent behaviour.

---

### Part 3 - Sensor Operations and Linking

#### 1. Question: We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

The @Consumes(MediaType.APPLICATION_JSON) annotation tells JAX-RS that the endpoint only accepts request bodies in JSON format. If a client sends data using another content type such as text/plain or application/xml, JAX-RS rejects the request because the media type does not match what the method is designed to consume. This prevents unsupported input formats from being processed and ensures the incoming data can be correctly mapped into Java objects.

#### 2. You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

A query parameter is more appropriate for filtering because the client is still requesting the same collection resource, just with a condition applied to it. For example, /sensors?type=AirQuality clearly means the sensors collection should be narrowed down by type. In contrast, a path such as /sensors/type/AirQuality can make the filter appear like a separate sub-resource. Query parameters are more flexible and fit better with common REST practices for optional filtering and search operations.

---

### Part 4 - Deep Nesting with Sub-Resources

#### 1. Question: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

The Sub-Resource Locator pattern helps keep the API modular by separating nested resource logic into dedicated classes. In this project, SensorResource is responsible for sensor-related operations, while SensorReadingResource manages the readings associated with a specific sensor. This separation improves readability, maintainability, and testability. If all nested paths were placed inside one large resource class, the code would become more crowded and harder to extend.

---

### Part 5 - Advanced Error Handling and Exception Mapping

#### 1. Question: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?


HTTP 422 is more semantically accurate because the target endpoint exists and the request body is structurally valid, but one of the values inside the payload cannot be processed correctly. In this coursework, the client may send a valid JSON request to create a sensor, but the provided roomId may not exist in the system. In that situation, the problem is not that the URI is missing, but that the submitted data is invalid in context. That is why 422 Unprocessable Entity is a better fit than 404 Not Found.

#### 2. Question: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

Exposing raw Java stack traces can leak internal technical details about the system. For example, an attacker may learn package names, class names, file names, framework behaviour, and the exact point where an error occurred. This information can help someone understand the internal structure of the application and identify possible weak points. Returning a generic JSON error response is safer because it prevents unnecessary disclosure of implementation details.
---

## Testing Summary

The API was tested manually using Postman to confirm that the required endpoints and validation rules were working correctly. Testing covered the discovery endpoint, room creation and retrieval, safe room deletion, sensor registration with room validation, sensor filtering by type, nested sensor readings, restriction of maintenance-state sensors, and global exception handling.

The final testing results showed that the API returned the expected JSON responses and status codes for both successful operations and error scenarios.

---

## Video Demonstration

**Video Link: https://drive.google.com/drive/folders/1oeyFW7sgv4nFHlDXeIVoLBZU7xyFdSC1   ** 

---

## Author

Name: **Sithika Weerasinghe**
Student ID: **w2151961**
