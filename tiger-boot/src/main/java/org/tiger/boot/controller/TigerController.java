package org.tiger.boot.controller;

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

    @GetMapping("/number")
    public RestResponse<Integer> getConnectionNumber() {
        return RestResponse.createBySuccess(connectionServer.getConnectionManager().getConnectionNumber());
    }
}
