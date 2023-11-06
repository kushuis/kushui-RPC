package com.kushui.rpc.common.codec;

import java.io.Serializable;

/**
 * RPC Request
 *
 * @author luxiaoxun
 */
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -2524587347775862771L;

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String version;

    //下面两个参数是泛化调用所需要的，因为无法获得具体的parameterTypes，只能获得其字符串
    private String[] parameterTypeNames;
    private Boolean generic = false;

    public String[] getParameterTypeNames() {
        return parameterTypeNames;
    }

    public void setParameterTypeNames(String[] parameterTypeNames) {
        this.parameterTypeNames = parameterTypeNames;
    }

    public Boolean getGeneric() {
        return generic;
    }

    public void setGeneric(Boolean generic) {
        this.generic = generic;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}