# Delivery App --- Technology Stack & Development Plan

## 1. Project Vision

We are building a scalable delivery platform with two primary
applications:

-   **Customer App** --- customers create delivery requests by selecting
    pickup and drop locations.
-   **Rider App** --- riders discover nearby delivery requests, accept
    them, and complete deliveries.

The first MVP will focus on the customer-side flow:

``` text
Open App
   ↓
Select Pickup
   ↓
Select Drop
   ↓
Confirm Delivery
   ↓
Publish Request
   ↓
Request Created
```

The architecture should be simple enough to develop with two people, but
structured so that it can scale later without requiring a complete
rewrite.

------------------------------------------------------------------------

# 2. Complete Technology Stack

## Mobile Applications

### Framework

**Flutter + Dart**

We will build:

-   Customer application
-   Rider application

Flutter gives us one codebase for Android and iOS while providing good
performance and a mature ecosystem.

### Flutter Libraries / Tools

Likely libraries:

-   `google_maps_flutter` --- map display
-   Google Places integration --- location search/autocomplete
-   HTTP client such as `dio` --- API communication
-   Riverpod or Bloc --- application state management
-   GoRouter --- navigation
-   Firebase Cloud Messaging --- push notifications
-   Secure storage --- tokens and sensitive local data

We will not add every library on day one. Dependencies should be
introduced only when needed.

------------------------------------------------------------------------

# 3. Backend

## Language

**Java 21**

## Framework

**Spring Boot 3.x**

Spring Boot will be the primary backend framework.

It will handle:

-   Authentication
-   User management
-   Delivery requests
-   Rider management
-   Delivery lifecycle
-   Location data
-   Pricing
-   Notifications
-   APIs
-   Business rules

------------------------------------------------------------------------

# 4. Backend Architecture

## Initial Architecture: Modular Monolith

We will NOT start with microservices.

The first backend will be a single Spring Boot application organized
into independent modules.

``` text
Spring Boot
│
├── Auth
├── User
├── Delivery
├── Rider
├── Location
├── Notification
└── Common
```

This gives us the simplicity of one backend while maintaining boundaries
between features.

## Future Architecture

If the platform grows significantly, modules can eventually become
independent services:

``` text
                    API Gateway
                         │
          ┌──────────────┼──────────────┐
          ↓              ↓              ↓
     User Service   Delivery Service  Rider Service
          │              │              │
          └──────────────┼──────────────┘
                         ↓
                       Kafka
                         │
        ┌────────────────┼────────────────┐
        ↓                ↓                ↓
   Notification       Payment          Analytics
      Service          Service           Service
```

We will only make this transition when scale or team requirements
justify it.

------------------------------------------------------------------------

# 5. API Architecture

The public API will initially use:

**REST + JSON**

Example:

``` http
POST /api/v1/auth/register
POST /api/v1/auth/login

POST /api/v1/deliveries
GET  /api/v1/deliveries/{id}
GET  /api/v1/deliveries/my

GET  /api/v1/riders/requests
POST /api/v1/deliveries/{id}/accept
```

Later, internal service-to-service communication can use:

**gRPC**

if the system is split into microservices.

------------------------------------------------------------------------

# 6. Database

## Primary Database

**PostgreSQL**

PostgreSQL will store:

-   Users
-   Riders
-   Delivery requests
-   Delivery status
-   Addresses
-   Payments
-   Ratings
-   Delivery history
-   Other transactional data

## Geospatial Database

**PostGIS**

PostGIS extends PostgreSQL with geographic functionality.

This is particularly important for a delivery application.

Example future query:

``` text
Find available riders within 5 km of pickup location.
```

We will store locations using geographic coordinates rather than relying
only on text addresses.

Example:

``` text
pickup_address:
Sector 62, Noida

pickup_location:
POINT(latitude, longitude)
```

------------------------------------------------------------------------

# 7. Redis

**Redis** will be introduced for high-speed temporary data.

Potential uses:

-   Active rider locations
-   Caching
-   Session-related data
-   Rate limiting
-   Temporary delivery state
-   Nearby rider matching
-   Distributed locks

We should avoid using Redis as the primary source of truth for important
transactional data.

PostgreSQL remains the source of truth.

------------------------------------------------------------------------

# 8. Real-Time Communication

## Initial Technology

**WebSockets**

Spring Boot will support WebSocket communication for real-time events.

Examples:

``` text
Rider accepts request
        ↓
Customer receives update immediately
```

``` text
Rider location changes
        ↓
Customer receives updated location
```

``` text
Delivery status changes
        ↓
Customer app updates instantly
```

For the initial implementation, Spring WebSocket/STOMP can be used.

