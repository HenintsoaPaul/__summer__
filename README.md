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

- Annotate your method with `@GetMapping(<myMapping>)` annotation of 
the package `src.summer.annotations` in the `summer-framework.jar`.
`myMapping` is a string representing the URL you want to be listened to.

- Controller method possible return types are `String` or `ModelView`.

## ModelViews: Where should I place them?

`ModelView`s must be placed in the root folder of the project.

## Compilation

During user's project compilation, the user must specify the parameter `-parameters`
when running `javac ...` command.

## Form

### Binding form values in Controllers

- Add input names as parameters of controller methods.

- The name of the parameter must match the input name. Or, annotate the method
with `@Param( name="<inputName>" )` to match it.

- After that, dispatch to values to the `ModelView` using `ModelView.addObject()`.
Input values are cast automatically by the controller of summer. Otherwise, it will
throw an Exception.

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
