# Sweet Shop Management System

A modern full-stack web application for managing a sweet shop inventory with user authentication, sweet management, and purchasing functionality. Built using Test-Driven Development (TDD) principles with a robust backend API and an intuitive frontend interface.

## 🍬 Features

- **User Authentication**: Secure registration and login system with JWT token-based authentication
- **Sweet Management**: Complete CRUD operations for managing sweet inventory
- **Search & Filter**: Advanced search functionality by name, category, and price range
- **Purchase System**: Real-time inventory management with purchase tracking
- **Admin Controls**: Administrative features for restocking and managing inventory
- **Responsive Design**: Modern, mobile-friendly user interface
- **Role-Based Access**: Different permissions for regular users and administrators

## 🛠️ Technology Stack

### Backend
- **Language**: Java
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Testing**: JUnit
- **Architecture**: RESTful API

### Frontend
- **Framework**: React (JSX)
- **Styling**: Tailwind CSS
- **Type**: Single Page Application (SPA)

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- Java 17 or higher
- Node.js 16 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher
- npm or yarn

## 🚀 Installation & Setup

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <https://github.com/1-RoushanKumar/Sweet_Shop_Management_System>
   cd backend
   ```

2. **Configure PostgreSQL Database**
   ```sql
   CREATE DATABASE sweet_shop_db;
   CREATE USER sweet_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE sweet_shop TO sweet_user;
   ```

3. **Update Application Properties**
   ```properties
   # src/main/resources/application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/sweet_shop_db
   spring.datasource.username=sweet_user
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   
   # JWT Configuration
   jwt.secret=your_jwt_secret_key
   ```

4. **Run the Backend**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The backend API will be available at `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install Dependencies**
   ```bash
   npm install
   ```

3. **Configure API Base URL**
   ```javascript
   // src/config/api.js
   export const API_BASE_URL = 'http://localhost:8080/api';
   ```

4. **Run the Frontend**
   ```bash
   npm run dev
   ```

The frontend application will be available at `http://localhost:5173`

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "password": "string",
  "role": "USER" // or "ADMIN"
}
```

#### Login User
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

### Sweet Management Endpoints (Protected)

#### Add New Sweet
```http
POST /api/sweets
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "name": "string",
  "category": "string",
  "price": "number",
  "quantity": "number",
  "description": "string"
}
```

#### Get All Sweets
```http
GET /api/sweets
Authorization: Bearer <jwt_token>
```

#### Search Sweets
```http
GET /api/sweets/search?name=chocolate&category=candy&minPrice=10&maxPrice=100
Authorization: Bearer <jwt_token>
```

#### Update Sweet
```http
PUT /api/sweets/{id}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "name": "string",
  "category": "string",
  "price": "number",
  "quantity": "number",
  "description": "string"
}
```

#### Delete Sweet (Admin Only)
```http
DELETE /api/sweets/{id}
Authorization: Bearer <jwt_token>
```

### Inventory Management Endpoints (Protected)

#### Purchase Sweet
```http
POST /api/sweets/{id}/purchase
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "quantity": "number"
}
```

#### Restock Sweet (Admin Only)
```http
POST /api/sweets/{id}/restock
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "quantity": "number"
}
```

## 🧪 Testing

### Backend Testing

The backend follows Test-Driven Development (TDD) principles with comprehensive JUnit tests:

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report
```

**Test Coverage Areas:**
- Unit tests for all service layer methods
- Integration tests for all API endpoints
- Authentication and authorization tests
- Database integration tests
- Error handling and edge case tests

### Frontend Testing

```bash
# Run frontend tests
npm test

# Run tests with coverage
npm test -- --coverage
```

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication system
- **Password Encryption**: BCrypt hashing for secure password storage
- **Role-Based Authorization**: Different access levels for users and administrators
- **CORS Configuration**: Proper cross-origin resource sharing setup
- **Input Validation**: Comprehensive validation for all API endpoints

