# __summer__

## What is summer?

Web MVC framework

## Prerequisites

* java 1.8
* ant

## How to use the `summer` framework?

- Clone from the project from GitHub.

- Run `build.xml` in powershell. That will create `summer-framework.jar`.

- Add `summer-framework.jar` in the `lib/` folder of your working directory.
 
- Set `summer-framework.jar` as one of the libraries used in you working directory.

- Setup controllers' folder name in `web.xml` (Explained bellow).

And... That is it. Normally, after completing those steps, you can use `summer`
framework in any java project. Happy coding...😊😉.


## Controllers

### Setup Controllers' folder

- Create a folder for your controllers like `controllers_dir`.

- Add the name of `controllers_dir` in the `web.xml` file
of your project like the following:

```xml
  <context-param>
      <param-name>app.controllers.packageName</param-name>
      <param-value>controllers_dir</param-value>
  </context-param>
```

### How to create Controllers?

- Create the java class in the controllers' folder set above.

- Add the annotation `@Controller` of the package `src.summer.annotations` 
in the `summer-framework.jar` to your java class.


### Controller Methods

#### How to map a method in a Controller to listen to a URL?

- Make sure you have configured the controller folder in the `web.xml` file.

- Make sure your method is in a class annotated with `@Controller`.

- Annotate your method with `@UrlMapping(<myMapping>)`.`myMapping` is a string 
representing the URL you want to be listened to.

- Controller methods' possible return types are `String` or `ModelView`.

- Controller methods' parameters must be annotated with `@Param(<myParam>)`.

#### How to return a JSON?

- Annotate your method with `@RestApi`.

- The return type can be of any type.

## ModelViews: Where should I place them?

`ModelView`s must be placed in the root folder of the project.

## Form

### Binding form values in Controllers

- Add input names as parameters of controller methods. All parameters of the method must be
annotated with `@Param( name="<inputName>"" )`. Otherwise, there will be an exception.

- Dispatch the values to the `ModelView` using `ModelView.addObject()`. 

- Input values are cast automatically by the controller of summer. Otherwise, 
it will throw an Exception.

```html
<h2>Simple Form</h2>
<form action="myFormController" method="POST">
    <div>
        <label for="mail">Email: </label>
        <input type="email" name="maily" id="mail" required>
    </div>
    <div>
        <label for="pass">Login: </label>
        <input type="password" name="passy" id="pass" required>
    </div>
    <div>
        <input type="submit" value="Submit">
    </div>
</form>
```

```java
import src.summer.annotations.Param;

@Controller
public class FormController extends HttpServlet {
    @UrlMapping( urlMapping = "myFormController" )
    public ModelView okForm(
            @Param( name = "maily" ) String emailaka,
            @Param( name="passy" ) String passy 
    ) {
        ModelView mv = new ModelView( "/my-view.jsp", null );
        mv.addObject( "email", emailaka );
        mv.addObject( "passwork", passy );
        return mv;
    }
}
```

###### Supported types

`int`, `String`, `java.time.LocalDate`, `java.time.LocalDateTime`.


### Sending File through form

- Add attribute `enctype="multipart/form-data"` to `<form action="[actionMethod]"><form/>` tag.

- In the controller method, annotate the args with `@Param( name="file<abcd...>", isFile = true )`.

- You can save the file to a desired path from your controller method.

```html
<form action="fufu" method="POST" enctype="multipart/form-data">
    <input type="file" name="fileA" />
    <input type="file" name="fileB" />

    <input type="submit" value="Envoyer" />
</form>
```

```java
@Controller
public class FileController {
    @Post
    @UrlMapping( url = "fufu" )
    public String gererFichier(
            @Param( name = "fileA", isFile = true ) SummerFile myFile
    ) {
        // some processing...
        System.out.println( "FileName > " + fileA.getFileName() );
        System.out.println( "byte > " + fileA.getFileBytes().length );
        
        // save file
        String fileDirectory = "C:\\Users\\Henintsoa\\Documents\\_data";
        myFile.saveToFile(fileDirectory);

        return "File saved on the server. Path: " + fileDirectory + "\\" + myFile.getFileName();
    }
}
```


### Form Validation

- Create an entity that contains the fields of the form.

- Annotate fields that should be validated with annotations
  in `annotations.form.validation`.

- In the html page, bind the form to the entity.

- In the controller method that is called on the form action, annotate
  parameters that should be validated with `@Validate(errorPage)`. **Only annotated
  parameters will be validated.**

