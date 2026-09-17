package dynamicqr;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class WildFlyEntityManagerFactoryFix implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractEntityManagerFactoryBean entityManagerFactoryBean) {
            entityManagerFactoryBean.setEntityManagerFactoryInterface(EntityManagerFactory.class);
        }
        return bean;
    }
}
