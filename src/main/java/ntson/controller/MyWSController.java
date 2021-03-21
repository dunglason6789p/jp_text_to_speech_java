package ntson.controller;

import ntson.model.GreetingPersonalRequest;
import ntson.model.GreetingPersonalResponse;
import ntson.model.GreetingRequest;
import ntson.model.GreetingResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class MyWSController{
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingResponse greeting(GreetingRequest request) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new GreetingResponse("Hello " + HtmlUtils.htmlEscape(request.getName()) + "!");
    }

    @MessageMapping("/helloPersonal")
    public void greeting2(GreetingPersonalRequest request) throws Exception {
        Thread.sleep(1000); // simulated delay
        simpMessagingTemplate.convertAndSend("/dest/", new GreetingPersonalResponse());
    }
}
