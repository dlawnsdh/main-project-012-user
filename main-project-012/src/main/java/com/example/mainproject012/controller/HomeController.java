package com.example.mainproject012.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
//@RestController
@Controller
@RequestMapping("/")
public class HomeController {
  @GetMapping("/index")
  public Mono<ResponseEntity<String>> home() {
      return Mono.just(new ResponseEntity<>("인증에 성공했습니다!", HttpStatus.OK));
  }

  @GetMapping("/token")
  public Flux<String> token(@RequestParam String accessToken, @RequestParam String refreshToken) {
      return Flux.just(accessToken, refreshToken);
  }

}
