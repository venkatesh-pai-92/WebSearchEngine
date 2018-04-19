<%@ page import="metaSearch.SearchEngine" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="metaSearch.ConfigureSearchEngine" %><%--
  Created by IntelliJ IDEA.
  User: Venkatesh
  Date: 25-01-2017
  Time: 13:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>VV Search Engine - MetaSearch</title>
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
<form id="form-searchEngine" action="ConfigureSearchEngine">
    <nav class="navbar navbar-default">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="/metaSearch.html">
                    <img src="MetaSearch.png" style="width:120px;height:48px;margin-top:-14px;margin-left:-10px;">
                </a>
            </div>
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <h2 class="navbar-text">Configure Search Engines</h2>
            </div>
        </div>
    </nav>
    <div class="container">
        <input type="hidden" name="action" id="action" value="add"/>
        <input type="hidden" name="delete-id" id="delete-id" value=""/>
        <div class="row" style="margin-top: 10px;margin-bottom: 10px;">
            <button type="button" class="btn btn-primary" id="btn-add-search-engine">Add Search Engine</button>
        </div>
        <div class="row form-inline" id="add-search-engine"
             style="margin-top:10px;margin-bottom: 10px;padding: 15px;border: solid 2px #ccc;">
            <div class="col-md-3">
                <div class="form-group">
                    <label>Group ID</label>
                    <input type="text" class="form-control" id="group-id" name="group-id" placeholder="Group ID">
                </div>
            </div>
            <div class="col-md-6">
                <div class="form-group">
                    <label>Config Url</label>
                    <input type="text" class="form-control" id="config-url" name="config-url" placeholder="Config Url"
                           style="width: 450px;">
                </div>
            </div>
            <div class="col-md-2">
                <div class="form-group">
                    <label>Active?</label>
                    <input type="checkbox" id="is-active" name="is-active" value="false">
                </div>
            </div>
            <div class="col-md-1">
                <button type="button" class="btn btn-primary" id="btn-add">Add</button>
            </div>
        </div>
        <%
            List<ConfigureSearchEngine> searchEngineList = new ArrayList<>();
            ConfigureSearchEngine searchEngine = new ConfigureSearchEngine();
            searchEngineList = searchEngine.getSearchEngines();
            if (searchEngineList.size() > 0) {
        %>
        <div class="row">
            <table class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th>Group ID</th>
                    <th>Config Url</th>
                    <th>isActive</th>
                    <th>Edit/Delete</th>
                </tr>
                </thead>
                <tbody>
                <%
                    for (int i = 0; i < searchEngineList.size(); i++) {

                %>
                <tr>
                    <td class="td-group-id"><%=searchEngineList.get(i).groupId%>
                    </td>
                    <td class="td-config-url"><%=searchEngineList.get(i).configUrl%>
                    </td>
                    <%
                        if (searchEngineList.get(i).isActive == true) {
                    %>
                    <td class="td-active">Active</td>
                    <%
                    } else {
                    %>
                    <td class="td-active">In-Active</td>
                    <%
                        }
                    %>
                    <td>
                        <button type="button" class="btn btn-default btn-edit-row" aria-label="Left Align">
                            <span class="glyphicon glyphicon-edit"></span>
                        </button>
                        <button type="button" class="btn btn-default btn-delete-row" aria-label="Left Align">
                            <span class="glyphicon glyphicon-floppy-remove"></span>
                        </button>
                    </td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
        <%
            }
        %>
    </div>
    <div class='popup' id='popup' style='display:none'>
        <div class="popup-div">
            <div class="row">
                <label>Are you sure you want to delete this entry?</label>
            </div>
            <div class="row">
                <button type="button" class="btn btn-primary" id='confirm' style="margin: 10px;">Yes</button>
                <button type="button" class="btn btn-primary" id='cancel' style="margin: 10px;">No</button>
            </div>
        </div>
    </div>
</form>
</body>
</html>
