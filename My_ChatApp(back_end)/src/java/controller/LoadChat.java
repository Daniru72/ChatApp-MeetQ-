
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.Chat_Status;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "LoadChat", urlPatterns = {"/LoadChat"})
public class LoadChat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        Gson gson = new Gson();
        
       String logged_user_id = req.getParameter("logged_user");
       String other_user_id = req.getParameter("other_user");
       
        Session session = HibernateUtil.getSessionFactory().openSession();
        
       User loggedUser =(User) session.get(User.class, Integer.parseInt(logged_user_id));
       
      User otherUser =(User) session.get(User.class, Integer.parseInt(other_user_id));
      
      
        Criteria criteria1 = session.createCriteria(Chat.class);
        criteria1.add(
                Restrictions.or(
                     Restrictions.and(Restrictions.eq("from_user", loggedUser), Restrictions.eq("to_user", otherUser)),
                      Restrictions.and(Restrictions.eq("from_user", otherUser), Restrictions.eq("to_user", loggedUser))
                          ));
        
         //sort chats
           criteria1.addOrder(Order.asc("date_time"));
        
        //get chat list           
        List<Chat>chatList = criteria1.list();
        
        //create chat arry 
        JsonArray chatArray = new JsonArray();
        
        //get chat status -> seen
        Chat_Status chatStatus = (Chat_Status) session.get(Chat_Status.class, 1);
        
         //create date time formate
         SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd, hh:mm a");
        
        for(Chat chat : chatList){
        
            JsonObject chatObject = new JsonObject();
            chatObject.addProperty("message", chat.getMessage());
            chatObject.addProperty("date_time", dateformat.format(chat.getDate_time()));
            
            if(chat.getFrom_user().getId() == otherUser.getId()){
            
                chatObject.addProperty("side", "left");
                
                if(chat.getChat_status().getId()==2){
                   
                    chat.setChat_status(chatStatus);
                    session.update(chat);
                    
                }
                    
            
            }else{
            
                chatObject.addProperty("side", "right");
                chatObject.addProperty("status", chat.getChat_status().getId());
            
            }
            
            //add chat object into chat array
            chatArray.add(chatObject);
        
        }
        
        
         //update DB
          session.beginTransaction().commit();
          
           resp.setContentType("application/json");
         resp.getWriter().write(gson.toJson(chatArray));
        
        
    }

    
    
   
}