------------------------------------------------------------------------

# 9. Message Broker

## Future Technology

**Apache Kafka**

Kafka will be introduced when the platform needs asynchronous event
processing.

Potential events:

``` text
DeliveryCreated
RiderAssigned
RiderArrived
PackagePickedUp
DeliveryStarted
DeliveryCompleted
PaymentCompleted
```

Example:

``` text
Delivery Service
       ↓
DeliveryCreated
       ↓
     Kafka
   ┌───┼──────┐
   ↓   ↓      ↓
Rider Notification Analytics
```

Kafka is intentionally NOT part of the first MVP.

------------------------------------------------------------------------

# 10. Google Maps Platform

Google Maps will be central to the delivery experience.

We expect to use:

### Maps SDK

For displaying maps.

### Places API

For:

-   Search
-   Autocomplete
-   Place selection

### Geocoding

For converting between:

``` text
Address ↔ Coordinates
```

### Routes API

For:

-   Route calculation
-   Distance
-   Estimated travel time

The application should store both:

``` text
Human-readable address
+
Latitude / Longitude
```

Example:

``` json
{
  "address": "Sector 62, Noida",
  "latitude": 28.6271,
  "longitude": 77.3747
}
```

------------------------------------------------------------------------

# 11. Authentication & Security

## Authentication

Use:

**Spring Security**

Initially:

-   Phone/email authentication
-   JWT-based access tokens

Potential future authentication:

-   Google OAuth
-   Apple Sign-In
-   Phone OTP

## Security Requirements

We must eventually implement:

-   Password hashing
-   JWT expiration
-   Refresh tokens
-   Role-based authorization
-   API validation
-   Rate limiting
-   Input validation
-   HTTPS
-   Secure API keys
-   Secrets management
-   Audit logging

Roles:

``` text
CUSTOMER
RIDER
ADMIN
```

------------------------------------------------------------------------

# 12. Notifications

## Push Notifications

**Firebase Cloud Messaging (FCM)**

Examples:

``` text
Your rider has accepted the delivery.
```

``` text
Your rider is approaching the pickup location.
```

``` text
Your delivery has been completed.
```

------------------------------------------------------------------------

# 13. Cloud & Infrastructure

## Cloud Provider

**AWS**

Potential AWS services:

-   EC2 / ECS / EKS
-   RDS PostgreSQL
-   ElastiCache Redis
-   S3
-   CloudFront
-   Load Balancer
-   CloudWatch
-   Secrets Manager

We will not deploy everything immediately.

------------------------------------------------------------------------

# 14. Containers

## Docker

Every major backend component should be containerized.

Initial deployment:

``` text
Docker
  ↓
Spring Boot
  ↓
PostgreSQL
  ↓
Redis
```

Docker ensures development and production environments remain
consistent.

------------------------------------------------------------------------

# 15. Kubernetes

## Future

**Kubernetes / AWS EKS**

Kubernetes will only be introduced when the application has enough scale
or operational complexity to justify it.

We should not start the project with Kubernetes.

------------------------------------------------------------------------

# 16. CI/CD

## GitHub Actions

Pipeline:

``` text
Developer pushes code
        ↓
GitHub
        ↓
Run tests
        ↓
Build application
        ↓
Build Docker image
        ↓
Push image
        ↓
Deploy
```

------------------------------------------------------------------------

# 17. Monitoring & Observability

## Application Monitoring

-   Spring Boot Actuator
-   OpenTelemetry
-   Prometheus
-   Grafana
-   Sentry

We want visibility into:

-   API response time
-   Error rate
-   Database performance
-   Active users
-   Active riders
-   Delivery requests
-   WebSocket connections
-   Server resource usage

Observability should be added progressively rather than becoming an MVP
blocker.

------------------------------------------------------------------------

# 18. Version Control & Collaboration

## GitHub

Repository:

``` text
delivery-platform
```

Main branches:

``` text
main
develop
```

Feature branches:

``` text
feature/customer-map
feature/location-search
feature/delivery-ui
feature/auth
feature/delivery-api
feature/database-schema
```

## Development Workflow

``` text
Create feature branch
        ↓
Develop
        ↓
Run tests
        ↓
Commit
        ↓
Push
        ↓
Pull Request
        ↓
Code Review
        ↓
Merge into develop
        ↓
Release to main
```

Never directly develop on `main`.

------------------------------------------------------------------------

# 19. Recommended Repository Structure

A simple initial structure:

``` text
delivery-platform/
│
├── customer-app/
│   └── Flutter project
│
├── rider-app/
│   └── Flutter project
│
├── backend/
│   └── Spring Boot project
│
├── docs/
│   ├── architecture/
│   ├── api/
│   └── database/
│
└── README.md
```

