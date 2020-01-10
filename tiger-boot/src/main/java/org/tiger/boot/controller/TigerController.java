package org.tiger.boot.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tiger.core.netty.server.connection.ConnectionServer;
import org.tiger.tools.common.RestResponse;

import javax.annotation.Resource;

/**
 * {@link TigerController}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 16:40 周三
 */
@RestController
@RequestMapping("/connection")
public class TigerController {

    @Resource
    private ConnectionServer connectionServer;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/number")
    public RestResponse<Integer> getConnectionNumber() {
        String json = stringRedisTemplate.opsForValue().get("ssp::area::findById::1");
        System.out.println(json);
        return RestResponse.createBySuccess(connectionServer.getConnectionManager().getConnectionNumber());
    }
}
