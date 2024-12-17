
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import entity.User_Status;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;


@WebServlet(name = "UpdateUserStatus", urlPatterns = {"/UpdateUserStatus"})
public class UpdateUserStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        responseJson.addProperty("message", "Unable to update status");
        
        try {
           String userId = req.getParameter("id");
           String statusId =  req.getParameter("statusId");
           
            System.out.println(userId);
            System.out.println(statusId);
           
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Get user by ID
            User user =(User) session.get(User.class, Integer.parseInt(userId));
            
             // Get the status
            User_Status userStatus =(User_Status) session.get(User_Status.class, Integer.parseInt(statusId));
            
            //Update User Status
            user.setUser_Status(userStatus);
            session.update(user);
            
            session.beginTransaction().commit();
            
             responseJson.addProperty("success", true);
        responseJson.addProperty("message", "User status updated successfully");
                    
                    
        } catch (Exception e) {
            e.printStackTrace();
        }
        
          resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
        
        
        
    }

    
    
  
}
