<%--
  Created by IntelliJ IDEA.
  User: Venkatesh
  Date: 26-01-2017
  Time: 18:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>VV Search Engine - Register Advertisment</title>
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
<form id="reg-ad" action="RegisterAdvertisement" method="post">
    <nav class="navbar navbar-default">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="/index.html">
                    <img src="VSearch.png" style="width:120px;height:48px;margin-top:-14px;margin-left:-10px;">
                </a>
            </div>
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <h2 class="navbar-text">Register Advertisement</h2>
            </div>
        </div>
    </nav>
    <div class="container">
        <div class="panel panel-default" style="width: 800px;">
            <div class="panel-heading">
                <h3 class="panel-title">Register Ad</h3>
            </div>
            <div class="panel-body">
                <div class="row" style="margin: 15px;">
                    <div class="form-group">
                        <label class="col-md-3">Customer Name</label>
                        <div class="col-md-9  inputGroupContainer">
                            <div class="input-group"><span class="input-group-addon"><i
                                    class="glyphicon glyphicon-user"></i></span>
                                <input name="user_name" placeholder="Customer Name" class="form-control"
                                       type="text">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row" style="margin: 15px;">

                    <div class="form-group">
                        <label class="col-md-3">n grams</label>
                        <div class="col-md-9  inputGroupContainer">
                            <p style="color: #989090;font-size: 15px;font-family: arial,sans-serif;"><B>Note:</B> Set of n-grams must be comma seapared.<br/> <B>For example: </B>database course,informatik tu kl,dbislab</p>
                            <div class="input-group" style="width: 525px;">
                                <input name="n_grams" placeholder="n grams" class="form-control" type="text">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row" style="margin: 15px;">
                    <div class="form-group">
                        <label class="col-md-3">Url</label>
                        <div class="col-md-9 inputGroupContainer">
                            <div class="input-group" style="width: 525px;">
                                <input name="url" placeholder="Url" class="form-control" type="text">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row" style="margin: 15px;">
                    <div class="form-group">
                        <label class="col-md-3">Description</label>
                        <div class="col-md-9 inputGroupContainer">
                            <div class="input-group" style="width: 525px;">
                                <textarea class="form-control" name="description" placeholder="Description "></textarea>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row" style="margin: 15px;">
                    <div class="form-group">
                        <label class="col-md-3">Budget</label>
                        <div class="col-md-9  inputGroupContainer">
                            <div class="input-group"><span class="input-group-addon">$</span>
                                <input name="budget" placeholder="Budget" class="form-control" type="text">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row" style="margin: 15px;">
                    <div class="form-group">
                        <label class="col-md-3">Money per click</label>
                        <div class="col-md-9  inputGroupContainer">
                            <div class="input-group"><span class="input-group-addon">$</span>
                                <input name="money_per_click" placeholder="Money per click" class="form-control"
                                       type="text">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row" style="margin: 15px;">
                    <div class="form-group">
                        <label class="col-md-3">Image Url</label>
                        <div class="col-md-9 inputGroupContainer">
                            <div class="input-group" style="width: 525px;">
                                <input name="image_url" placeholder="Image Url" class="form-control" type="text">
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Button -->
                <div class="row" style="margin: 15px;">
                    <div class="form-group">
                        <label class="col-md-4"></label>
                        <div class="col-md-4">
                            <button type="submit" class="btn btn-primary">Register</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</form>
</body>
</html>