This keeps the two mobile apps and backend together while still
separating their codebases.

------------------------------------------------------------------------

# 20. Customer MVP

The first version should focus only on this:

``` text
Customer opens app
        ↓
Home screen
        ↓
Select pickup
        ↓
Select drop
        ↓
See both locations on map
        ↓
See route
        ↓
Confirm details
        ↓
Publish request
        ↓
Request created
        ↓
Waiting for rider
```

We should NOT build payments, advanced rider matching, Kafka,
Kubernetes, or an admin dashboard before this works.

------------------------------------------------------------------------

# 21. Initial Customer Screens

## Screen 1 --- Home

Contains:

-   Pickup field
-   Drop field
-   Map
-   Continue button

## Screen 2 --- Location Search

Contains:

-   Search bar
-   Google Places suggestions
-   Current location
-   Selected location

## Screen 3 --- Confirm Request

Contains:

-   Pickup address
-   Drop address
-   Map preview
-   Distance
-   Estimated time
-   Delivery details
-   Publish button

## Screen 4 --- Request Status

Initially:

``` text
Request published

Searching for a rider...
```

Later this becomes the live delivery tracking screen.

------------------------------------------------------------------------

# 22. Initial Database Design

We will eventually create tables such as:

``` text
users
riders
deliveries
delivery_status_history
addresses
```

Basic delivery model:

``` text
deliveries
-------------------------
id
customer_id
pickup_address
pickup_location
drop_address
drop_location
status
created_at
updated_at
```

Delivery statuses:

``` text
PENDING
ACCEPTED
ARRIVED_AT_PICKUP
PICKED_UP
IN_TRANSIT
DELIVERED
CANCELLED
```

The schema will be refined before implementation.

------------------------------------------------------------------------

# 23. Full Product Roadmap

## Phase 0 --- Planning

-   Define MVP
-   Define user flows
-   Define architecture
-   Define database
-   Define API contracts
-   Set up GitHub

## Phase 1 --- Customer UI

-   Flutter setup
-   Navigation
-   Home screen
-   Pickup selection
-   Drop selection
-   Confirmation screen
-   Request status screen

## Phase 2 --- Google Maps

-   Google Cloud project
-   API configuration
-   Maps
-   Places autocomplete
-   Location permissions
-   Coordinates
-   Markers
-   Routes
-   Distance and ETA

## Phase 3 --- Backend

-   Spring Boot setup
-   PostgreSQL
-   PostGIS
-   Database migrations
-   Delivery entity
-   Delivery API
-   Validation
-   Error handling

## Phase 4 --- Integration

``` text
Flutter
   ↓
Spring Boot
   ↓
PostgreSQL/PostGIS
```

The customer should be able to create a real delivery request.

## Phase 5 --- Authentication

-   Register
-   Login
-   JWT
-   User profile
-   Authorization

## Phase 6 --- Rider App

-   Rider registration
-   Rider login
-   Online/offline status
-   Available requests
-   Request details
-   Accept request
-   Active delivery

## Phase 7 --- Matching

-   Nearby rider discovery
-   PostGIS queries
-   Rider availability
-   Request assignment
-   Matching rules

## Phase 8 --- Real-Time System

-   WebSockets
-   Rider location
-   Delivery status
-   Customer tracking
-   Live updates

## Phase 9 --- Notifications

-   FCM
-   Push notifications
-   Delivery updates

## Phase 10 --- Payments

-   Pricing engine
-   Payment gateway
-   Payment status
-   Refunds
-   Transaction history

## Phase 11 --- Production Infrastructure

-   Docker
-   AWS
-   CI/CD
-   Monitoring
-   Logging
-   Backups
-   Security hardening

## Phase 12 --- Scaling

Only when required:

-   Redis optimization
-   Kafka
-   Load balancing
-   Horizontal scaling
-   Microservices
-   Kubernetes
-   Advanced observability

------------------------------------------------------------------------

# 24. Immediate Action Plan

## Step 1 --- Create the GitHub repository

Create:

``` text
delivery-platform
```

Add:

``` text
README.md
.gitignore
LICENSE
```

Create:

``` text
main
develop
```

------------------------------------------------------------------------

## Step 2 --- Decide the first ownership split

Recommended:

### Developer A

Customer application:

-   Flutter
-   UI
-   Google Maps
-   Places
-   Location selection

### Developer B

Backend:

-   Spring Boot
-   PostgreSQL
-   PostGIS
-   API design
-   Database

Both developers should understand both sides, but each person can own a
primary area.

