package com.dragon.study.springboot.thrift.client.route;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by dragon on 16/5/6.
 */
@Data
@EqualsAndHashCode(exclude = {"timeout"})
@ToString
public class Node {
  private String ip;
  private int port;
  private int timeout = 500;
}
