package com.example.mainproject012.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


// 임시 컨트롤러
@Slf4j
@RestController
@RequestMapping("/")
public class HomeController {
  @GetMapping("/token")
  public Flux<String> token(@RequestParam String accessToken, @RequestParam String refreshToken) {
      return Flux.just(accessToken, refreshToken);
  }

}
