
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C0644696
 */
@WebServlet("/products")
public class products
        extends HttpServlet {

    /**
     * Provides GET /servlet and GET /servlet?id=XXX
     *
     * @param request - the request object
     * @param response - the response object
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Content-Type", "text/plain-text");
        try (PrintWriter out = response.getWriter()) {
            if (!request.getParameterNames().hasMoreElements()) {
                // There are no parameters at all
                out.println(getResults("SELECT * FROM PRODUCT"));
            } else {
                // There are some parameters
                int id = Integer.parseInt(request.getParameter("id"));
                out.println(getResults("SELECT * FROM PRODUCT WHERE Product_ID = ?", String.valueOf(id)));
            }
        } catch (IOException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Provides POST /servlet?name=XXX&age=XXX
     *
     * @param request - the request object
     * @param response - the response object
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("product_id") && keySet.contains("Name") && keySet.contains("Description") && keySet.contains("Quantity")) {
                // There are some parameters     
                String product_id = request.getParameter("product_id");
                String Name = request.getParameter("Name");
                String Description = request.getParameter("Description");
                String Quantity = request.getParameter("Quantity");

                doUpdate("INSERT INTO PRODUCT (Product_ID, Name, Description, Quantity) VALUES (?, ?, ?, ?)", product_id, Name, Description, Quantity);
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?name=XXX&age=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("product_id") && keySet.contains("Name") && keySet.contains("Description") && keySet.contains("Quantity")) {
                // There are some parameters  
                String product_id = request.getParameter("product_id");
                String Name = request.getParameter("Name");
                String Description = request.getParameter("Description");
                String Quantity = request.getParameter("Quantity");

                doUpdate("UPDATE PRODUCT SET Product_ID = ?, Name = ?, Description = ?, Quantity = ? WHERE PRODUCT_ID = ?", product_id, Name, Description, Quantity, product_id);
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?name=XXX&age=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("product_id")) {
                // There are some parameters                
                String product_id = request.getParameter("product_id");

                doUpdate("DELETE FROM PRODUCT WHERE Product_ID = ?", product_id);
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?name=XXX&age=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getResults(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(String.format("%s\t%s\t%s\t%s\n", rs.getInt("Product_ID"), rs.getString("Name"), rs.getString("Description"), rs.getInt("Quantity")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }
}
