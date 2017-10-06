# Stubmarine

Simulate email universe that assists in development of software.
The software that powers Stubmarine.

## Getting Started

### Install Dependencies

1. Yarn globally via `brew install yarn`
1. ChromeDriver globally via
    1. `brew install chromedriver`
    1. `mkdir -p ~/.chromdrivertest && ln -s /usr/local/bin/chromedriver ~/.chromdrivertest/chromedriver`
1. Frontend dependencies via
    1. `cd frontend`
    1. `yarn install`
    1. `cd ..`
1. Backend dependencies via 
    1. `cd backend`
    1. `./gradlew`

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

Deployment uses the Cloud Foundry CLI:
1. `brew install cloudfoundry/tap/cf-cli`

Deploy by:
1. Build UberJar as above
1. `cf push stubmarine`

## Misc Python scripts

1. Install Miniconda via `brew cask install miniconda`
1. Add Miniconda bin to path
1. Create conda env via `conda create --name stubmarine python=3.6`
1. Activate env via `source activate stubmarine`
1. Install requirements via `pip install -r misc/requirements.txt`
1. Run misc commands via `python misc/*.py`

## License

Licensed under the MIT License.
