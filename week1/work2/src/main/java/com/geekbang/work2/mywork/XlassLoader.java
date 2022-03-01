package com.geekbang.work2.mywork;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class XlassLoader extends ClassLoader {
    public static void main(String[] args) throws Exception {
        final String className = "Hello";
        final String methodName = "hello";
        XlassLoader classLoader = new XlassLoader();
        Class<?> clazz = classLoader.findClass(className);
        for(Method m: clazz.getDeclaredMethods()){
            System.out.println(clazz.getSimpleName() + "." + m.getName());
        }
        // 创建对象
        Object instance = clazz.getDeclaredConstructor().newInstance();
        // 调用实例方法
        Method method = clazz.getMethod(methodName);
        method.invoke(instance);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //如果支持包名，则需要进行路径转换
        String resourcePath = name.replace(".", "/");
        String suffix = ".xlass";
        //获取输入流
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcePath + suffix);
        try{
            int length = in.available();
            byte[] bytes = new byte[length];
            in.read(bytes);
            byte[] classBytes = decode(bytes);
            return defineClass(name,classBytes,0,bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(in);
        }
        return null;
    }

    private void close(Closeable res) {
        if(null != res){
            try{
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] decode(byte[] bytes) {
        byte[] targetArray = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            targetArray[i] = (byte) (255 - bytes[i]);
        }
        return targetArray;
    }
}
