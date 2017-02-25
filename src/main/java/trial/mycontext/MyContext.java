package trial.mycontext;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;
import java.lang.annotation.Annotation;

public class MyContext extends AbstractContext {

    private StorageRepository storageRepository;

    public MyContext(BeanManager beanManager) {
        super(beanManager);
    }

    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        return storageRepository.getStorage(createIfNotExist);
    }

    public void init(BeanManager beanManager) {
        System.out.println("++ context init ++");
        storageRepository = BeanProvider
                .getContextualReference(beanManager, StorageRepository.class, false);
    }


    public Class<? extends Annotation> getScope() {
        return MyScope.class;
    }

    public boolean isActive() {
        return true;
    }

    @SessionScoped
    public static class StorageRepository implements Serializable {

        @Inject
        private BeanManager beanManager;
        private ContextualStorage storage;


        public ContextualStorage getStorage(boolean createIfNotExist) {
            if (storage == null && createIfNotExist) {
                storage = new ContextualStorage(beanManager, true, true);
            }
            return storage;
        }

        @PreDestroy
        void destroy() {
            if (storage != null) {
                AbstractContext.destroyAllActive(storage);
            }
        }
    }

}
