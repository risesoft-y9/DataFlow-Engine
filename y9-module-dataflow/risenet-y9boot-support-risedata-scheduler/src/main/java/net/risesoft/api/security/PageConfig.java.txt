package net.risesoft.api.security;

import java.util.List;

/**
 * @Description : 页面配置类
 * @ClassName PageConfig
 * @Author lb
 * @Date 2022/11/18 9:10
 * @Version 1.0
 */
public class PageConfig {
    private String url;
    private String name;
    private String icon;
    private List<PageConfig> children;

    public static PageConfig asPage(String url, String name, String icon, List<PageConfig> children) {
        PageConfig pageConfig = new PageConfig();
        pageConfig.setChildren(children);
        pageConfig.setIcon(icon);
        pageConfig.setUrl(url);
        pageConfig.setName(name);
        return pageConfig;
    }

    public static PageConfig asPage(String url, String name, String icon) {
        return asPage(url, name, icon, null);
    }

    public static PageConfig asPage(String url, String name) {
        return asPage(url, name, null, null);
    }

    @Override
    public String toString() {
        return "PageConfig{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", children=" + children +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<PageConfig> getChildren() {
        return children;
    }

    public void setChildren(List<PageConfig> children) {
        this.children = children;
    }
}