## 🎨 Frontend Features

- **Modern UI/UX**: Clean, intuitive interface built with React and Tailwind CSS
- **Responsive Design**: Mobile-first approach ensuring compatibility across devices
- **Real-time Updates**: Dynamic inventory updates and purchase confirmations
- **Search & Filter**: Advanced filtering capabilities with instant results
- **Admin Dashboard**: Comprehensive administrative panel for inventory management
- **Error Handling**: User-friendly error messages and loading states
```

## 🤖 My AI Usage

### AI Tools Used
I utilized multiple AI tools throughout the development process:
- **Claude (Anthropic)**: Primary AI assistant for architecture decisions and code review
- **Gemini (Google)**: Frontend development and React component creation
- **ChatGPT (OpenAI)**: Backend API design and troubleshooting

### How I Used AI

#### Backend Development with TDD
- **Claude & ChatGPT**: I used these tools extensively to implement Test-Driven Development practices. Specifically:
  - Asked Claude to help structure my JUnit test cases following the Red-Green-Refactor pattern
  - Used ChatGPT to generate comprehensive test scenarios for each API endpoint
  - Consulted Claude for best practices in writing meaningful unit tests and integration tests
  - Got assistance with mocking dependencies and setting up test data

####JWT Security Implementation
**Claude**: While I have good experience with JWT concepts and implementation, I used Claude to help with syntax and configuration details:
  - Quickly recall Spring Security configuration syntax and annotations
  - Get the correct JWT filter chain implementation structure
  - Remember proper CORS configuration syntax in Spring Boot
  - Verify role-based authorization annotations and security expressions

#### Frontend Development
- **Gemini**: Given my basic knowledge of React, I used Gemini extensively for frontend development:
  - Generated React component structures for user registration, login, and dashboard pages
  - Got help with Tailwind CSS styling and responsive design implementation
  - Received assistance with API integration using fetch/axios for backend communication
  - Used Gemini to create search and filter functionality components
  - Got guidance on React state management and component lifecycle methods

### Reflection on AI Impact

**Positive Impacts:**
1. **Accelerated Learning Curve**: AI tools significantly reduced the time needed to understand new technologies, especially Spring Security and React hooks
2. **TDD Implementation**: Without AI guidance, implementing proper TDD practices would have taken much longer. The tools helped me understand the Red-Green-Refactor cycle and write meaningful tests
3. **Code Quality**: AI suggestions helped me write cleaner, more maintainable code following best practices
4. **Problem Solving**: When stuck on specific implementation details, AI tools provided multiple approaches and explanations
5. **Frontend Development**: As someone with basic React knowledge, Gemini was instrumental in creating a polished, responsive frontend

**Challenges & Learnings:**
1. **Over-reliance Risk**: I had to be careful not to blindly copy AI-generated code without understanding it
2. **Verification Necessity**: AI suggestions weren't always perfect, requiring me to test and validate all recommendations
3. **Learning Balance**: While AI accelerated development, I ensured I understood the underlying concepts rather than just copying code

**Workflow Enhancement:**
The AI tools transformed my development workflow by:
- Reducing research time for unfamiliar technologies
- Providing instant feedback on code structure and best practices
- Helping debug complex issues faster
- Enabling me to focus more on business logic rather than syntax and configuration

**Overall Assessment:**
AI tools were invaluable for this project, especially given the requirement to use TDD principles and implement technologies I wasn't fully familiar with. They acted as knowledgeable pair programming partners, helping me deliver a more robust and well-tested application than I could have created alone in the same timeframe.

```

## 📝 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 📞 Contact

Your Name - rk04393@gmail.com

Project Link: https://github.com/1-RoushanKumar/Sweet_Shop_Management_System

---

**Note**: This project was developed as part of a full-stack development exercise, demonstrating Test-Driven Development principles, modern web technologies, and comprehensive API design.
