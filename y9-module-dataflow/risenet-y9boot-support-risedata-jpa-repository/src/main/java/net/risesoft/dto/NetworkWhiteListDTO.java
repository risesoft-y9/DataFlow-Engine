package net.risesoft.dto;

import java.util.Date;

public class NetworkWhiteListDTO {

    private String id;
    
    private String description;
    
    private String ipMatch;

    private String service;

    private String environment;
    
    private Date createDate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpMatch() {
        return ipMatch;
    }

    public void setIpMatch(String ipMatch) {
        this.ipMatch = ipMatch;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

}
