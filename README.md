# team-moshpit-project

Instructions to Run the Moshpit Application:

1. Add application.yml, found in the Slack channel, to src/main/resources .
2. Run 'createdb moshpit_dev'
3. Run 'createdb moshpit_test'

### QuickStart Instructions

- Check nothing else runs on port 8080 `lsof -i :8080`
- Build the app and start the server, using the Maven command `mvn spring-boot:run`
- Visit `http://localhost:8080/artists/{id}`

### Running the tests

- Start the server in a terminal session `mvn spring-boot:run -Dspring-boot.run.profiles=test`
- Check nothing else runs on port 8080 `lsof -i :8080` 
- Run your tests in the second terminal session with `mvn test`