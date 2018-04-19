<%--
  Created by IntelliJ IDEA.
  User: Venkatesh
  Date: 27-01-2017
  Time: 01:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>VV Search Engine - Advertisment Registration completed</title>
    <script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
    <link rel="stylesheet" href="css/bootstrap.css">
    <link rel="stylesheet" href="Search.css">
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <script type="text/javascript" src="Search.js"></script>
    <script type="text/javascript" src="js/bootstrap.js"></script>
    <script type="text/javascript" src="js/bootstrap.min.js"></script>
    <script type="text/javascript" src="js/bootstrapvalidator.min.js"></script>
</head>
<body>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="/index.html">
                <img src="VSearch.png" style="width:120px;height:48px;margin-top:-14px;margin-left:-10px;">
            </a>
        </div>
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <h2 class="navbar-text">Ad Registration Completed</h2>
        </div>
    </div>
</nav>
<div class="container">
    <h2 style="color: #42a542;">Advertisement registration completed. Thank you for Registering.</h2>
    <br/>
    <div style="color: #4e3a3a; cursor: pointer;font-size: 20px;" onMouseOver="this.style.textDecoration='underline';" onmouseout="this.style.textDecoration='none';" onClick="location.href='registerAdvertisement.jsp'">Register another advertisement</div>
</div>
</body>
</html>
