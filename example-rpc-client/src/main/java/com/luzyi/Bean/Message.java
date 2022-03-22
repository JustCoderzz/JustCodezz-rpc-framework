package com.luzyi.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lusir
 * @date 2022/3/22 - 16:32
 **/
@Data
@AllArgsConstructor
public class Message implements Serializable {
    private  String content;
}
