package servlet;

import metaSearch.MetaSearchResult;
import queryProcessing.QueryElements;
import queryProcessing.QueryProcessor;
import metaSearch.MetaSearch;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Venkatesh on 02-02-2017.
 */
@WebServlet(name = "MetaSearchServlet")
public class MetaSearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            MetaSearch metaSearch = new MetaSearch();
            List<MetaSearchResult> metaSearchResultList = new ArrayList<>();
            QueryProcessor qp = new QueryProcessor();
            QueryElements queryElements = new QueryElements();
            String queryString = request.getParameter("query");
            queryString = queryString.replaceAll(",", "");
            String language = "en";
            queryElements = qp.processQuery(queryString, language);
            metaSearchResultList = metaSearch.getMetasearchResults(queryElements.unstemmedTerms);
            HttpSession session = request.getSession(true);
            session.setAttribute("metaSearchResultList", metaSearchResultList);
            session.setAttribute("query", queryElements.queryString);
            RequestDispatcher rd = null;
            rd = request.getRequestDispatcher("MetaSearch.jsp");
            rd.forward(request, response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