- **Till now, methods are redirect to the errorPage using *GET* method**

##### Validators:

- @Required()

- @Min(value)

- @Max(value)

- @IntRange(minValue, maxValue)

##### Validation errors:

- Validation errors are available in `request` object of the page specified in `@Validate(errorPage="...")`.

- There is a default method to show validation errors using bootstrap classes. Anyway, you can customize the
way you display the validation error in your page. Errors for a field are available as `List<String>` via 
`ValidationError.getErrors("myFieldName")`.

###### Example:

```java
    @Post
    @UrlMapping(url = "reservation_save")
    public ModelView save(
            @Validate(errorPage = "reservation_form") 
            @Param(name = "formData") ReservationFormData reservationFormData
    ) {
    // your controller method body...
    }
```

```html
    <%-- Date reservation --%>
    <div class="mb-3">
        <label class="form-label">Date Reservation</label>
        <input type="datetime-local"
               name="formData.date_reservation"
               class="form-control"
               value="<%= lastInput != null ? lastInput.getDate_reservation() : "" %>"/>
        <%
            if (lastInput != null) {
              Optional<ValidationError> vErr = vLog.getErrorByInput("formData.date_reservation");
              if (vErr.isPresent()) {
                out.print(vErr.get().toHtml());
              }
            }
        %>
    </div>

    <%-- Type Siege --%>
    <div class="mb-3">
        <label class="form-label">Type Siege: </label>
        <select name="formData.id_type_siege">
            <option value="">Choisir le siege</option>
            <%for (TypeSiege typeSiege : typeSieges) { %>
            <option
                    value="<%= typeSiege.getId()%>"
                    <%
                        if (lastInput != null) {
                            if (lastInput.getId_type_siege() == typeSiege.getId()) {
                                out.print("selected");
                            }
                        }
                    %>
            >
                <%= typeSiege.getNom() %>
            </option>
            <% } %>
            <%
                if (lastInput != null) {
                Optional<ValidationError> vErr = vLog.getErrorByInput("formData.date_reservation");
                  if (vErr.isPresent()) {
                    out.print(vErr.get().toHtml());
                  }
                }
            %>
        </select>
    </div>
```


## Session

- To use Session in a `@Controller` class, you need to add a field `SummerSession`
to your class.

- To use Session in a view, you need to dispatch the `SummerSession` of your
controller to your `ModelView`.

- Adding duplicate keys will throw an a `SummerSessionException` in your method.

```java
import jakarta.servlet.http.HttpServlet;
import src.summer.annotations.controller.Controller;
import src.summer.beans.ModelView;
import src.summer.beans.SummerSession;
import src.summer.exception.SummerSessionException;

@Controller
public class YourController extends HttpServlet {
    // Session injection
    SummerSession summerSession;

    @UrlMapping( urlMapping = "auth" )
    public ModelView authentication( @Param( name = "inp_login" ) String login,
                                     @Param( name = "inp_password" ) String password )
            throws SummerSessionException {
        HashMap<String, Object> map = new HashMap<>();
        map.put( "login", login );
        map.put( "password", password );
        
        // Store values
        summerSession.addAttribute( "login", login );
        summerSession.addAttribute( "password", password );
        
        // Retrieve values
        String d = summerSession.getAttribute( "myAttribute" );
        
        // Clear session
        summerSession.destroy();
        
        return new ModelView( "my-view.jsp", map );
    }
}
```

## Authorization

### Setup authorization variables names

- User's authorization data are stored in the 2 variables in the Session.

- Add variables' names in the `web.xml` file of your project like the following:

```xml
<context-param>
    <param-name>var_user_authenticated</param-name>
    <param-value>yourVariableName1</param-value>
</context-param>
<context-param>
    <param-name>var_user_role_level</param-name>
    <param-value>yourVariableName2</param-value>
</context-param>
```

### Authorize routes

- Annotate the methods to be authorized with `@Authorize( int level )` annotation.

```java
import jakarta.servlet.http.HttpServlet;
import src.summer.annotations.Authorized;
import src.summer.annotations.controller.Controller;
import src.summer.beans.ModelView;

@Controller
public class YourController extends HttpServlet {

    @Authorized( roleLevel = 2 ) // The user need to have a roleLevel >= 2
    @UrlMapping( urlMapping = "protected" )
    public ModelView someMethod() {
        return new ModelView( "my-view.jsp", null );
    }
}
```
