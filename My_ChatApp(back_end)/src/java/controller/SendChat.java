
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.Chat_Status;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;


@WebServlet(name = "SendChat", urlPatterns = {"/SendChat"})
public class SendChat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        
       String logged_user_id = req.getParameter("loggedUserId");
       String other_user_id = req.getParameter("OtherUserId");
       String message = req.getParameter("message");
       
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
       
        Session session = HibernateUtil.getSessionFactory().openSession();
        
       User loggedUser =(User) session.get(User.class, Integer.parseInt(logged_user_id));
       
       User otherUser = (User) session.get(User.class, Integer.parseInt(other_user_id));
       
       
        Chat chat = new Chat();
        
        //get chat status Unseen -> 2
       Chat_Status chatStatus = (Chat_Status) session.get(Chat_Status.class, 2);
        chat.setChat_status(chatStatus);
        chat.setDate_time(new Date());
        chat.setFrom_user(loggedUser);
        chat.setTo_user(otherUser);
        chat.setMessage(message);
        
        session.save(chat);
        
          try {
            
               session.beginTransaction().commit();
          responseJson.addProperty("success", true);
              
        } catch (Exception e) {
        }
         
          
          
           resp.setContentType("application/json");
         resp.getWriter().write(gson.toJson(responseJson));
        
        
    }

    
    
    
    
}
