
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        
       JsonObject requestJson = gson.fromJson(req.getReader(), JsonObject.class);
       String mobile = requestJson.get("mobile").getAsString();
       String password = requestJson.get("password").getAsString();
       
         if(mobile.isEmpty()){
          responseJson.addProperty("message", "Please Fill Your Number");
           
        }else if(!mobile.matches("^07[1,2,4,5,6,7,8,0][0-9]{7}$")){
          responseJson.addProperty("message", "Invalide Mobile Number");
          
        }else if(password.isEmpty()){
           responseJson.addProperty("message", "Please Fill Your Password");
           
        }else{
        
             Session session = HibernateUtil.getSessionFactory().openSession();
             
             Criteria criteria1 =  session.createCriteria(User.class);
             criteria1.add(Restrictions.eq("mobile", mobile));
             criteria1.add(Restrictions.eq("password", password));
             
             if(!criteria1.list().isEmpty()){
             //User Found
             
            User user =(User) criteria1.uniqueResult();
            
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Sign In Success");
            responseJson.add("user", gson.toJsonTree(user));
             
             }else{
            //User not found
             
             responseJson.addProperty("message", "Invalide Details");
             
             }
             
              session.close();
        
        
        }
        
         resp.setContentType("application/json");
         resp.getWriter().write(gson.toJson(responseJson));
         
    }

    
   
}
