## 📄 Seek AI — Document Query Application — Backend

#### Spring Boot + Java backend for Seek AI, a RAG (Retrieval-Augmented Generation) application that lets users upload documents and ask natural-language questions about their content.

### Workflow
Backend implements the RAG (Retrieval-Augmented Generation) pattern: retrieving the most relevant sections of an uploaded document, then handing that context to a generative AI model to produce a grounded answer.

#### Upload Flow
```
Authenticated user uploads via POST /api/documents/upload
      ↓
Apache Tika extracts raw text (PDF, DOCX, PPTX, TXT)
      ↓
Text is split into chunks (~800 tokens each)
      ↓
Each chunk is embedded via Gemini's embedding model
      ↓
Chunks + vectors are stored in an in-memory vector store
      ↓
Document metadata + owner are saved to PostgreSQL
```

#### Query Flow
```
Authenticated user sends POST /api/documents/{id}/query
      ↓
Document ownership is verified
      ↓
Question is embedded into a vector
      ↓
Similarity search finds the top 5 most relevant chunks
      ↓
Chunks are joined into a "context" block
      ↓
A prompt (instructions + context + question) is sent to Gemini
      ↓
Model generates a grounded answer, returned as JSON
```

### Authentication & Authorization
Seek AI uses JWT-based authentication to protect document operations.

#### Authentication Flow
```
Register
   ↓
Password hashed with BCrypt
   ↓
User stored in PostgreSQL

Login
   ↓
Credentials verified
   ↓
JWT generated
   ↓
Token returned to client
```

Protected document requests require:
```
Authorization: Bearer <JWT>
```

### Tech Stack
<table>
  <tr>
    <th>Purpose</th>
    <th>Technology</th>
  </tr>
  <tr>
    <td>Language/Runtime</td>
    <td>Java 17</td>
  </tr>
  <tr>
    <td>Framework</td>
    <td>Spring Boot, Maven</td>
  </tr>
  <tr> 
     <td>Security</td> 
     <td>Spring Security, JWT, BCrypt</td> 
  </tr>
  <tr>
    <td>AI orchestration</td>
    <td>Spring AI</td>
  </tr>
  <tr>
    <td>Text extraction</td>
    <td>Apache Tika</td>
  </tr>
  <tr>
    <td>Embeddings + Chat Model</td>
    <td>Google Gemini (text-embedding-004, gemini-2.5-flash)</td>
  </tr>
  <tr>
    <td>Vector store</td>
    <td>Spring AI SimpleVectorStore (in-memory)</td>
  </tr>
  <tr>
    <td>Database</td>
    <td>PostgreSQL (document metadata only)</td>
  </tr>
</table>

### Project Structure
```
seek-backend
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── seek
│   │   │           └── docQuery
│   │   │               ├── config
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   └── VectorStoreConfig.java
│   │   │               ├── controller
│   │   │               │   ├── AuthController.java
│   │   │               │   └── DocumentController.java
│   │   │               ├── dto
│   │   │               │   ├── AuthResponse.java
│   │   │               │   ├── LoginRequest.java
│   │   │               │   └── RegisterRequest.java
│   │   │               │   ├── QueryRequest.java
│   │   │               │   └── QueryResponse.java
│   │   │               ├── entity
│   │   │               │   ├── Document.java
│   │   │               │   └── User.java
│   │   │               ├── exception
│   │   │               │   └── GlobalExceptionHandler.java
│   │   │               ├── repository
│   │   │               │   ├── DocumentRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               ├── service
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── DocumentQueryService.java
│   │   │               │   ├── DocumentUploadService.java
│   │   │               │   └── JwtService.java
│   │   │               └── SeekApplication.java
│   │   └── resources
│   │       └── application.properties
├── pom.xml
├── mvnw
└── mvnw.cmd
```

#### Why Spring AI?
Spring AI provides vendor-agnostic abstractions (EmbeddingModel, ChatModel, VectorStore) so the application logic doesn't depend on any single AI provider's SDK.

### API Endpoints
<table>
  <tr>
    <th>Method</th>
    <th>Endpoint</th>
    <th>Description</th>
  </tr>
  <tr> 
    <td>POST</td> 
    <td>/api/auth/register</td> 
    <td>Create a new user account.</td> 
  </tr> 
  <tr> 
    <td>POST</td> 
    <td>/api/auth/login</td> 
    <td>Authenticate a user and return a JWT.</td> 
  </tr>
  <tr>
    <td>POST</td>
    <td>/api/documents/upload</td>
    <td>Upload a file (multipart form-data, field name file).</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/api/documents</td>
    <td>List all uploaded documents.</td>
  </tr>
  <tr>
    <td>POST</td>
    <td>/api/documents/{id}/query</td>
    <td>Ask a question about a specific document — body: { "query": "..." }.</td>
  </tr>
  <tr>
    <td>DELETE</td>
    <td>/api/documents/{id}</td>
    <td>Delete a document's metadata record.</td>
  </tr>
</table>

### Local Setup
```
- Java 17
- Maven
- PostgreSQL running locally
- Google Gemini API key
- JWT Secret
```

#### Steps:
```
# create the database
psql -U postgres -c "CREATE DATABASE seek_db;"

# set your API key
export GEMINI_API_KEY=your-gemini-api-key
export JWT_SECRET=your-long-random-secret

# run the app
./mvnw spring-boot:run
```

### Terminology
<table>
  <tr>
    <th>Term</th>
    <th>Meaning</th>
  </tr>
  <tr>
    <td>RAG</td>
    <td>Retrieval-Augmented Generation — retrieving relevant context, then generating an answer grounded in it.</td>
  </tr>
  <tr>
    <td>Embedding</td>
    <td>A numeric vector representation of text's meaning (handled entirely server-side).</td>
  </tr>
  <tr>
    <td>Chunk</td>
    <td>A smaller segment of a larger document, split for more precise retrieval.</td>
  </tr>
  <tr>
    <td>Vector Store</td>
    <td>Vector Store is optimized for saving and searching embeddings by similarity.</td>
  </tr>
  <tr>
    <td>Similarity Search</td>
    <td>Finding the stored vectors that are closest in meaning to a query vector.</td>
  </tr>
  <tr>
    <td>Prompt</td>
    <td>The instruction and context sent to a generative AI model.</td>
  </tr>
  <tr> 
    <td>JWT</td> 
    <td>JSON Web Token used to authenticate users and protect document operations.</td> 
  </tr>
  <tr>
    <td>CORS</td>
    <td>Browser security policy that blocks unauthorized domains from accessing an API.</td>
  </tr>
</table>

#### Related Repository
https://github.com/Rohitha-25/Seek-AI-Frontend
