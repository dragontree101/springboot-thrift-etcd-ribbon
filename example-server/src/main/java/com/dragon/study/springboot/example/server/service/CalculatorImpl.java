package com.dragon.study.springboot.example.server.service;


import com.dragon.study.springboot.autoconfigure.thrift.server.ThriftService;
import com.dragon.study.springboot.example.api.Calculator;
import com.dragon.study.springboot.example.api.InvalidOperation;
import com.dragon.study.springboot.example.api.SharedStruct;
import com.dragon.study.springboot.example.api.Work;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ThriftService
public class CalculatorImpl implements Calculator.Iface {

  private Map<Integer, SharedStruct> log;

  public CalculatorImpl() {
    log = new HashMap<>();
  }

  public int ping(Map<String, String> map) {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("ping()");
    return map.size();
  }

  public int add(int n1, int n2) {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("add(" + n1 + "," + n2 + ")");
    return n1 + n2;
  }

  public int calculate(int logid, Work work) throws InvalidOperation {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("calculate(" + logid + ", {" + work.op + "," + work.num1 + "," + work.num2 + "})");
    int val = 0;
    switch (work.op) {
      case ADD:
        val = work.num1 + work.num2;
        break;
      case SUBTRACT:
        val = work.num1 - work.num2;
        break;
      case MULTIPLY:
        val = work.num1 * work.num2;
        break;
      case DIVIDE:
        if (work.num2 == 0) {
          InvalidOperation io = new InvalidOperation();
          io.whatOp = work.op.getValue();
          io.why = "Cannot divide by 0";
          throw io;
        }
        val = work.num1 / work.num2;
        break;
      default:
        InvalidOperation io = new InvalidOperation();
        io.whatOp = work.op.getValue();
        io.why = "Unknown operation";
        throw io;
    }

    SharedStruct entry = new SharedStruct();
    entry.key = logid;
    entry.value = Integer.toString(val);
    log.put(logid, entry);

    return val;
  }

  public SharedStruct getStruct(int key) {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("getStruct(" + key + "), return null");
    return null;
  }

  public void zip() {
    try {
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("zip()");
  }

}
