package com.yqy.rpc.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: User
 * @Description: 用户
 * @Author: YangQingyuan
 * @Data: 2020/02/03
 * @Version: V1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private String userName;

}
