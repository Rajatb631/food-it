# foodit

Docker+Postgres 
1) download docker desktop
2)launch the docker deskstop application
3)launch the container - run -- "docker compose up -d"
4) verify the container is running by running "docker ps"



###########Springboot backend###############################

1)cd clientside
2)run "./mvnw.cmd spring-boot:run "-Dspring-boot.run.jvmArguments=-Duser.timezone=UTC"  // For Windows
3)run the server on localhost:8080