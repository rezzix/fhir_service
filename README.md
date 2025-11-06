# DPP Declarations Fhir service
Le service de declarations DPP permet de recevoir et de controller les declarations DPP des SIHs.

# Etapes de traitement
Le service permet de vérifier la structure globale et les rubrique de la declaration ainsi que la conformité avec les référentiels nationaux 

# API Documentation

## OpenAPI Documentation
This service provides OpenAPI documentation for Spring-based endpoints. After starting the service, you can access the documentation at these endpoints when the application is running on the default port (8080):

- **Custom Swagger UI**: [http://localhost:8080/custom-swagger-ui.html](http://localhost:8080/custom-swagger-ui.html) or [http://localhost:8080/swagger](http://localhost:8080/swagger) (uses external Swagger UI)
- **API Documentation (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) - Contains Spring-based endpoints only

**Note**: The Swagger UI shows only Spring controller endpoints (like `/api/**`), not the HAPI FHIR server endpoints which are handled separately.

## FHIR Endpoints
The FHIR server endpoints are available at `/fhir/**` and are not included in the OpenAPI documentation above because they are handled by the HAPI FHIR server servlet, not Spring's request mapping. These endpoints follow FHIR R5 specification.

### Accessing FHIR Endpoints
- FHIR Server Base URL: `http://localhost:8080/fhir`
- Example FHIR endpoints:
  - Capability Statement: `GET http://localhost:8080/fhir/metadata`
  - FHIR Resources: `GET/POST/PUT/DELETE http://localhost:8080/fhir/{resourceType}/{id}`

### Testing FHIR Endpoints
You can test the FHIR endpoints directly using tools like curl, Postman, or by accessing them in a browser for read operations.

## Available Endpoints

### FHIR Endpoints
- FHIR Server: `/fhir/*` - HAPI FHIR R5 server endpoints

### Custom API Endpoints
- Get Declaration: `GET /api/declarations/{id}`
- Upload Document: `POST /api/declarations/{declarationId}/upload` (multipart/form-data)
- List Documents: `GET /api/declarations/{declarationId}/documents`
- Download Document: `GET /api/documents/{documentId}/download`
- Test Endpoint: `GET /test/hello`

### Metadata
- Capability Statement: `GET /metadata` (returns FHIR CapabilityStatement in JSON format)
