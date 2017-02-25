package trial.mycontext;

import javax.annotation.PreDestroy;
import java.io.Serializable;

@MyScope
public class HelloBean implements Serializable {
    private String value = "hello";

    public String getValue() {
        return value;
    }

    @PreDestroy
    private void destroy() {
        System.out.println("++ HelloBean preDestroy ++");
    }
}
