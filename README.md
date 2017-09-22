# Wallraff

Simulate email universe that assists in development of software.
The software that powers Stubmarine.

## Getting Started

### Install Dependencies

1. Install/update yarn globally
1. `cd frontend`, `yarn install`, and `cd ..`
1. `cd backend`, and `./gradlew`

### Quick Start Application

1. Start the backend via`JWTSECRET=secret ./gradew :backend:bootRun`
1. Start the frontend by:
    1. `cd frontend`
    1. `yarn start`
1. Navigate to [localhost:3000](http://localhost:3000)

## Testing

Run all tests via `./gradlew testAll`

## Building an UberJar

1. `./gradlew buildUberJar`

## Deploying

1. Build UberJar as above
1. `cf push stubmarine`
