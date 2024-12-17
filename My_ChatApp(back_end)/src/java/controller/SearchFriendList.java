
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.User;
import java.io.File;
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


@WebServlet(name = "SearchFriendList", urlPatterns = {"/SearchFriendList"})
public class SearchFriendList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        
      String userId =  req.getParameter("userid");
      String searchText =  req.getParameter("searchText");
      
       
        
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
         responseJson.addProperty("nouser", false);
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        
       User user =(User) session.get(User.class, Integer.parseInt(userId));
       
      Criteria criteria1 = session.createCriteria(User.class);
      criteria1.add( Restrictions.ne("id", user.getId()));
      criteria1.add(Restrictions.ilike("first_name", searchText + "%"));
      
     List<User> friendsList = criteria1.list();
     
     if(friendsList.isEmpty()){
          responseJson.addProperty("nouser", true);
         System.out.println("no User found");
     }else{
     
          JsonArray jsonChatArray = new JsonArray();
          for(User otherUsers : friendsList){
     
          //get chat list
                Criteria criteria2 = session.createCriteria(Chat.class);
                criteria2.add(Restrictions.or(
                        Restrictions.and(
                                Restrictions.eq("from_user", user),
                                Restrictions.eq("to_user", otherUsers)
                        ),
                        Restrictions.and(
                                Restrictions.eq("from_user", otherUsers),
                                Restrictions.eq("to_user", user)
                        )
                ));
                
                  criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);

                //creat chat item to send frontend
                JsonObject chatItem = new JsonObject();
                chatItem.addProperty("otherUserId", otherUsers.getId());
                chatItem.addProperty("otherUserMobile", otherUsers.getMobile());
                chatItem.addProperty("otherUserName", otherUsers.getFirst_name() + " " + otherUsers.getLast_name());
                chatItem.addProperty("otherUserStatus", otherUsers.getUser_Status().getId());

                //check avatar image
                String applicationPath = req.getServletContext().getRealPath("");
                String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");
                File folder = new File(newApplicationPath + "//ProfileImages//" + otherUsers.getMobile());
                
                
                
                 if (folder.exists()) {
                    //avatar image found
                    chatItem.addProperty("avatarImageFound", true);
                } else {
                    //avatar image not found
                    chatItem.addProperty("avatarImageFound", false);
                    chatItem.addProperty("otherUserAvatarImage", otherUsers.getFirst_name().charAt(0) + "" + otherUsers.getLast_name().charAt(0));

                }

                //check new chat or not
                List<Chat> dbChatList = criteria2.list();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy, MM dd hh:ss a");

                if (dbChatList.isEmpty()) {
                    //no chat
                    chatItem.addProperty("message", "Let's Start new convercation.");
                    chatItem.addProperty("dateTime", dateFormat.format(user.getRegistered_date()));
//                    chatItem.addProperty("chatStatus_id", 1); //1-seen
                    chatItem.addProperty("otherUsermsg", "success");

                } else {
                    //found last chat
                      if(dbChatList.get(0).getFrom_user().getId()==user.getId()){
                        //user send msg to otheruser 
                        chatItem.addProperty("message", dbChatList.get(0).getMessage());
                    chatItem.addProperty("dateTime", dateFormat.format(dbChatList.get(0).getDate_time()));
                    chatItem.addProperty("chatStatus_id", dbChatList.get(0).getChat_status().getId());
                    
                    }else{
                       //otheruser send msg to user 
                        chatItem.addProperty("message", dbChatList.get(0).getMessage());
                       chatItem.addProperty("dateTime", dateFormat.format(dbChatList.get(0).getDate_time()));
                       chatItem.addProperty("otherUsermsg", "success");
                    }

                }
                
                 Criteria unseenCriteria = session.createCriteria(Chat.class);
               unseenCriteria.add(Restrictions.eq("from_user", otherUsers));
               unseenCriteria.add(Restrictions.eq("to_user", user));
               unseenCriteria.add(Restrictions.eq("chat_status.id", 2));
               
                if(unseenCriteria.list().isEmpty()){
                 chatItem.addProperty("unseenChat", false);
               }else{
                   chatItem.addProperty("unseenChat", true);
                   // Count unseen messages
               int unseenChatCount = unseenCriteria.list().size();
                chatItem.addProperty("unseenChatCount", unseenChatCount);
               }
                //get last convercation
                jsonChatArray.add(chatItem);
     
          }
          
          
          //send users
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Success");
            responseJson.addProperty("user", gson.toJson(user));
            responseJson.add("jsonChatArray", gson.toJsonTree(jsonChatArray));

            session.beginTransaction().commit();
         
            
             resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
     }
     
    
        
        
    }

   

    
    
    
}
