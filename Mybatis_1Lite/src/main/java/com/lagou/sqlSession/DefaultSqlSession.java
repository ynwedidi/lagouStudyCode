package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

public class DefaultSqlSession implements  SqlSession{

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {

        List<Object> list = selectAll(statementId, params);

        if(list.size() == 1){
            return (T) list.get(0);
        }else {
            throw new RuntimeException("查询结果为空，或者返回值过多");
        }

    }

    @Override
    public <T> List<T> selectAll(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();

        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);

        List<Object> query = simpleExecutor.query(configuration, mappedStatement, params);

        return (List<T>) query;
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) throws Exception {

        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {

                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();

                String statementId = className + "." +methodName;

                if (methodName.toLowerCase().contains("insert")){
                    // 走insert 方法
                }

                if (methodName.toLowerCase().contains("delete")){
                    // 走delete方法
                }

                if (methodName.toLowerCase().contains("update")){
                    // 走update方法
                }

                Type genericReturnType = method.getGenericReturnType();

                if (genericReturnType instanceof ParameterizedType){
                    List<Object> objects = selectAll(statementId, args);
                    return objects;
                }

                return selectOne(statementId, args);
            }
        });


        return (T) proxyInstance;
    }
}
