package reptiles.paramcheck.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Auther: わらい
 * @Time: 2020/9/22 17:56
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext appCtx;

    /**
     * @param applicationContext
     *            ApplicationContext.
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appCtx = applicationContext;
    }

    private static ApplicationContext getApplicationContext() {
        return appCtx;
    }

    public static Object getBean(String beanName) {
        return appCtx.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }
}
