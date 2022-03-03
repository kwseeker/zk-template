package top.kwseeker.zk.configcenter.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.lang.NonNull;

import java.util.Properties;

public class ExtendsPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private Properties props;

    @Override
    protected void processProperties(@NonNull ConfigurableListableBeanFactory beanFactory, @NonNull Properties props)
            throws BeansException {
        super.processProperties(beanFactory, props);
        this.props = props;
    }

    public Object getProperty(String key) {
        return props.get(key);
    }

    public void setProps(Properties props) {
        this.props = props;
    }
}
