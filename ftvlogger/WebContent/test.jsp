<%@ page import="java.sql.*, javax.sql.*, javax.naming.*, java.util.*" %>

<sql:query var="rs" dataSource="jdbc/ftvdb">
select date from henergy
</sql:query>

<html>
  <head>
    <title>DB Test</title>
  </head>
  <body>

  <h2>Results</h2>

<c:forEach var="row" items="${rs.rows}">
    Foo ${row.date}<br/>
</c:forEach>

  </body>
</html>