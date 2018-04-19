

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>VV Search Engine</title>
<script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
<script type="text/javascript" src="Search.js"></script>
</head>
<body>


<form action="SearchResult">
    <center><br><br><br>
        <img src="VSearch.png" style="width:600px;height:225px;"><br><br>
        <input size="50" name="query" style="width: 425px;height: 25px;box-shadow: 1px 1px 1px 1px #ccc;" type="text">
        <input type="submit"  title="submit" style="height: 30px;" value="Search">
        <br><br>
        <input id="set_parameters" type="button"title="Set Parameters" style="height: 30px;" value="Set Parameters"><br><br>
    </center>
    <div id="parameters_container">
        <input type="radio" name="score" value="tfidf" checked> TF IDF Score<br>
        <input type="radio" name="score" value="bm25"> BM 25 Score<br>
        <input type="radio" name="score" value="combined"> Combined Score<br><br>
        Max number of Results: <input name="set_parameters" id="set_parameters" style="width: 150px;height: 25px;box-shadow: 1px 1px 1px 1px #ccc;" type="text" value="20">
    </div>
</form>


</body>
</html>
