package com.qq.system.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(name = "oauth2-server")
public interface AuthService {

    @RequestMapping(method = RequestMethod.POST, value = "/oauth/token", headers = {"Content-Type=application/x-www-form-urlencoded"})
    Object postAccessToken(@RequestParam MultiValueMap<String, String> parameters);

}
