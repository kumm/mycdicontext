package trial.mycontext;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

public class MyCdiExtension implements Extension {

    private MyContext myContext;

    public void registerContexts(
            @Observes final AfterBeanDiscovery afterBeanDiscovery,
            final BeanManager beanManager) {

        myContext = new MyContext(beanManager);
        afterBeanDiscovery.addContext(myContext);
    }

    public void initializeContexts(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        myContext.init(beanManager);
    }
}