------------------------------------------------------------------------

# 25. Step 3 --- Set Up the Development Environment

### Frontend

Install:

-   Flutter SDK
-   Android Studio
-   Android SDK
-   VS Code or IntelliJ
-   Git

### Backend

Install:

-   Java 21
-   IntelliJ IDEA
-   Maven or Gradle
-   PostgreSQL
-   PostGIS
-   Docker

### Accounts

Create/configure:

-   GitHub
-   Google Cloud
-   Firebase
-   AWS later

------------------------------------------------------------------------

# 26. Step 4 --- Create the Initial Projects

Create:

``` text
customer-app/
rider-app/
backend/
```

For the first milestone, only `customer-app` and `backend` need active
development.

------------------------------------------------------------------------

# 27. Step 5 --- Google Cloud Setup

Create a Google Cloud project.

Enable only the APIs required for the MVP:

-   Maps SDK
-   Places API
-   Routes API
-   Geocoding if required

Create restricted API keys.

Do NOT commit API keys to GitHub.

------------------------------------------------------------------------

# 28. Step 6 --- Build the First Customer Screen

The first working screen should contain:

``` text
┌─────────────────────────────┐
│ Pickup                      │
│ Search pickup location      │
│                             │
│ Drop                        │
│ Search drop location        │
│                             │
│           MAP               │
│                             │
│        [Continue]            │
└─────────────────────────────┘
```

Do not connect the backend yet.

------------------------------------------------------------------------

# 29. Step 7 --- Implement Location Selection

The user should be able to:

1.  Search pickup
2.  Select a place
3.  Store its address
4.  Store latitude
5.  Store longitude
6.  Display the pickup marker
7.  Search drop
8.  Select a place
9.  Store its address
10. Store latitude
11. Store longitude
12. Display the drop marker

------------------------------------------------------------------------

# 30. Step 8 --- Add Route

Once pickup and drop are available:

``` text
Pickup
   ↓
Google Routes API
   ↓
Route
   ↓
Distance + ETA
```

Display this information on the confirmation screen.

------------------------------------------------------------------------

# 31. Step 9 --- Design the Backend API

Before implementing the API, agree on the contract.

Initial endpoint:

``` http
POST /api/v1/deliveries
```

Request:

``` json
{
  "pickup": {
    "address": "Sector 62, Noida",
    "latitude": 28.6271,
    "longitude": 77.3747
  },
  "drop": {
    "address": "Indirapuram, Ghaziabad",
    "latitude": 28.6412,
    "longitude": 77.3715
  }
}
```

Response:

``` json
{
  "id": "DLV-10001",
  "status": "PENDING"
}
```

The exact API contract should be documented before both sides integrate.

------------------------------------------------------------------------

# 32. Definition of the First Major Milestone

We consider the first milestone complete when:

``` text
Customer
   ↓
Opens Flutter app
   ↓
Searches pickup
   ↓
Selects pickup
   ↓
Searches drop
   ↓
Selects drop
   ↓
Sees both markers
   ↓
Sees route
   ↓
Confirms request
   ↓
Presses Publish
   ↓
Spring Boot receives request
   ↓
PostgreSQL stores request
   ↓
App receives delivery ID
   ↓
Shows "Request Published"
```

At this point we have the first real vertical slice of the product.

------------------------------------------------------------------------

# 33. Guiding Principles

### 1. Build the MVP first

Do not build infrastructure for millions of users before we have users.

### 2. Keep boundaries clean

Even the initial monolith should have clear modules.

### 3. PostgreSQL is the source of truth

Redis and Kafka should not replace transactional persistence.

### 4. Store coordinates, not just addresses

Location is a core part of the business.

### 5. API contracts matter

Frontend and backend should agree on request/response formats before
integration.

### 6. Use Git properly

Feature branches + pull requests + code review.

### 7. Test important business logic

Especially:

-   Delivery state transitions
-   Authentication
-   Rider matching
-   Pricing
-   Permissions

### 8. Scale based on evidence

Introduce Redis, Kafka, microservices, and Kubernetes when actual
requirements justify them.

------------------------------------------------------------------------

# 34. Our Next Action

The immediate next task is **not coding the complete application**.

We will first create the project foundation:

``` text
1. GitHub repository
2. Branching strategy
3. Flutter customer app
4. Spring Boot backend
5. PostgreSQL + PostGIS
6. Google Cloud project
7. Google Maps API configuration
8. Initial architecture documentation
```

After this, we will build the first customer feature:

**Pickup + Drop location selection using Google Maps.**

Then we will connect it to the Spring Boot backend and persist the first
delivery request.
