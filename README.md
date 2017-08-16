Team 39 members:
================
Alex Greco  (agreco3) grecoa66@gatech.edu

Shawn Hiemstra (shiemstra3) shiemstra3@gatech.edu

Eren Brumley (ebrumley3) ebrumley3@gatech.edu

Samuel Kinuthia (skinuthia3) skinuthia3@gatech.edu



Stack info:
==========

Languages:
-------
Java 8

AngularJs 1

    https://github.com/johnpapa/angular-styleguide/tree/master/a1#controllers

    https://www.codeschool.com/courses/shaping-up-with-angular-js (tutorial)

css3

html5


Frameworks:
--------
Jax-rs (from mvn)

Jersey (from mvn)

BootStrap 3

    http://getbootstrap.com/


Server Tools:
-------
Glassfish (4.1)

Build Tools:

Maven (3.3.9)

npm (3.9.5)

IDE:
-------
Intellij

Version control:
--------
Git

Database:
-------
MySQL 5.7 hosted on Google Cloud Platform, using https://github.com/GoogleCloudPlatform/cloud-sql-mysql-socket-factory to connect

Authentication:
-------
http://stackoverflow.com/questions/20969835/angularjs-login-and-authentication-in-each-route-and-controller

https://blog.brunoscopelliti.com/deal-with-users-authentication-in-an-angularjs-web-app/


Project Data:
============
ASACS

ASACS/target/ASACS/
- Directory houses our full stack, all external libraries.
 
ASACS/src/main/webapp
- Contains the landing index.html, which creates the navbar for login/logout/home.
- All sub-folders contain the modules broken down by function
- All HTML5 and angular.js controllers and js.service modules
 
ASACS/src/main/java/com/gatech/asacs
- Contains all project Java API classes
 
ASACS/team039_p3_complete_v7.sql
- Contains tables and seed data
 
Java classes containing DB connections and SQL statements
 -------
- Authentication
- Bunk
- Client
- CommonDB
- DBConnectionService
- Foodbank
- Foodpantry
- ManageServices
- Reports
- Room
- Shelter
- SoupKitchen
- Waitlist

How To Mimic Environment
==============
- First install intellij
- Install java 1.8
- Install npm
- Install Maven
- Install Glassfish
- Pull project down in intellij
- Configure API Credentials for GoogleCould DB
- Run Maven, clean, and install
- Configure glassfish as a locally hosted web server
- Run project in intellij
