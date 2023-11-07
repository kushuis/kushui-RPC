package com.kushui.rpc.client.generic;



public interface GenericService {

    /**
     * 泛化调用
     * @param methodName
     * @param parameterTypeNames
     * @param args
     * @return
     */
    Object $invoke(String methodName, String[] parameterTypeNames, Object[] args);
}
