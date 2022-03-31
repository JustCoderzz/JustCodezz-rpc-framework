package com.lusir.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lusir
 * @date 2022/3/31 - 18:45
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hello implements Serializable {
    String message;
    String description;
}
