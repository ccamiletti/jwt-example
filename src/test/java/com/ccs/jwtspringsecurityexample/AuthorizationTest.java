package com.ccs.jwtspringsecurityexample;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.ccs.jwtspringsecurityexample.controller.LoginRequest;
import com.ccs.jwtspringsecurityexample.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

public class AuthorizationTest extends AbstractMvcTest {
	
    @Autowired
    private UserRepository userRepository;

    private ObjectMapper mapper = new ObjectMapper();
    
	protected MockMvc mockMvc;

	private static Set<Class> inited = new HashSet<>();
    
    
    public void doInit() throws Exception {

    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }

    @Before
    public void init() throws Exception {
        if (!inited.contains(getClass())) {
            inited.add(getClass());
        }
    }

    @Test
    public void loginAndGetToken() throws Exception {

    	ResultActions userlogin = login("ccamiletti@gamail.com", "lkajdkjasdlkad");
    	final String token = extractToken(userlogin.andReturn());
    	
    	ResultActions islogin =  mockMvc.perform(get("/api/auth/isAuthenticate").header("Authorization", "Bearer " + token));
    	
    	String respo = islogin.andReturn().getResponse().getContentAsString();
    	
    	System.out.println(respo);
    	
    }

	protected ResultActions login(String username, String password) throws Exception {
        final LoginRequest auth = new LoginRequest();
        auth.setUsernameOrEmail(username);
        auth.setPassword(password);
        String jsonString = json(auth);
        return mockMvc.perform(
                post("/api/auth/signin")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON));
        
    }

    protected String extractToken(MvcResult result) throws UnsupportedEncodingException {
        return JsonPath.read(result.getResponse().getContentAsString(), "accessToken");
    }

    protected String json(Object o) throws IOException {
        return mapper.writeValueAsString(o);
    }


}
