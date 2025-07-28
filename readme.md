# ISO 8583 Integration

A Spring Boot application for handling ISO 8583 financial messages over TCP using 
[Spring Integration](https://docs.spring.io/spring-integration/reference/overview.html) and 
[jPOS](https://jpos.org/).

## Overview

This project provides a robust TCP server that can receive, parse, and process ISO 8583 messages commonly used in financial transaction processing. It leverages Spring Integration for message routing and jPOS for ISO 8583 message handling.

## Features

- **TCP Server**: Listens for incoming ISO 8583 messages on a configurable port
- **Message Processing**: Automatically unpacks and processes ISO 8583 messages
- **Spring Integration**: Uses Spring Integration for message routing and handling
- **jPOS Integration**: Leverages jPOS library for ISO 8583 message parsing
- **Configurable**: Server port and other settings are externally configurable
- **Comprehensive Testing**: Includes integration tests with TCP client simulation

## Technology Stack

- **Java 21**: Modern Java runtime
- **Spring Boot 3.5.4**: Application framework
- **Spring Integration**: Message routing and integration patterns
- **jPOS 2.1.10**: ISO 8583 message processing library
- **Maven**: Build and dependency management

## Project Structure

```
src/
├── main/
│   ├── java/com/github/koosty/iso8583/
│   │   ├── Main.java                              # Spring Boot application entry point
│   │   ├── ServerProperties.java                  # Configuration properties
│   │   ├── TcpServerConfiguration.java           # TCP server and ISO 8583 configuration
│   │   └── Iso8583MessageInboundEndpoint.java    # Message processing endpoint
│   └── resources/
│       ├── application.yaml                       # Application configuration
│       └── iso-8583-fields.xml                   # ISO 8583 field definitions
└── test/
    └── java/com/github/koosty/iso8583/
        └── Iso8583IntegrationApplicationTests.java # Integration tests
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd iso-8583-integration
   ```

2. **Build the project**
   ```bash
   mvnw clean compile
   ```

3. **Run tests**
   ```bash
   mvnw test
   ```

4. **Start the application**
   ```bash
   mvnw spring-boot:run
   ```

The server will start on port 2222 by default and begin listening for ISO 8583 messages.

## Configuration

### Application Properties

Configure the server in `application.yaml`:

```yaml
spring:
  application:
    name: iso-8583-integration

server:
  port: 2222  # TCP server port (default: 2222)

logging:
  level:
    com.github.koosty: DEBUG
```

### ISO 8583 Field Configuration

The ISO 8583 field definitions are configured in `iso-8583-fields.xml`. This file defines:
- Message Type Indicator (MTI)
- Field definitions and formats
- Data element specifications
- Field lengths and types

## Usage

### Sending ISO 8583 Messages

The server accepts ISO 8583 messages over TCP connections. Messages should be:
- Formatted according to ISO 8583 standard
- Serialized with ByteArrayCrLfSerializer
- Sent as byte arrays over TCP

### Example Message Flow

1. **Client connects** to the TCP server (default port 2222)
2. **Client sends** an ISO 8583 message (e.g., financial transaction request)
3. **Server receives** and unpacks the message using jPOS
4. **Server processes** the message and logs the details
5. **Server responds** with a success confirmation

### Sample ISO 8583 Message

The integration test [Iso8583IntegrationApplicationTests.java](src/test/java/com/github/koosty/iso8583/Iso8583IntegrationApplicationTests.java) demonstrates a typical financial transaction message:

```java
// Create ISO 8583 message
ISOMsg request = new ISOMsg();
request.setMTI("0100");  // Financial transaction request

// Set required fields
request.set(2, "5642570404782927");    // Primary Account Number
request.set(3, "011000");              // Processing Code
request.set(4, "78000");               // Transaction Amount
request.set(7, "1220145711");          // Transmission Date/Time
request.set(11, "101183");             // System Trace Audit Number
// ... additional fields
```

## API Documentation

### TCP Server Configuration

**Class**: `TcpServerConfiguration`

- **Port**: Configurable via `ServerProperties`
- **Serialization**: ByteArrayCrLfSerializer
- **Message Channel**: `message-channel`

### Message Processing

**Class**: `Iso8583MessageInboundEndpoint`

- **Input Channel**: `message-channel`
- **Processing**: Unpacks ISO 8583 messages using jPOS
- **Response**: Returns success/error status as byte array

### Configuration Properties

**Class**: `ServerProperties`

- **Prefix**: `server`
- **Port Property**: `server.port` (default: 2222)

## Testing

### Running Tests

```bash
# Run all tests
mvnw test

# Run with verbose output
mvnw test -X
```

### Integration Test

The project includes comprehensive integration tests that:
- Start the Spring Boot application
- Create a TCP client connection
- Send sample ISO 8583 messages
- Verify server responses
- Test end-to-end message flow

## Development

### Adding New Message Types

1. **Update field definitions** in `iso-8583-fields.xml`
2. **Modify message processing** in `Iso8583MessageInboundEndpoint`
3. **Add corresponding tests** in the test suite

### Extending Functionality

- **Custom Message Processors**: Implement additional `@ServiceActivator` methods
- **Message Routing**: Add Spring Integration routing configuration
- **Database Integration**: Add JPA/JDBC for message persistence
- **Security**: Implement SSL/TLS for secure connections

## Monitoring and Logging

The application provides detailed logging at DEBUG level for the `com.github.koosty` package:

- Connection establishment and termination
- Message reception and processing
- ISO 8583 message content (in UTF-8 format)
- Error handling and exception details

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   - Change the `server.port` configuration
   - Ensure no other services are using port 2222

2. **Message Parsing Errors**
   - Verify ISO 8583 field definitions in `iso-8583-fields.xml`
   - Check message format and field lengths

3. **Connection Issues**
   - Verify client uses ByteArrayCrLfSerializer
   - Check network connectivity and firewall settings

### Debug Mode

Enable debug logging by setting:
```yaml
logging:
  level:
    com.github.koosty: DEBUG
    org.springframework.integration: DEBUG
```

