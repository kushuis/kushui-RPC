package com.kushui.rpc.client.generic;

public interface ServiceInvoker {

    Object $invoke(String interfaceClassName, String methodName, String[] parameterTypeNames, Object[] args, Boolean generic);

}
