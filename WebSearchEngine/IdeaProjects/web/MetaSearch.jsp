<%@ page import="java.util.ArrayList" %>
<%@ page import="metaSearch.MetaSearchResult" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: Venkatesh
  Date: 04-02-2017
  Time: 02:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Meta Search Results</title>
    <script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
    <link rel="stylesheet" href="css/bootstrap.css">
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <script type="text/javascript" src="Search.js"></script>
    <script type="text/javascript" src="js/bootstrap.js"></script>
    <script type="text/javascript" src="js/bootstrap.min.js"></script>
</head>
<body>
<form action="MetaSearchServlet">
<%
    String query = (String) session.getAttribute("query");
%>
    <nav class="navbar navbar-default">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="/metaSearch.html">
                    <img src="MetaSearch.png" style="width:120px;height:48px;margin-top:-14px;margin-left:-10px;">
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
            </div>
        </div>
    </nav>
    <div class="container-fluid">
        <br>
        <%
        %>
        <div class="row">
            <div class="col-md-8 col-md-offset-1">
                <div id="search-results">
                    <%
                        List<MetaSearchResult> metaSearchResultList = new ArrayList<>();
                        metaSearchResultList = (List<MetaSearchResult>)session.getAttribute("metaSearchResultList");
                        for (int i = 0; i < metaSearchResultList.size() ; i++) {
                    %>
                    <div style="color: #340dab;font-size: 19px;font-family: arial,sans-serif;"><a
                            href="<%=metaSearchResultList.get(i).url%>"><%=metaSearchResultList.get(i).url%>
                    </a></div>
                    <div style="color: #006621;font-size: 14px;font-family: arial,sans-serif;">Rank: <%=i+1%>
                    </div>
                    <div style="color: #989090;font-size: 15px;font-family: arial,sans-serif;">
                        <B>Score: </B> <%=metaSearchResultList.get(i).mergedScore%>
                    </div>
                    <div style="color: #006621;font-size: 14px;font-family: arial,sans-serif;">IP Url: <%=metaSearchResultList.get(i).configUrl%>
                    </div>
                    <br>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>

    </div>
</form>
</body>
</html>
