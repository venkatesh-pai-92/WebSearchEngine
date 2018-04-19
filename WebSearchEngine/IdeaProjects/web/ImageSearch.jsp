<%@page import="org.json.simple.JSONObject" %>
<%@page import="org.json.simple.JSONArray" %>
<%@ page import="imageSearch.ImageResult" %>
<%@ page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>


<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Search Results</title>
    <script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
    <link rel="stylesheet" href="css/bootstrap.css">
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <script type="text/javascript" src="Search.js"></script>
    <script type="text/javascript" src="js/bootstrap.js"></script>
    <script type="text/javascript" src="js/bootstrap.min.js"></script>
    <style>
        .image-default {
            height: 225px;
            width: 320px;
            border: solid 1px green;
            padding: 3px;
            box-shadow: 2px 2px 2px 2px #ccc;
        }
    </style>
</head>
<body>
<form action="SearchResult">
    <%
        Boolean mistakeQuery = false;
        String correctQuery = "";
        int k = (Integer) session.getAttribute("k");
        int score = (Integer) session.getAttribute("score");
        String language = (String) session.getAttribute("language");
        String queryString = (String) session.getAttribute("queryString");
        HashMap<String, Integer> dictionarySuggession = (HashMap<String, Integer>) session.getAttribute("dictionarySuggession");

        for (Map.Entry<String, Integer> sug : dictionarySuggession.entrySet()) {
            if (sug.getValue() > 0) {
                mistakeQuery = true;
            }
        }
        List<ImageResult> imageResultList = new ArrayList<>();
        imageResultList = (List<ImageResult>) session.getAttribute("imageResultList");

    %>

    <input type="hidden" name="language" id="language" value="<%=language%>">
    <nav class="navbar navbar-default">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="/index.html">
                    <img src="VSearch.png" style="width:120px;height:48px;margin-top:-14px;margin-left:-10px;">
                </a>
            </div>
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <div class="navbar-form navbar-left">
                    <div class="form-group">
                        <input type="text" class="form-control" id="query" name="query"
                               style="width: 425px;height: 33px;box-shadow: 1px 1px 1px 1px #ccc;"
                               value="<%=queryString.replaceAll("\"", "&quot;")%>">
                    </div>
                    <button type="submit" id="Search" class="btn btn-primary" title="Search" style="height: 33px;">
                        Search
                    </button>
                </div>
                <ul class="nav navbar-nav">
                    <li id="all-tab"><a href="#">All<span class="sr-only">(current)</span></a></li>
                    <li class="active" id="image-tab"><a href="#">Images<span class="sr-only">(current)</span></a></li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><input type="button" id="show_json" class="btn btn-primary" title="Search"
                               style="height: 33px;margin-top: 7px;"
                               value="Show JSON"></li>
                </ul>
            </div>
        </div>
    </nav>
    <div style="width:80%;margin: 0 auto;">
        <input type="hidden" name="k" id="k" value="<%=k%>">
        <input type="hidden" name="score" id="score" value="<%=score%>">
        <input type="hidden" name="imageSearch" id="imageSearch" value="false">
        <br>


        <div id="search-results">
            <%
                for (int i = 0; i < imageResultList.size(); i++) {
                    if (i % 3 == 0) {
            %>
            <div class="row" style="margin-top: 10px;">
                <%
                    }
                %>
                <a href="<%=imageResultList.get(i).url%>">
                    <div class="col-md-4" score="<%=imageResultList.get(i).imageScore%>">
                        <img src="<%= "data:"+imageResultList.get(i).imageType+";base64,"+ imageResultList.get(i).image %>"
                             class="image-default"/>
                    </div>
                </a>
                <%
                    if (i % 3 == 2) {
                %>
            </div>
            <%
                    }

                }
            %>

        </div>
        <div id="search-json">

        </div>
    </div>
</form>
</body>
</html>

