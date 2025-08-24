# Fb2 parser

## Simple parser of book format fb2 in java objects

Using:

```java
  System.out.println("Started FB 2 read...");

  FictionBookFb2 fictionBookFb2 = new FictionBookFb2(new File("/file.xml"));

  System.out.println("Completed FB 2 read...");

  System.out.println(fictionBookFb2.getTitle());
  System.out.println(fictionBookFb2.getAuthors());

  String bookContent = fictionBookFb2.getContentAsString();

  System.out.println("Result length: " + bookContent.length());
  System.out.println("-------------------------");
  System.out.println(bookContent);
```
