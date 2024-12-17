
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
@WebServlet(name = "UpdateProfile", urlPatterns = {"/UpdateProfile"})
public class UpdateProfile extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        
       String firstName = req.getParameter("firstName");
       String lastName = req.getParameter("lastName");
       String mobile = req.getParameter("mobile");
        Part profileImage = req.getPart("profileImage");
        
        
        
        if(firstName.isEmpty()){
           responseJson.addProperty("message", "Please Fill Your First Name");
           
        }else if(lastName.isEmpty()){
           responseJson.addProperty("message", "Please Fill Your Last Name");
           
        }else{
        
        
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("mobile", mobile));
            
          User user = (User) criteria1.uniqueResult();
          
          user.setFirst_name(firstName);
          user.setLast_name(lastName);
          
          session.update(user);
          session.beginTransaction().commit();
          
          
           String applicationPath = req.getServletContext().getRealPath("");
                 String  newApplicationPath = applicationPath.replace("build"+File.separator +"web", "web");
                     
                      File imageFile = new File(newApplicationPath+"//ProfileImages//"+mobile+"//profile.jpg");
                      if(imageFile.exists()){
                           imageFile.delete();
                           
                            File newImagefile = new File(newApplicationPath+"//ProfileImages//"+mobile);
                            
                            try {
                                    InputStream inputStream1 = profileImage.getInputStream();
                                    File file1 = new File(newImagefile, "profile.jpg");
                                    Files.copy(inputStream1, file1.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    System.out.println("Image1 saved successfully.");
                                    
                                     responseJson.addProperty("success", true);
                                    responseJson.addProperty("message", "User updated successfully");
                                    
                                     responseJson.addProperty("user", gson.toJson(user));
                                    
                                } catch (IOException e) {
                                    System.err.println("Error saving image1: " + e.getMessage());
                                }
                           
                           
                           
                      }else{
                        responseJson.addProperty("message", "Failed to delete old profile image");
                      }
                     
        session.close();
        
        }
        
         
        
         resp.setContentType("application/json");
         resp.getWriter().write(gson.toJson(responseJson));
        
    }

    
    
    
  
}
