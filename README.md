# __summer__
Creation of a java framework based on spring-framework.

## How to create Controllers?

- Create a folder for your controllers.
Like `controllers_dir`. You can add classes that ain't controller
in this folder.


- Add the name of `controllers_dir` in the `web.xml` file
of your project like the following:

```xml
  <context-param>
      <param-name>app.controllers.packageName</param-name>
      <param-value>controllers_dir</param-value>
  </context-param>
```

- When you create a new controller, add the annotation `Controller` in 
the package `src.summer.annotations` in the .jar of the framework.