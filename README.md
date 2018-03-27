<<<<<<< HEAD
# AndroidDemo
=======
![Bridge International Academies Logo](https://raw.githubusercontent.com/BridgeInternationalAcademies/AndroidTechnicalTest/master/Banner%20Logo%20280x60.png)

# Android Technical Test

## Objective

To build a fast, secure, maintainable, usable and reliable Android app that uses the Bridge International Academies Technical Test API in an effort to administer pupils.

## Technical Test API

The Technical Test API is a RESTful web service that makes uses of basic HTTP GET, POST, PUT and DELETE calls. The API documentation can be accessed [here](https://bridgetechnicaltest.azurewebsites.net/).

![Technical Test API](https://raw.githubusercontent.com/BridgeInternationalAcademies/AndroidTechnicalTest/master/Technical%20Test%20API.png)

### Swagger

Upon visiting the API documentation page, you will see the following page where you can see a short description of the API, followed by the resources it provides and a list of all the actions that can be performed on them. You can also try out the API directly from the web browser. The API exposes a standard [Swagger](http://swagger.io/) endpoint.

### Real World Simulation

The API attempts to simulate real world usage in several ways:

1. Occasionally real web services go down due to any number of reasons. The Technical Test API will occasionally throw errors. Your app will need to deal with this.
2. To simulate bad network connectivity or the server being under intense load, the Technical Test API will sometimes take a few seconds to respond.
3. To simulate other users creating, updating and deleting data the Technical Test API will sometimes create, update or delete pupils from its internal database.

### Validation

Pupils have several fields, including Name, Country, Image, Latitude and Longitude. To insert or update a pupil, these fields values must be valid for the Technical Test API to accept them. If they are invalid the API will return a standard 400 Bad Request error response.

### Reset

During testing you may have deleted all pupils or added lots of strange and invalid data. You can use the reset endpoint to clear the database and seed it with some stock data so you can start from afresh.

## App Requirements

The App must have the ability to perform the following basic functions:

1. Display all pupils.
2. Display details for a single pupil.
3. Delete a pupil.
4. Continue to work offline using previously gathered data.

Feel free to implement extra useful or interesting features if you want. In addition, you should also:

1. Write a short ReadMe about your code and your design or any assumptions you made.
2. Write production quality code.
3. Contact us if you have any problems.
4. Submit your source code as a .zip file. Also, do not include any binary files in your final solution.

## Good Luck!
>>>>>>> first commit
