# __summer__
Creation of a java framework based on spring-framework.


## How to use the `summer` framework?

- Clone from the project from GitHub.

- Run `build.ps1` in powershell. That will create `summer-framework.jar`.

- Add `summer-framework.jar` in the `lib/` folder of your working directory.
 
- Set `summer-framework.jar` as one of the libraries used in you working directory.

And... That is it. Normally, after completing those steps, you can use `summer`
framework in any java project. Happy coding...😊😉


## Controllers: How to create Controllers?

- Create a folder for your controllers like `controllers_dir`.

- Add the name of `controllers_dir` in the `web.xml` file
of your project like the following:

```xml
  <context-param>
      <param-name>app.controllers.packageName</param-name>
      <param-value>controllers_dir</param-value>
  </context-param>
```

- When you create a new controller, add the annotation `@Controller` of 
the package `src.summer.annotations` in the `summer-framework.jar`.


## Controller Methods: How to map a method in a Controller to listen to a URL?

- Make sure you have configured the controller folder in the `web.xml` file.

- Make sure your method is in a class annotated with `@Controller`.

- Annotate your method with `@GetMapping(<myMapping>)`.`myMapping` is a string 
representing the URL you want to be listened to.

- Controller methods' possible return types are `String` or `ModelView`.

- Controller methods' parameters must be annotated with `@Param(<myParam>)`.

## ModelViews: Where should I place them?

`ModelView`s must be placed in the root folder of the project.

## Compilation

During user's project compilation, the user must specify the parameter `-parameters`
when running `javac ...` command.

## Form

### Binding form values in Controllers

- Add input names as parameters of controller methods. All parameters of the method must be
annotated with `@Param( name="<inputName>"" )`. If it ain't the case, there will be an exception.

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
@Controller
public class FormController extends HttpServlet {
    @GetMapping( urlMapping = "myFormController" )
    public ModelView okForm( @Param( name = "maily" ) String emailaka, String passy ) {
        ModelView mv = new ModelView( "/my-view.jsp", null );
        mv.addObject( "email", emailaka );
        mv.addObject( "passwork", passy );
        return mv;
    }
}
```

### Supported types
`int`, `String`, `LocalDate`.

### Passing Objects

- Input Names must match object attributes names.

## Session

- To use Session in a `@Controller` class, you need to add a field `SummerSession`
to your class.

```java
import jakarta.servlet.http.HttpServlet;
import src.summer.annotations.Controller;
import src.summer.beans.SummerSession;

@Controller
public class YourController extends HttpServlet {
    SummerSession summerSession;

    // your methods ...
}
```

- To use Session in a view, you need to dispatch the `SummerSession` of your
controller to your `ModelView`.

```java
import jakarta.servlet.http.HttpServlet;
import src.summer.annotations.Controller;
import src.summer.beans.ModelView;
import src.summer.beans.SummerSession;
import src.summer.exception.SummerSessionException;

@Controller
public class YourController extends HttpServlet {
    SummerSession summerSession;

    @GetMapping( urlMapping = "auth" )
    public ModelView authentication( @Param( name = "inp_login" ) String login,
                                     @Param( name = "inp_password" ) String password )
            throws SummerSessionException {
        HashMap<String, Object> map = new HashMap<>();
        map.put( "login", login );
        return new ModelView( "my-view.jsp", map );
    }
}
```
