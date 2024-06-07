package net.risedata.rpc.provide.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risedata.rpc.provide.config.Application;
/**
* @description: 提供rpc 框架与springCloud整合所需要的网络请求内容
* @Author lb176
* @Date 2021/4/29==16:29
*/
@RestController
public class ProvideController {
   @Value("${rpc.port:8999}")
   private int port;
   @RequestMapping(Application.GET_PORT_URL)
   public int getPort(){
       return port;
   }

}
