# Verifit App
A Rest API using Spring Boot, Swagger and Docker. For demonstration purposes.

## Prerequisites
- Install Docker Desktop (or any alternative docker manager) [https://docs.docker.com/desktop/install/](https://docs.docker.com/desktop/install/)
- git clone [https://github.com/Metrowe/verifit](https://github.com/Metrowe/verifit)

## Run
In command line navigate to the cloned git repo and run the following command:
```docker build -t verifit-app .```

Go the Images tab in Docker Desktop and click on Run for the ```verifit-app``` image.

Once container is running on port 8080, the swagger UI will be available at http://localhost:8080/swagger-ui/index.html

## Recommended testing
There are three endpoints
- POST /api/attendance
- GET /api/attendance/{username}/streak
- GET /api/attendance/{username}/discount

To illustrate the functionality make the following requests:
- POST attendance: username=user01 timestamp=TODAYS DATE (e.g. 2025-09-01 08:00:00)
- POST attendance: username=user01 timestamp=ONE WEEK AGO (e.g. 2025-08-25 08:00:00)
- GET streak: username=user01 : Returns 2, they have 2 weeks in a row that they have attended at least once
- GET discount: username=user01 : Returns false, they don't have a streak of 3 or more in a row

- POST attendance: username=user01 timestamp=TWO WEEKS AGO (e.g. 2025-08-18 08:00:00)
- POST attendance: username=user01 timestamp=THREE WEEKS AGO (e.g. 2025-08-11 08:00:00)
- GET streak: username=user01 : Returns 4, they have 4 weeks in a row that they have attended at least once
- GET discount: username=user01 : Returns true, they have a streak of 3 or more in a row

You can continue adding records or users as needed. Adding more attendances in the same week will not alter the streak or discount eligibility.
