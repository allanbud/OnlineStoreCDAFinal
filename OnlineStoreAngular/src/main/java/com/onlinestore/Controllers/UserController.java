package com.onlinestore.Controllers;

import com.onlinestore.config.SecurityConfig;
import com.onlinestore.config.SecurityUtility;
import com.onlinestore.domain.User;
import com.onlinestore.domain.security.Role;
import com.onlinestore.domain.security.UserRole;
import com.onlinestore.service.MailContructorService;
import com.onlinestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;



@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailContructorService mailConstructor;

    @Autowired
    private JavaMailSender mailSender;

    @RequestMapping(value="/newUser", method= RequestMethod.POST)
    public ResponseEntity newUserPost(
            HttpServletRequest request,
            @RequestBody HashMap<String, String> mapper
    ) throws Exception {
        String username = mapper.get("username");
        String userEmail = mapper.get("email");

        if(userService.findByUsername(username) != null) {
            return new ResponseEntity("usernameExists", HttpStatus.BAD_REQUEST);
        }

        if(userService.findByEmail(userEmail) != null) {
            return new ResponseEntity("emailExists", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(userEmail);

        String password = SecurityUtility.randomPassword();

        String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
        user.setPassword(encryptedPassword);

        Role role = new Role();
        role.setRoleId(1); //id 1 = normal user
        role.setName("ROLE_USER");
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));
        userService.createUser(user, userRoles);

        SimpleMailMessage email = mailConstructor.constructNewUserEmail(user, password);
        mailSender.send(email);

        return new ResponseEntity("User Added!", HttpStatus.OK);

    }

    @RequestMapping(value="/forgetPassword", method=RequestMethod.POST)
    public ResponseEntity forgetPasswordPost(HttpServletRequest request, @RequestBody HashMap<String, String> mapper)
            throws Exception {


        /*.findByEmail by string "email" is wrong because as inspect page shows on line 87 there is an object NOT string!!!!
        also printout gives object format {"email":"some@some.com"} - NOT string
        To fix this issue copy @RequestBody HashMap<String, String> mapper from newUserPost method
         */
        User user = userService.findByEmail(mapper.get("email"));

        if(user == null) {
            return new ResponseEntity("Email not found", HttpStatus.BAD_REQUEST);
        }
        String password = SecurityUtility.randomPassword();

        String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
        user.setPassword(encryptedPassword);
        //SAVE to DB after generate and set

        //if modify or create DO NOT FORGET TO SAVE (in this case to DB :)))
        userService.save(user);

        SimpleMailMessage newEmail = mailConstructor.constructNewUserEmail(user, password);
        mailSender.send(newEmail);

        return new ResponseEntity("ForgetPassword Email sent!", HttpStatus.OK);

    }

    @RequestMapping(value="/updateUserInfo", method=RequestMethod.POST)
    public ResponseEntity profileInfo(
            @RequestBody HashMap<String, Object> mapper
    ) throws Exception{

        int id = (Integer) mapper.get("id");
        String email = (String) mapper.get("email");
        String username = (String) mapper.get("username");
        String firstName = (String) mapper.get("firstName");
        String lastName = (String) mapper.get("lastName");
        String newPassword = (String) mapper.get("newPassword");
        String currentPassword = (String) mapper.get("currentPassword");

        User currentUser = userService.findById(Long.valueOf(id));

        if(currentUser == null) {
            throw new Exception ("User not found");
        }

        if(userService.findByEmail(email) != null) {
            if(userService.findByEmail(email).getId() != currentUser.getId()) {
                return new ResponseEntity("Email not found!", HttpStatus.BAD_REQUEST);
            }
        }

        if(userService.findByUsername(username) != null) {
            if(userService.findByUsername(username).getId() != currentUser.getId()) {
                return new ResponseEntity("Username not found!", HttpStatus.BAD_REQUEST);
            }
        }

        SecurityConfig securityConfig = new SecurityConfig();

        if(newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")) {
            BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
            String dbPassword = currentUser.getPassword();
            if(currentPassword.equals(dbPassword)) {
                currentUser.setPassword(passwordEncoder.encode(newPassword));
            } else {
                return new ResponseEntity("Incorrect current password!", HttpStatus.BAD_REQUEST);
            }
        }

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setUsername(username);
        currentUser.setEmail(email);

        userService.save(currentUser);

        return new ResponseEntity("Update Success", HttpStatus.OK);
    }


    @RequestMapping("/getCurrentUser")
    //Java security: Principal gets current user logged in
    public User getCurrentUser(Principal principal) {
        String username = principal.getName();
        User user = new User();
        if (null != username) {
            user = userService.findByUsername(username);
        }

        return user;
    }


}