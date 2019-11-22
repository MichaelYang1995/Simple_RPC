package com.yqy.rpc.registry.api;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


/**
 * @ClassName: ServiceURL
 * @Description: 服务URL
 *               服务是以URL的形式注册到注册中心的，该类代表一个服务URL，包含了一个远程服务的所有信息
 * @Author: YangQingyuan
 * @Data: 2019/11/16
 * @Version: V1.0
 **/
@Slf4j
@EqualsAndHashCode(of = {"serviceAdderss"})  //根据serviceAddress重写equals和hashCode方法(即“serviceAddress相等，则equals返回true”)
@ToString
public class ServiceURL {
    private String serviceAddress;  //服务地址

    private Map<Key, List<String>> params = new HashMap<>(); //配置服务参数列表

    public static ServiceURL DEFAULT_SERVICE_URL; //默认服务地址

    //默认URL服务地址为本机地址
    static {
        try{
            DEFAULT_SERVICE_URL = new ServiceURL(InetAddress.getLocalHost().getHostAddress());
        }catch (UnknownHostException e){
            e.printStackTrace();
        }
    }

    private ServiceURL(String serviceAddress){
        this.serviceAddress = serviceAddress;
    }

    public ServiceURL(){}

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getServiceAddress(){
        return serviceAddress;
    }

    public boolean containKey(Key key){
        return params.containsKey(key);
    }

    public List<String> getParamsByKey(Key key){
        return params.containsKey(key) ? params.get(key) : key.getDefaultParams();
    }

    /**
     * 将字符串数据解析成对应的ServiceURL对象
     * @param data
     * @return
     */
    public static ServiceURL parse(String data){
        String[] urlSlices = data.split("//?");
        ServiceURL serviceURL = new ServiceURL(urlSlices[0]);
        if (urlSlices.length > 1){
            String[] params = urlSlices[1].split("&");
            for (String param:params){
                String[] tempkv = param.split("=");
                String key = tempkv[0];
                Key keyEnum = Key.ValueOf(key.toUpperCase());
                if (keyEnum != null){
                    String[] values = tempkv[1].split(",");
                    serviceURL.params.put(keyEnum, Arrays.asList(values));
                }else {
                    log.error("key {} 不存在", key);
                }
            }
        }
        return serviceURL;
    }

    /**
     * 参数枚举类，目前只有一个参数:权重
     */
    public enum Key{
        WEIGHT(Arrays.asList("100"));
        private List<String> defaultValues;

        Key(){}

        Key(List<String> defaultValues){
            this.defaultValues = defaultValues;
        }

        public List<String> getDefaultValues(){
            return defaultValues != null ? defaultValues : Collections.EMPTY_LIST;
        }
    }
}
