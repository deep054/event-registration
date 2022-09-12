# AEM Interview Assignment

This project is been created to demonstrate AEM coding challange assignment 

## Assignment Requirement which are considered  
Coding Challenge Description: Build a simple AEM site with 2 pages - Event Details, Event Registration Page using AEM latest core components and maven archetype.
The Event details page needs to have title, image, event details as text.
The event registration page should collect first name, last name and email.
This detail on submission should go to a mocked up endpoint. 
The result need to be stored in JCR as GUID. Once complete the user can get a alert message or moved to thank you page.

* Key Assumption: Customer doesn’t have AEM Forms license (but just Sites). Document all assumptions.
  
* Task (elaborating the exercise further)
  
     -Using the Maven archetype for AEM, set up a project. 
  
     -Build
        -A Servlet that will accept three fields (first name, last name, and email). Do validation and exception handling (imagine the front end can call this servlet)
  
        -Build a Service to take the fields, validate and use HTTP Service (can be mocked) to call external API (Do validation and exception handling)
  
        -Write a quick unit test (to test i or ii or both)
  
        -[Bonus Points] Extent a core component (can be Text component) to have any additional field.

## Dependencies
* This project is been built using AEM archetype 23 
* The Latest archetype 37 is not working in 6.5.0 (No SP) since I was not able to download latest SP on my local Mac
* Used core component version 2.8.0 which is compatible with AEM 6.5.0
* Tested this project in AEM 6.5.0
* Changed CSRF config to allow POST method  
   

## Assumptions / Key points
* Assuming basic validation will be done at frontend.
* Used and extended text component for adding event details on a page.
* Used core form component for adding user input fields on a page (added new action type).
* Created servlet to accept form data 

## Modules

The main parts of the template are:

* core: Java bundle containing all core functionality like OSGi services, listeners or schedulers, as well as component-related Java code such as servlets or request filters.
* ui.apps: contains the /apps (and /etc) parts of the project, ie JS&CSS clientlibs, components, templates, runmode specific configs as well as Hobbes-tests
* ui.content: contains sample content using the components from the ui.apps
* ui.tests: Java bundle containing JUnit tests that are executed server-side. This bundle is not to be deployed onto production.
* ui.launcher: contains glue code that deploys the ui.tests bundle (and dependent bundles) to the server and triggers the remote JUnit execution
* ui.frontend: an optional dedicated front-end build mechanism (Angular, React or general Webpack project)

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with

    mvn clean install -PautoInstallPackage

Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish

Or alternatively

    mvn clean install -PautoInstallPackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

## Testing

There are three levels of testing contained in the project:

* unit test in core: this show-cases classic unit testing of the code contained in the bundle. To test, execute:

    mvn clean test

* server-side integration tests: this allows to run unit-like tests in the AEM-environment, ie on the AEM server. To test, execute:

    mvn clean verify -PintegrationTests

* client-side Hobbes.js tests: JavaScript-based browser-side tests that verify browser-side behavior. To test:

    in the browser, open the page in 'Developer mode', open the left panel and switch to the 'Tests' tab and find the generated 'MyName Tests' and run them.

## ClientLibs

The frontend module is made available using an [AEM ClientLib](https://helpx.adobe.com/experience-manager/6-5/sites/developing/using/clientlibs.html). When executing the NPM build script, the app is built and the [`aem-clientlib-generator`](https://github.com/wcm-io-frontend/aem-clientlib-generator) package takes the resulting build output and transforms it into such a ClientLib.

A ClientLib will consist of the following files and directories:

- `css/`: CSS files which can be requested in the HTML
- `css.txt` (tells AEM the order and names of files in `css/` so they can be merged)
- `js/`: JavaScript files which can be requested in the HTML
- `js.txt` (tells AEM the order and names of files in `js/` so they can be merged
- `resources/`: Source maps, non-entrypoint code chunks (resulting from code splitting), static assets (e.g. icons), etc.

## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
