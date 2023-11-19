package com.baize.auth.entity;

import lombok.Data;

/**
 * 用户实体信息
 *
 * @author gemj
 * @since 2023/08/22 11:09
 */

@Data
public class User {
    private String username;
    private String password;
}
