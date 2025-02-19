package com.voice.chatgpt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public Mono<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        return geminiService.getChatResponse(userMessage);
    }
}