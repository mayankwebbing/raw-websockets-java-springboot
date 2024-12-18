# Raw Websockets Without STOMP in Java Spring Boot

This project demonstrates the implementation of a real-time chat application using **raw WebSockets** in a **Spring Boot** backend. It uses `@EnableWebSocket` for WebSocket configuration and handles messaging functionality without leveraging higher-level protocols like STOMP or SockJS.

---

## Features

- **WebSocket Communication**:
  - A WebSocket-based connection is established between clients and the server.
  - Communication happens in real time, with clients sending messages to specific chat rooms.

- **Room Management**:
  - Ability to create, retrieve, and manage chat rooms.
  - WebSocket messages are scoped by room ID, ensuring messages only reach members of the relevant room.

- **Database Integration**:
  - Messages are stored in a database, ensuring persistence across sessions.
  - Uses JPA for data management and lazy-loading for room associations.

- **Lightweight DTOs**:
  - Uses `MessageDTO` to handle lightweight communication between server and clients.
  - Separates the data layer (`Message` model) from the transport layer (`MessageDTO`).

---

## How It Works

1. **Establish Connection**:
   - Clients establish a WebSocket connection to the server at `/room/{roomId}`.

2. **Send and Receive Messages**:
   - Messages are sent as JSON payloads via the WebSocket connection.
   - The server broadcasts the message to all participants in the room.

3. **Persist Messages**:
   - Each message is saved in the database for future retrieval.
   - REST endpoints allow fetching message history for a room.

---

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/mayankwebbing/raw-websockets-java-springboot.git
   cd raw-websockets-java-springboot
   ```

2. **Configure the Database**:
   - Update the `application.properties` file with your database details.

3. **Run the Application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **WebSocket Connection**:
   - Open a WebSocket client or a browser console.
   - Connect to: `ws://localhost:8080/room/{roomId}`.

---

## Endpoints

### WebSocket
- **URL**: `/room/{roomId}`
- **Methods**: Real-time message sending and receiving.

### REST Endpoints
- **Room Management**:
  - `GET /rooms`: Get all rooms.
  - `POST /rooms`: Create a new room.
  - `GET /rooms/{id}`: Get a specific room.

- **Message Management**:
  - `GET /messages/room/{roomId}`: Get all messages for a room.
  - `POST /messages`: Create a new message.
  - `PUT /messages/{id}`: Update an existing message.
  - `DELETE /messages/{id}`: Delete a message.

---

## Technologies Used

- **Java Spring Boot**
- **WebSocket API**
- **Spring Data JPA**
- **H2 Database (or any configured database)**
- **Jackson for JSON processing**

---

## Future Improvements

- **Authentication**: Add user authentication for secure access.
- **Front-End Integration**: Create a client interface for chat interactions.
- **Typing Indicators**: Implement typing indicators using WebSocket broadcasts.
- **File Sharing**: Enhance to support file uploads via WebSocket.

---