# {Local Email Universe}

Simulate email universe that assists in development of software.

## Getting Started

1. Install/update yarn globally
1. `cd frontend`, `yarn install`, and `cd ..`
1. `cd backend`, and `./gradlew`

1. `cd backend`, `./gradew bootRun`, and `cd ..`
1. `cd frontend`, and `yarn start`
1. Open [localhost:3000](http://localhost:3000) in your browser of choice

## Building an Uberjar

1. `cd frontend`, `yarn build`, and `cd ..`
1. `cp frontend/dist/* backend/src/main/resources/static`
1. `cd backend`, and `./gradlew build jar`
