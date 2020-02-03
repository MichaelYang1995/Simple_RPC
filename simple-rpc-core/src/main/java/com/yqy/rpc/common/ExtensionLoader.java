package com.yqy.rpc.common;

import com.sun.source.tree.WhileLoopTree;
import com.yqy.rpc.common.enumeration.ExceptionEnum;
import com.yqy.rpc.common.enumeration.support.ExtensionBaseType;
import com.yqy.rpc.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/**
 * @ClassName: ExtensionLoader
 * @Description: 简单扩展点加载工具类
 *               应用内的依赖注入使用枚举单例
 *               应用外的依赖注入使用Java的SPI机制
 * @Author: YangQingyuan
 * @Date: 2019/12/1 14:19
 * @Version: 1.0
 */
@Slf4j
public class ExtensionLoader {
    private static ExtensionLoader INSTANCE = new ExtensionLoader();    //单例

    public static ExtensionLoader getInstance(){
        return INSTANCE;
    }

    //保存已加载的实现类Map
    //<String:接口名, <String:实现类别名, Object:实现类实例>>
    private Map<String, Map<String, Object>> extensionMap = new HashMap<>();

    private ExtensionLoader(){}

    /**
     * 加载指定目录下的配置文件
     */
    public void loadResource(){
        URL parent = this.getClass().getClassLoader().getResource("/rpc");
        if (parent != null){
            //开始读取{/rpc}目录下的配置文件
            File dir = new File(parent.getFile());
            File[] files = dir.listFiles();
            for (File file:files){
                handleFile(file);
            }
            //配置文件读取完成
        }
    }

    /**
     * 处理配置文件
     * 将配置文件中的实现类实例化并注册到extensionMap中
     * @param file
     */
    private void handleFile(File file) {
        String interfaceName = file.getName();    //配置文件名为接口的全限定名
        try{
            Class<?> interfaceClass = Class.forName(interfaceName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null){
                String[] kv = line.split("=");
                if (kv.length != 2){
                    //log.info("配置文件格式错误");
                }
                //实例化实现类
                try {
                    Class<?> impl = Class.forName(kv[1]);
                    Object instance = impl.getDeclaredConstructor().newInstance();
                    registry(interfaceClass, kv[0], instance);
                }catch (Throwable t){
                    t.printStackTrace();
                    throw new RPCException(ExceptionEnum.EXTENSION_CONFIG_ERROR, "加载类或实例失败");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            throw new RPCException(ExceptionEnum.EXTENSION_CONFIG_ERROR, "读取配置文件失败");
        } catch (ClassNotFoundException e) {
            e.getCause();
            throw new RPCException(ExceptionEnum.EXTENSION_CONFIG_ERROR, "未找到待配置实例的接口");
        }
    }

    /**
     *
     * @param interfaceClass
     * @param enumType
     * @param type
     * @param <T>
     * @return
     */
    public <T> T load(Class<T> interfaceClass, Class enumType, String type){
        ExtensionBaseType<T> baseType = ExtensionBaseType.valueOf(enumType, type.toUpperCase());
        //应用内依赖
        if (baseType != null){
            return baseType.getInstance();
        }
        //应用外依赖
        if (!extensionMap.containsKey(interfaceClass)){
            throw new RPCException(ExceptionEnum.NO_SUPPORTED_INSTANCE, interfaceClass+"没有可用的实现类");
        }
        Object o = extensionMap.get(interfaceClass.getName()).get(type);
        if (o == null){
            throw new RPCException(ExceptionEnum.NO_SUPPORTED_INSTANCE, interfaceClass+"没有可用的实现类");
        }
        return interfaceClass.cast(o);
    }

    /**
     *
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public <T> List<T> load(Class<T> interfaceClass){
        if (!extensionMap.containsKey(interfaceClass.getName())){
            return Collections.emptyList();
        }
        Collection<Object> values = extensionMap.get(interfaceClass.getName()).values();
        List<T> instances = new ArrayList<>();
        values.forEach(value -> instances.add(interfaceClass.cast(value)));
        return instances;
    }

    /**
     *
     * @param interfaceClass
     * @param alias
     * @param instance
     */
    private void registry(Class<?> interfaceClass, String alias, Object instance){
        if (!extensionMap.containsKey(interfaceClass.getName())){
            extensionMap.put(interfaceClass.getName(), new HashMap<>());
        }
        extensionMap.get(interfaceClass.getName()).put(alias, instance);
    }
}
