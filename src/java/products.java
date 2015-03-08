
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C0644696
 */
@Path("/products")
public class products {

    /**
     * Provides GET /servlet and GET /servlet?id=XXX
     *
     * @param request - the request object
     * @param response - the response object
     */
       @GET
    @Produces("application/json")
    public String doGet() {

        String res = getResults("SELECT * FROM PRODUCT");
        return res;

    }

    
    
    @GET
    @Path("{id}")
    @Produces("application/json")
    public String doGet(@PathParam("id") String id) {

        String res = getResults("SELECT * FROM PRODUCT where product_id = ?",id);
        return res;
    }
    /**
     * Provides POST /servlet?name=XXX&age=XXX
     *
     * @param request - the request object
     * @param response - the response object
     */
   
      @POST
    @Consumes("application/json")
    public void doPost(String str) {
        JsonParser parser = Json.createParser(new StringReader(str));
        Map<String, String> map = new HashMap<>();
        String name = "", value;
       
        while (parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                    name = parser.getString();
                    break;
                case VALUE_STRING:
                    
                    value = parser.getString();
                    map.put(name, value);
                    break;
                case VALUE_NUMBER:
                    value = Integer.toString(parser.getInt());
                    map.put(name, value);
                    break;
                    
            }
            
        }
        System.out.println(map);
        
            String names = map.get("name");
            String description = map.get("description");
            String quantity = map.get("quantity");
            

        
        doUpdate("INSERT INTO PRODUCT ( product_name, product_description, quantity) VALUES ( ?, ?, ?)", names, description, quantity);
    }
    
    
     @PUT
    @Path("{id}")
    @Consumes("application/json")
    public void doPut(@PathParam("id") String id, String str) {
        JsonParser parser = Json.createParser(new StringReader(str));
        Map<String, String> map = new HashMap<>();
        String name = "", value;
       
        while (parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                    name = parser.getString();
                    break;
                case VALUE_STRING:
                    value = parser.getString();
                    map.put(name, value);
                    break;
            }
        }
        System.out.println(map);
        
            
            String names = map.get("name");
            String description = map.get("description");
            String quantity = map.get("quantity");

                doUpdate("UPDATE PRODUCT SET product_id = ?, product_name = ?, product_description = ?, quantity = ? WHERE PRODUCT_ID = ?", id, names,description,quantity, id);
           
    }
    
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
