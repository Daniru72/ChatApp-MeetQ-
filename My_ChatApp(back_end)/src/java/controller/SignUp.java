
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import entity.User_Status;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        
       String mobile = req.getParameter("mobile");
       String firstName = req.getParameter("firstName");
       String lastName = req.getParameter("lastName");
       String password = req.getParameter("password");
        Part prifileImage = req.getPart("profileImage");
        
        
        if(mobile.isEmpty()){
          responseJson.addProperty("message", "Please Fill Your Number");
          
        }else if(!mobile.matches("^07[1,2,4,5,6,7,8,0][0-9]{7}$")){
          responseJson.addProperty("message", "Please Fill Your Number");
          
        }else if(firstName.isEmpty()){
           responseJson.addProperty("message", "Please Fill Your First Name");
           
        }else if(lastName.isEmpty()){
           responseJson.addProperty("message", "Please Fill Your Last Name");
           
        }else if(password.isEmpty()){
           responseJson.addProperty("message", "Please Fill Your Password");
           
        }else if(password.length() < 5){
           responseJson.addProperty("message", "Please Fill Your Password");
           
        }else{
        
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            Criteria criteria1 =  session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("mobile", mobile));
            
            if(!criteria1.list().isEmpty()){
               //User already Registered
            
               responseJson.addProperty("message", "Mobile Number Already Used");
               
            }else{
              //New User
            
             User user = new User();;
             user.setFirst_name(firstName);
             user.setLast_name(lastName);
             user.setMobile(mobile);
             user.setPassword(password);
             user.setRegistered_date(new Date());
             
             //get user status 2 = Offline
            User_Status user_status =(User_Status) session.get(User_Status.class, 2);
            user.setUser_Status(user_status);
              
           
            
            if(prifileImage != null){
                
                 session.save(user);
            session.beginTransaction().commit();
            
                //image selected
                 String applicationPath = req.getServletContext().getRealPath("");
                 String  newApplicationPath = applicationPath.replace("build"+File.separator +"web", "web");
                      System.out.println("Image path: " + newApplicationPath);
                      File folder = new File(newApplicationPath+"//ProfileImages//"+mobile);
                              
                       folder.mkdir();
                       
                       try {
                                    InputStream inputStream1 = prifileImage.getInputStream();
                                    File file1 = new File(folder, "profile.jpg");
                                    Files.copy(inputStream1, file1.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    System.out.println("Image1 saved successfully.");
                                    
                                     responseJson.addProperty("success", true);
                                     responseJson.addProperty("message", "Registration Complete");
                                    
                                } catch (IOException e) {
                                    System.err.println("Error saving image1: " + e.getMessage());
                                }
            
            
            }else{
               
                responseJson.addProperty("message", "Please select Profile Image");
                
            }
            
            
            
              
            }
            
            session.close();
        
        
        
        }
        
         resp.setContentType("application/json");
         resp.getWriter().write(gson.toJson(responseJson));
        
        
    }

    
    
}
