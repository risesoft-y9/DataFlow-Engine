package net.risedata.register.discover;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.StringUtils;

import net.risedata.register.service.IServiceInstance;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @Description :
 * @ClassName IServiceInstanceImpl
 * @Author lb
 * @Date 2021/11/26 10:22
 * @Version 1.0
 */
public class IServiceInstanceImpl implements ServiceInstance {

    private IServiceInstance iServiceInstance;

    private URI uri;

    private String scheme;

    public IServiceInstanceImpl(IServiceInstance iServiceInstance) {
        this.iServiceInstance = iServiceInstance;
        this.scheme  = iServiceInstance.isSecure()?"https":"http";
        try {

            this.uri= new URI(scheme+"://"+iServiceInstance.getHost()+":"+iServiceInstance.getPort()+"/"+ (iServiceInstance.getContext()==null?"":iServiceInstance.getContext()));
        } catch (URISyntaxException e) {
            DiscoveryManager.LOG.error(iServiceInstance +" create uri error"+e.getMessage());
        }
    }

    @Override
    public String getServiceId() {
        return iServiceInstance.getServiceId();
    }

    @Override
    public String getHost() {
        return iServiceInstance.getHost();
    }

    @Override
    public int getPort() {
        return iServiceInstance.getPort();
    }

    @Override
    public boolean isSecure() {
        return iServiceInstance.isSecure();
    }

    @Override
    public URI getUri() {
        return  uri;
    }

    @Override
    public Map<String, String> getMetadata() {
        return iServiceInstance.getMetadata();
    }

    @Override
    public String getInstanceId() {
        return iServiceInstance.getInstanceId();
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    public IServiceInstance getIServiceInstance() {
        return iServiceInstance;
    }

    public void setIServiceInstance(IServiceInstance iServiceInstance) {
        this.iServiceInstance = iServiceInstance;
        this.scheme  = iServiceInstance.isSecure()?"https":"http";
        try {
            this.uri= new URI(scheme+"://"+iServiceInstance.getHost()+":"+iServiceInstance.getPort()+ (StringUtils.isEmpty(this.iServiceInstance.getContext())?"":this.iServiceInstance.getContext()));
        } catch (URISyntaxException e) {
            DiscoveryManager.LOG.error(iServiceInstance +" create uri error"+e.getMessage());
        }
    }
}
