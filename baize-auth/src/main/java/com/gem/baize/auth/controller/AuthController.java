package com.gem.baize.auth.controller;

import com.gem.baize.common.security.dto.LoginDTO;
import com.gem.baize.common.security.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/login")
    public ResponseEntity<LoginVO> login(@RequestBody LoginDTO request) {
        log.info("login request: {}", request.toString());
        // 1. 验证租户

        // 2. 验证用户

        // 3. 生成令牌

        // 4. 保存刷新令牌到Redis (用于后续验证)

        // 5. 更新用户最后登录时间

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // 1. 从请求头获取令牌

        // 2. 验证并解析令牌

        // 3. 将访问令牌加入黑名单(剩余有效期内不可用)

        // 4. 删除关联的刷新令牌

        // 5. 清除其他会话数据(如有)

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginVO> refresh(@RequestBody LoginVO request) {
        // 1. 验证刷新令牌

        // 2. 检查令牌是否在有效存储中

        // 3. 获取用户信息

        // 4. 生成新访问令牌

        // 5. (可选)生成新刷新令牌并替换旧的

        return ResponseEntity.ok().build();
    }


}
