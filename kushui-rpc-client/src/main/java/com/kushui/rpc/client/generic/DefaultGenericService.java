package com.kushui.rpc.client.generic;




public class DefaultGenericService implements GenericService {

    private ServiceInvoker serviceInvoker;

    private String interfaceClassName;

    public DefaultGenericService(ServiceInvoker serviceInvoker, String interfaceClassName) {
        this.serviceInvoker = serviceInvoker;
        this.interfaceClassName = interfaceClassName;
    }


    @Override
    public Object $invoke(String methodName, String[] parameterTypeNames, Object[] args) {
        return serviceInvoker.$invoke(interfaceClassName, methodName, parameterTypeNames, args, true);
    }


}
