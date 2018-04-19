<%@page import="java.util.Map" %>
<%@page import="org.json.simple.JSONObject" %>
<%@page import="org.json.simple.JSONArray" %>
<%@page import="java.util.HashMap" %>
<%@page import="java.util.Iterator" %>
<%@ page import="advertisement.AdvertisementResult" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>

<%--
    Document   : Search
    Created on : 22 Nov, 2016, 2:20:14 AM
    Author     : Venkatesh
--%>


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
</head>
<body>
<form action="SearchResult">
    <%
        Boolean mistakeQuery = false;
        String correctQuery = "";
        int k = (Integer) session.getAttribute("k");
        int score = (Integer) session.getAttribute("score");
        String language = (String) session.getAttribute("language");
        HashMap<String, Integer> dictionarySuggession = (HashMap<String, Integer>) session.getAttribute("dictionarySuggession");
        JSONObject jsonObject = (JSONObject) session.getAttribute("jsonObject");
        JSONArray resultList = (JSONArray) jsonObject.get("resultList");
        JSONArray QueryInfo = (JSONArray) jsonObject.get("query");
        for (Map.Entry<String, Integer> sug : dictionarySuggession.entrySet()) {
            if (sug.getValue() > 0) {
                mistakeQuery = true;
            }
        }

    %>
    <% Iterator<JSONObject> queryInfoIterator = QueryInfo.iterator();
        while (queryInfoIterator.hasNext()) {
            JSONObject queryInfoObj = (JSONObject) queryInfoIterator.next();
            String query = (String) queryInfoObj.get("query");
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
                               value="<%=query.replaceAll("\"", "&quot;")%>">
                    </div>
                    <button type="submit" id="Search" class="btn btn-primary" title="Search" style="height: 33px;">
                        Search
                    </button>
                </div>
                <ul class="nav navbar-nav">
                    <li class="active" id="all-tab"><a href="#">All<span class="sr-only">(current)</span></a></li>
                    <li id="image-tab"><a href="#">Images<span class="sr-only">(current)</span></a></li>
                </ul>
            </div>
        </div>
    </nav>
    <div class="container-fluid">
        <input type="hidden" name="k" id="k" value="<%=k%>">
        <input type="hidden" name="score" id="score" value="<%=score%>">
        <input type="hidden" name="imageSearch" id="imageSearch" value="false">

        <br>
        <%
            }
            if (mistakeQuery) {
                for (Map.Entry<String, Integer> sug : dictionarySuggession.entrySet()) {
                    correctQuery = correctQuery + sug.getKey() + " ";
                }
        %>
        <div style="color: #337ab7; cursor: pointer;font-size: 20px;" id="doyoumean"
             onMouseOver="this.style.textDecoration='underline';" onmouseout="this.style.textDecoration='none';">Do you
            mean: <%=correctQuery%>
        </div>
        <br>

        <%
            }
        %>
        <div class="row">
            <div class="col-md-8 col-md-offset-1">
                <div id="search-results">
                    <%
                        Iterator<JSONObject> resultListIterator = resultList.iterator();
                        while (resultListIterator.hasNext()) {
                            JSONObject resultListObj = (JSONObject) resultListIterator.next();
                            String url = (String) resultListObj.get("url");
                            int rank = Integer.parseInt(resultListObj.get("rank").toString());
                            float tf_idf_score = Float.parseFloat(resultListObj.get("score").toString());
                            String snippet = (String) resultListObj.get("snippet");
                            String missing = (String) resultListObj.get("missing");
                            resultListObj.remove("snippet");
                            resultListObj.remove("missing");
                    %>
                    <div style="color: #340dab;font-size: 19px;font-family: arial,sans-serif;"><a
                            href="<%=url%>"><%=url%>
                    </a></div>
                    <div style="color: #989090;font-size: 15px;font-family: arial,sans-serif;"><%=snippet%>
                    </div>
                    <div style="color: #006621;font-size: 14px;font-family: arial,sans-serif;">Rank: <%=rank%>
                    </div>
                    <div style="color: #989090;font-size: 15px;font-family: arial,sans-serif;">
                        <B>Score: </B> <%=tf_idf_score%>
                    </div>
                    <%
                        if (missing != "") {
                    %>
                    <div style="color: #989090;font-size: 14px;font-family: arial,sans-serif;">Missing: <span
                            style="text-decoration: line-through;"><%=missing%></span>
                    </div>
                    <%
                        }
                    %>
                    <br>
                    <%
                        }
                    %>
                </div>
            </div>
            <%
                List<AdvertisementResult> advertisementResultList = (List<AdvertisementResult>) session.getAttribute("adResultList");
                if (advertisementResultList.size() > 0) {
            %>
            <div class="col-md-3">
                <div id="search-ads">
                    <%
                        for (int i = 0; i < advertisementResultList.size(); i++) {
                            if (i < 4) {
                    %>
                    <a style="color: #60086f; text-decoration: none;"
                       href="/AdvertisementClick?id=<%=advertisementResultList.get(i).adId%>&url=<%=advertisementResultList.get(i).url%>">
                        <div style="font-size: 20px;font-family: arial,sans-serif;font-weight: bold;">Advertisement
                        </div>
                        <div style="font-size: 16px;font-family: arial,sans-serif;">
                            <%=advertisementResultList.get(i).url%>
                        </div>
                        <%
                            if (advertisementResultList.get(i).imageUrl != null || advertisementResultList.get(i).imageUrl != "") {
                        %>
                        <img src="<%=advertisementResultList.get(i).imageUrl%>" style="width:225px;"/>
                        <%
                            }
                        %>
                        <div style="color: #989090;font-size: 15px;font-family: arial,sans-serif;"><%=advertisementResultList.get(i).description%>
                        </div>
                    </a>
                    <br/>
                    <%
                            }
                        }
                    %>
                </div>
            </div>
            <%
                }
            %>
        </div>

    </div>
</form>
</body>
</html>
