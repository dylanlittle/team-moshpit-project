# team-moshpit-project

The application uses:
- `maven` to build the project
- `thymeleaf` for templating
- `flyway` to manage `postgres` db migrations
- `selenium` for feature testing
- `faker` to generate fake names for testing
- `junit4` for unit testing
- `auth0` and `spring-security` for authentication and user management
- `lombok` to generate getters and setters for us

Instructions to Run the Moshpit Application:

1. Add application.yml, found in the Slack channel, to src/main/resources .
2. Run 'createdb moshpit_dev'
3. Run 'createdb moshpit_test'

## QuickStart Instructions

- Fork and clone this repository to your machine
- Open the codebase in an IDE like InteliJ or VSCode
    - If using IntelliJ, accept the prompt to install the Lombok plugin (if you don't get prompted, press command and comma
      to open the Settings and go to Plugins and search for Lombok made by Jetbrains and install).
- Create two new Postgres databases: 
    - Run 'createdb moshpit_dev'
    - Run 'createdb moshpit_test'
- Install Maven `brew install maven`
- Check nothing else runs on port 8080 `lsof -i :8080`
- Build the app and start the server, using the Maven command `mvn spring-boot:run`
> The database migrations will run automatically at this point
- Visit `http://localhost:8080/artists/{id}`

## Running the tests

- Install chromedriver using `brew install chromedriver`
- Start the server in a terminal session `mvn spring-boot:run -Dspring-boot.run.profiles=test`
- Open a new terminal session and navigate to the team-moshpit-project directory
- Check nothing else runs on port 8080 `lsof -i :8080`
- Run your tests in the second terminal session with `mvn test`

> All the tests should pass. If one or more fail, read the next section.

## Common Setup Issues

### The application is not running

For the feature tests to execute properly, you'll need to have the server running in one terminal session and then use a second terminal session to run the tests.

### Chromedriver is in the wrong place

Selenium uses Chromedriver to interact with the Chrome browser. If you're on a Mac, Chromedriver needs to be in `/usr/local/bin`. You can find out where it is like this `which chromedriver`. If it's in the wrong place, move it using `mv`.

### Chromedriver can't be opened

Your Mac might refuse to open Chromedriver because it's from an unidentified developer. If you see a popup at that point, dismiss it by selecting `Cancel`, then go to `System Preferences`, `Security and Privacy`, `General`. You should see a message telling you that Chromedriver was blocked and, if so, there will be an `Open Anyway` button. Click that and then re-try your tests.
